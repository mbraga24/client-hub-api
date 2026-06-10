package com.havefunwith.customer.unit;

import com.havefunwith.customer.Customer;
import com.havefunwith.customer.CustomerJPADataAccessService;
import com.havefunwith.customer.CustomerRepository;
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
class CustomerJPADataAccessServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerJPADataAccessService underTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
//        underTest = new CustomerJPADataAccessService(customerRepository); // manually inject mocks into CustomerJPADataAccessService
    }

    // Closes all mocks after each test to release resources.
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    // Verifies that selectAllCustomers() delegates to repository.findAll().
    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        Mockito.verify(customerRepository)
                .findAll();
    }
    // Verifies that selectCustomerById() delegates to repository.findById() with the given ID.
    @Test
    void selectCustomerById() {
        long customerId = -1;

        underTest.selectCustomerById(customerId);

        Mockito.verify(customerRepository)
                .findById(customerId);
    }
    // Verifies that insertCustomer() delegates to repository.save() with the given customer.
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
    // Verifies that existsPersonWithEmail() delegates to repository.existsCustomerByEmail() with the given email.
    @Test
    void existsPersonWithEmail() {
        String email = "john_doe@email.com";

        underTest.existsPersonWithEmail(email);

        Mockito.verify(customerRepository)
                .existsCustomerByEmail(email);
    }
    // Verifies that existsPersonById() delegates to repository.existsCustomerById() with the given ID.
    @Test
    void existsPersonById() {
        long customerId = 1;

        underTest.existsPersonById(customerId);

        Mockito.verify(customerRepository)
                .existsCustomerById(customerId);
    }
    // Verifies that deleteCustomer() delegates to repository.deleteById() with the given ID.
    @Test
    void deleteCustomer() {
        long customerId = 1;

        underTest.deleteCustomer(customerId);

        Mockito.verify(customerRepository)
                .deleteById(customerId);
    }
    // Verifies that updateCustomer() delegates to repository.save() with the given customer.
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