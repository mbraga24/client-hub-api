package com.havefunwith.customer.unit;

import com.havefunwith.customer.*;
import com.havefunwith.exception.DuplicatedResourceException;
import com.havefunwith.exception.ResourceNotChangedException;
import com.havefunwith.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

/*
    Business Layer Test
 */
@ExtendWith(MockitoExtension.class) // another way of initializing mocks (no need to initialize, open, and close AutoCloseable).
class CustomerServiceTest {

    // Mock the DAO layer to isolate the service for testing.
    @Mock
    private CustomerDao customerDao;

    // Automatically inject mocks objects into the underTest object.
    @InjectMocks
    private CustomerService underTest;

    // Test data for mock customers.
    private final String name = "John Doe";
    private final Integer age = 23;
    private final String email = "john_doe@email.com";

    // Commented out manual initialization and injection of mocks as we're using @InjectMocks above.
    //    @BeforeEach
    //    void setUp() {
    //        underTest = new CustomerService(customerDao); // manually inject mocks into CustomerService
    //    }

    @Test
    void canGetAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        // Verify if the DAO's method was called when fetching all customers.
        Mockito.verify(customerDao)
                .selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        long customerId = 3;
        Customer customer = new Customer(name, age, email);

        // When
        // Mock the DAO's response to return test customer for the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // Then
        var actual = underTest.getCustomer(customerId);
        // Check if the returned customer matches our test customer.
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        long customerId = 3;

        // When
        // Mock the DAO's response to return an empty Optional for the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.empty());

        // Then
        // When given ID is passed to service under test method.
        // Assert a specific exception is thrown when trying to fetch a non-existent
        // customer.
        assertThatThrownBy(() -> underTest.getCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] does not exist".formatted(customerId));
    }

    @Test
    void canAddCustomer() {
        // Given
        // Create a customer registration request object.
        CustomerRegistrationRequest customerRequest =
                new CustomerRegistrationRequest(name, email, age);

        // When
        // Mock the DAO layer to return false when checking if email already exists
        // in the system.
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        Mockito.when(customerDao.insertCustomer(any())).thenReturn(1L);

        // Invoke the method of the service under test with the provided customer request object.
        underTest.addCustomer(customerRequest);

        // then
        // Use ArgumentCaptor to capture the customer object that the DAO's method receives.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // Verify that the DAO's method was called and capture its argument.
        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        // Retrieve the captured customer object from the ArgumentCaptor.
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // Assert that all attributes are the same to confirm customer was added.
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRequest.age());
    }

    @Test
    void willThrowExceptionIfEmailExists() {
        // Given
        // Create a customer registration request object.
        CustomerRegistrationRequest customerRequest =
                new CustomerRegistrationRequest(name, email, age);

        // When
        // Mock the DAO layer to return true when checking if email exists
        // in the system.
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        // Then
        // When given customer request object is passed to service under test method.
        // Assert that adding a customer with a duplicate email throws the appropriate exception
        // with the correct message.
        assertThatThrownBy(() -> underTest.addCustomer(customerRequest))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("Customer with email [%s] already exist.".formatted(email));

        // Verify that the DAO's method was never called.
        Mockito.verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void canDeleteCustomerById() {
        // Given
        long customerId = 1;

        // When
        // Mock DAO layer to return true if customer with given ID exists.
        Mockito.when(customerDao.existsPersonById(customerId))
                .thenReturn(true);

        // Invoke the method of the service under test with the provided
        // customer's ID.
        underTest.deleteCustomer(customerId);

        // Then
        // Verify that the DAO's method was called with the given customerId
        Mockito.verify(customerDao)
                .deleteCustomer(customerId);
    }

    @Test
    void willThrowExceptionIfPersonIdIsNotFound() {
        // Given
        long customerId = 10;

        // When
        // Mock DAO layer to return false if customer with given ID
        // does not exist.
        Mockito.when(customerDao.existsPersonById(customerId))
                .thenReturn(false);


        // Then
        // When given ID is passed to service under test method.
        // Assert that deleting a non-existing customer throws the expected exception
        // with the correct message.
        assertThatThrownBy(() -> underTest.deleteCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] was not found.".formatted(customerId));

        // Verify that the DAO method was never called.
        Mockito.verify(customerDao, never()).deleteCustomer(any());
    }

    @Test
    void willUpdateAllCustomerProperties() {
        // Given
        // Define test data.
        String newEmail = "john123@email.com";
        String newName = "Johnny";
        int newAge = 30;
        long customerId = 10;

        // Create an update request object that changes all attributes.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName,
                newEmail,
                newAge
        );

        // Create a mock customer object for the test.
        Customer customer = new Customer(customerId, name, age, email);

        // Mock DAO layer to return test customer when fetching with
        // the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // Mock DAO layer to return false when person with the given ID
        // does not exist.
        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        // When
        // Invoke the method of the service under test with the given ID and
        // update request object.
        underTest.updateCustomer(customerId, updateRequest);

        // Then
        // Use ArgumentCaptor to capture the customer object the DAO's method
        // receives.
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        // Verify that the DAO's method was called and capture its argument.
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Retrieve the captured customer object argument from ArgumentCaptor
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // Assert that all attributes were updated.
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void willUpdateOnlyCustomerName() {
        // Given
        // Define test data.
        long customerId = 10;
        String newName = "Johnny";

        // Create an update request object that only changes the name of the customer.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        // Create a mock customer object for the test.
        Customer customer = new Customer(customerId, name, age, email);

        // Mock the DAO layer to return test customer when fetching
        // with the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // When
        // Invoke method of the service under test with provided customer ID
        // and update request object.
        underTest.updateCustomer(customerId, updateRequest);

        // Then
        // Use ArgumentCaptor to capture the customer object that the DAO's
        // method receives.
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        // Verify that the DAO's method was called and capture its argument.
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Retrieve the captured customer object from the ArgumentCaptor.
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // Assert that only the name has been updated, and other attributes remain unchanged.
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void willUpdateOnlyCustomerEmail() {
        //  Given
        // Define test data.
        long customerId = 10;
        String newEmail = "newUpdated@email.com";

        // Create an update request object that only changes the email of the customer.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        // Create a mock customer object for the test.
        Customer customer = new Customer(name, age, email);

        // Mock DAO layer method to return a test customer when fetching
        // with the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // Mock DAO layer method to return false if person with the given
        // email does not exist.
        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        //  When
        // Invoke the service under test with the provided update request
        // object and the given customerId.
        underTest.updateCustomer(customerId, updateRequest);

        //  Then
        // Use ArgumentCaptor to capture the customer object that the DAO's
        // method receives
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        // Verify that the DAO's method was called and capture its argument.
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Retrieve the captured customer argument from ArgumentCaptor.
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // Assert that only the email has been updated, while other
        // attributes remain the same.
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void willUpdateOnlyCustomerAge() {
        //  Given
        // Define test data.
        long customerId = 10;
        int newAge = 5;

        // Generate customer request object updating only the age attribute.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, newAge
        );

        // Create a mock customer object for the test
        Customer customer = new Customer(name, age, email);

        // Mock DAO layer method to return a test customer when fetching
        // with given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        //  When
        // Invoke service under test with provided update request
        // object and the customerId.
        underTest.updateCustomer(customerId, updateRequest);

        //  Then
        // Use ArgumentCaptor to capture the customer object that the DAO's
        // method receives.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        // Verify that the DAO's method was called and capture argument.
        Mockito.verify(customerDao)
                .updateCustomer(customerArgumentCaptor.capture());

        // Retrieve the captured customer argument from ArgumentCaptor.
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // Assert that only the age has been updated, while the other attributes
        // remain the same.
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void updateWillThrowResourceNotChangeException() {
        //  Given
        // Define test data.
        long customerId = 10;

        // Create update request object with all null values.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, null);

        // Create mock customer object for the test.
        Customer customer = new Customer(name, age, email);

        // Mock DAO layer to return a test customer when fetching
        // with the given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        //  Then
        // When given ID and update request object are passed to service under test
        // method. Assert that when attributes from the update request are null or
        // unchanged throws the expected exception with the correct message.
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(ResourceNotChangedException.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateWillThrowDuplicatedResourceException() {
        //  Given
        // Define test data.
        long customerId = 10;
        String existingEmail = "exists_" + email;

        // Create update request object with only the existing email
        // attribute.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, existingEmail, null
        );

        // Create mock customer object for test.
        Customer customer = new Customer(name, age, email);

        // Mock DAO layer method to return a customer test when fetching
        // with given ID.
        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        // Mock DAO layer method to return false when person with the given
        // email exists.
        Mockito.when(customerDao.existsPersonWithEmail(existingEmail))
                .thenReturn(true);

        //  Then
        // When given ID and update request object are passed to service under test
        // method. Assert that when email already exist, throw the expected exception
        // with the correct message.
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("Customer with email [%s] already exist."
                        .formatted(existingEmail));
    }
}