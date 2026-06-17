package com.clienthub.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    Data Access Service Layer
 */
@Slf4j
@Repository("jpa")
public class CustomerJpaRepository implements CustomerDataAccess {

    private final CustomerRepository customerRepository;

    public CustomerJpaRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> selectCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }
    
    @Override
    public Optional<Customer> selectCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> selectCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public Long insertCustomer(Customer customer) {
        log.info("insertCustomer :: saving customer with username [{}]", customer.getUsername());
        customerRepository.save(customer);
        log.info("insertCustomer :: customer saved with id [{}]", customer.getId());
        return customer.getId();
    }

    @Override
    public Boolean existsPersonWithUsername(String username) {
        return customerRepository.existsCustomerByUsername(username);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public boolean existsPersonById(Long id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("deleteCustomer :: deleting customer with id [{}]", id);
        customerRepository.deleteById(id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        log.info("updateCustomer :: updating customer with id [{}]", customer.getId());
        customerRepository.save(customer);
    }

}
