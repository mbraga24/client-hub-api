package com.clienthub.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDataAccess {

     List<Customer> selectAllCustomers();

     Optional<Customer> selectCustomerById(Long id);

     Optional<Customer> selectCustomerByPhoneNumber(String phoneNumber);

     Optional<Customer> selectCustomerByEmail(String email);

     Optional<Customer> selectCustomerByUsername(String username);

     Long insertCustomer(Customer customer);

     void deleteCustomer(Long id);

     void updateCustomer(Customer customer);

     Boolean existsPersonWithUsername(String username);

     boolean existsPersonWithEmail(String email);

     boolean existsPersonById(Long id);



}
