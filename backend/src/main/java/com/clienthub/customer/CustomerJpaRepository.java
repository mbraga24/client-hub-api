package com.clienthub.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    Data Access Service Layer
 */
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
        customerRepository.save(customer);
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
        customerRepository.deleteById(id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

}
