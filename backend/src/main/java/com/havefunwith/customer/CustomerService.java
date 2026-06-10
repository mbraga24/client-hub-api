package com.havefunwith.customer;

import com.havefunwith.exception.DuplicatedResourceException;
import com.havefunwith.exception.ResourceNotChangedException;
import com.havefunwith.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
    Business Layer
 */
@Slf4j
@Service
// @RequiredArgsConstructor // Lombok annotation to generate constructor for final fields
public class CustomerService {

    private final CustomerDao customerDAO;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        log.info("getAllCustomers :: FETCHING ALL CUSTOMERS");
        return customerDAO.selectAllCustomers();
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(Long id) {
        log.info("getCustomer :: FETCHING CUSTOMER WITH ID: [{}]", id);
        return customerDAO.selectCustomerById(id)
                .orElseThrow(() -> { 
                    log.warn("getCustomer :: CUSTOMER NOT FOUND WITH ID: [{}]", id);
                    return new ResourceNotFoundException(
                        "Customer with id [%s] does not exist".formatted(id)
                    );
                });
    }

    @Transactional
    public Long addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDAO.existsPersonWithEmail(email)) {
            log.error("addCustomer :: DUPLICATED EMAIL DETECTED: [{}]", email);
            throw new DuplicatedResourceException("Customer with email [%s] already exist.".formatted(email));
        }
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.email()
        );
        log.info("addCustomer :: ADDING NEW CUSTOMER: {}", customer);
        return customerDAO.insertCustomer(customer);  
    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("deleteCustomer :: DELETING CUSTOMER WITH ID: {}", customerId);
        if (!customerDAO.existsPersonById(customerId)) {
            log.warn("deleteCustomer :: CUSTOMER NOT FOUND WITH ID: {}", customerId);
            throw new ResourceNotFoundException(
                    "Customer with id [%s] was not found.".formatted(customerId)
            );
        }
        customerDAO.deleteCustomer(customerId);
    }

    @Transactional
    public void updateCustomer(Long customerId, CustomerUpdateRequest updateRequest) {
        log.info("updateCustomer :: UPDATING CUSTOMER WITH ID: [{}]", customerId);
        Customer customer = getCustomer(customerId);
        boolean changes = false;

        if (updateRequest.name() != null && !customer.getName().equals(updateRequest.name())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !customer.getAge().equals(updateRequest.age())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.email() != null && !customer.getEmail().equals(updateRequest.email())) {
            if (customerDAO.existsPersonWithEmail(updateRequest.email())) {
                log.error("updateCustomer :: DUPLICATED EMAIL DETECTED: [{}]", updateRequest.email());
                throw new DuplicatedResourceException(
                        "Customer with email [%s] already exist."
                                .formatted(updateRequest.email()));
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes) {
            log.warn("updateCustomer :: NO DATA CHANGES FOUND FOR CUSTOMER WITH ID: [{}]", customerId);
            throw new ResourceNotChangedException("No data changes found");
        }

        customerDAO.updateCustomer(customer);
        log.info("updateCustomer :: CUSTOMER WITH ID [{}] UPDATED SUCCESSFULLY", customerId);
    }

}
