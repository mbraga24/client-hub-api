package com.clienthub.customer.unit;

import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerJpaRepository;
import com.clienthub.customer.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/*
    Data Access Service Layer test
 */
class CustomerJpaRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerJpaRepository underTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        Mockito.verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        long customerId = -1;

        underTest.selectCustomerById(customerId);

        Mockito.verify(customerRepository)
                .findById(customerId);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                "John Doe",
                24,
                "john_doe@email.com"
        );

        underTest.insertCustomer(customer);

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "john_doe@email.com";

        underTest.existsPersonWithEmail(email);

        Mockito.verify(customerRepository)
                .existsCustomerByEmail(email);
    }

    @Test
    void existsPersonById() {
        long customerId = 1;

        underTest.existsPersonById(customerId);

        Mockito.verify(customerRepository)
                .existsCustomerById(customerId);
    }

    @Test
    void deleteCustomer() {
        long customerId = 1;

        underTest.deleteCustomer(customerId);

        Mockito.verify(customerRepository)
                .deleteById(customerId);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                "John Doe",
                24,
                "john_doe@email.com"
        );

        underTest.updateCustomer(customer);

        Mockito.verify(customerRepository)
                .save(customer);
    }
}
