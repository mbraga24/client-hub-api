package com.clienthub.demo;

import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerDataAccess;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerInMemoryRepository implements CustomerDataAccess {

    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();

        Customer john = new Customer(1L, "john_doe", "John", "Doe", 24, "john@email.com", "555-0001");
        Customer keyla = new Customer(2L, "keyla_smith", "Keyla", "Smith", 28, "keila@email.com", "555-0002");

        customers.add(john);
        customers.add(keyla);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public Long insertCustomer(Customer customer) {
        customers.add(customer);
        return customer.getId();
    }

    @Override
    public Optional<Customer> selectCustomerByPhoneNumber(String phoneNumber) {
        return customers.stream()
                .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByUsername(String username) {
        return customers.stream()
                .filter(c -> c.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Boolean existsPersonWithUsername(String username) {
        return customers.stream()
                .anyMatch(customer -> customer.getUsername().equals(username));
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonById(Long id) {
        return customers.stream()
                .anyMatch(customer -> customer.getId().equals(id));
    }

    @Override
    public void deleteCustomer(Long id) {
        customers.stream()
                .filter(customer -> !customer.getId().equals(id))
                .findFirst()
                .ifPresent(c -> customers.remove(c));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }

}
