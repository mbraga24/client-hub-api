package com.clienthub.customer;

import com.clienthub.exception.DuplicatedResourceException;
import com.clienthub.exception.ResourceNotChangedException;
import com.clienthub.exception.ResourceNotFoundException;
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
public class CustomerService {

    private final CustomerDataAccess customerDAO;

    public CustomerService(@Qualifier("jpa") CustomerDataAccess customerDAO) {
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
    public Long addCustomer(CustomerCreateRequest customerCreateRequest) {
        String email = customerCreateRequest.email();
        if (customerDAO.existsPersonWithEmail(email)) {
            log.error("addCustomer :: DUPLICATED EMAIL DETECTED: [{}]", email);
            throw new DuplicatedResourceException("Customer with email [%s] already exist.".formatted(email));
        }
        Customer customer = new Customer(
                customerCreateRequest.appUserId(),
                customerCreateRequest.username(),
                customerCreateRequest.firstName(),
                customerCreateRequest.lastName(),
                customerCreateRequest.age(),
                customerCreateRequest.email(),
                customerCreateRequest.phoneNumber()
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

        if (updateRequest.firstName() != null && !customer.getFirstName().equals(updateRequest.firstName())) {
            customer.setFirstName(updateRequest.firstName());
            changes = true;
        }

        if (updateRequest.lastName() != null && !customer.getLastName().equals(updateRequest.lastName())) {
            customer.setLastName(updateRequest.lastName());
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

        if (updateRequest.phoneNumber() != null && !customer.getPhoneNumber().equals(updateRequest.phoneNumber())) {
            customer.setPhoneNumber(updateRequest.phoneNumber());
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
