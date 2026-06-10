package com.havefunwith.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

     List<Customer> selectAllCustomers();

     Optional<Customer> selectCustomerById(Long id);

     Long insertCustomer(Customer customer);

     void deleteCustomer(Long id);

     void updateCustomer(Customer customer);

     boolean existsPersonWithEmail(String email);

     boolean existsPersonById(Long id);

}
