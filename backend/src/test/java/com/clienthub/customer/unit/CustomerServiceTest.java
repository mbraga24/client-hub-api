package com.clienthub.customer.unit;

import com.clienthub.customer.*;
import com.clienthub.exception.DuplicatedResourceException;
import com.clienthub.exception.ResourceNotChangedException;
import com.clienthub.exception.ResourceNotFoundException;
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
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDataAccess customerDao;

    @InjectMocks
    private CustomerService underTest;

    private final Long appUserId = 1L;
    private final String username = "johndoe";
    private final String firstName = "John";
    private final String lastName = "Doe";
    private final Integer age = 23;
    private final String email = "john_doe@email.com";
    private final String phoneNumber = "555-0001";

    @Test
    void canGetAllCustomers() {
        underTest.getAllCustomers();

        Mockito.verify(customerDao)
                .selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        long customerId = 3;
        Customer customer = new Customer(appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        var actual = underTest.getCustomer(customerId);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        long customerId = 3;

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] does not exist".formatted(customerId));
    }

    @Test
    void canAddCustomer() {
        CustomerCreateRequest customerRequest =
                new CustomerCreateRequest(appUserId, username, firstName, lastName, email, age, phoneNumber);

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        Mockito.when(customerDao.insertCustomer(any())).thenReturn(1L);

        underTest.addCustomer(customerRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getFirstName()).isEqualTo(customerRequest.firstName());
        assertThat(capturedCustomer.getLastName()).isEqualTo(customerRequest.lastName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRequest.age());
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(customerRequest.phoneNumber());
    }

    @Test
    void willThrowExceptionIfEmailExists() {
        CustomerCreateRequest customerRequest =
                new CustomerCreateRequest(appUserId, username, firstName, lastName, email, age, phoneNumber);

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> underTest.addCustomer(customerRequest))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("Customer with email [%s] already exist.".formatted(email));

        Mockito.verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void canDeleteCustomerById() {
        long customerId = 1;

        Mockito.when(customerDao.existsPersonById(customerId))
                .thenReturn(true);

        underTest.deleteCustomer(customerId);

        Mockito.verify(customerDao)
                .deleteCustomer(customerId);
    }

    @Test
    void willThrowExceptionIfPersonIdIsNotFound() {
        long customerId = 10;

        Mockito.when(customerDao.existsPersonById(customerId))
                .thenReturn(false);

        assertThatThrownBy(() -> underTest.deleteCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] was not found.".formatted(customerId));

        Mockito.verify(customerDao, never()).deleteCustomer(any());
    }

    @Test
    void willUpdateAllCustomerProperties() {
        String newEmail = "john123@email.com";
        String newFirstName = "Johnny";
        String newLastName = "Dosonh";
        int newAge = 30;
        String newPhoneNumber = "555-1110";
        long customerId = 10;


        String newUsername = "updateduser";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newUsername,
                newFirstName,
                newLastName,
                newEmail,
                newAge,
                newPhoneNumber
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getFirstName()).isEqualTo(updateRequest.firstName());
        assertThat(capturedCustomer.getLastName()).isEqualTo(updateRequest.lastName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(updateRequest.phoneNumber());
    }

    @Test
    void willUpdateOnlyCustomerFirstName() {
        long customerId = 10;
        String newFirstName = "Johnny";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newFirstName, null, null, null, null
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getFirstName()).isEqualTo(newFirstName);
        assertThat(capturedCustomer.getLastName()).isEqualTo(lastName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void willUpdateOnlyCustomerEmail() {
        long customerId = 10;
        String newEmail = "newUpdated@email.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null, newEmail, null, null
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getFirstName()).isEqualTo(firstName);
        assertThat(capturedCustomer.getLastName()).isEqualTo(lastName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void willUpdateOnlyCustomerAge() {
        long customerId = 10;
        int newAge = 5;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null, null, newAge, null
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao)
                .updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getFirstName()).isEqualTo(firstName);
        assertThat(capturedCustomer.getLastName()).isEqualTo(lastName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(newAge);
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void updateWillThrowResourceNotChangeException() {
        long customerId = 10;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null, null, null, null
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(ResourceNotChangedException.class)
                .hasMessage("No data changes found");
    }

    @Test
    void updateWillThrowDuplicatedResourceException() {
        long customerId = 10;
        String existingEmail = "exists_" + email;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null, existingEmail, null, null
        );

        Customer customer = new Customer(customerId, appUserId, username, firstName, lastName, age, email, phoneNumber);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(existingEmail))
                .thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("Customer with email [%s] already exist."
                        .formatted(existingEmail));
    }
}
