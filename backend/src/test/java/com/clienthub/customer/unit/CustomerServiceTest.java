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

    private final String name = "John Doe";
    private final Integer age = 23;
    private final String email = "john_doe@email.com";

    @Test
    void canGetAllCustomers() {
        underTest.getAllCustomers();

        Mockito.verify(customerDao)
                .selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        long customerId = 3;
        Customer customer = new Customer(name, age, email);

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
                new CustomerCreateRequest(name, email, age);

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        Mockito.when(customerDao.insertCustomer(any())).thenReturn(1L);

        underTest.addCustomer(customerRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRequest.age());
    }

    @Test
    void willThrowExceptionIfEmailExists() {
        CustomerCreateRequest customerRequest =
                new CustomerCreateRequest(name, email, age);

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
        String newName = "Johnny";
        int newAge = 30;
        long customerId = 10;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName,
                newEmail,
                newAge
        );

        Customer customer = new Customer(customerId, name, age, email);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void willUpdateOnlyCustomerName() {
        long customerId = 10;
        String newName = "Johnny";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        Customer customer = new Customer(customerId, name, age, email);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void willUpdateOnlyCustomerEmail() {
        long customerId = 10;
        String newEmail = "newUpdated@email.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        Customer customer = new Customer(name, age, email);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail))
                .thenReturn(false);

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void willUpdateOnlyCustomerAge() {
        long customerId = 10;
        int newAge = 5;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, newAge
        );

        Customer customer = new Customer(name, age, email);

        Mockito.when(customerDao.selectCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        underTest.updateCustomer(customerId, updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao)
                .updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void updateWillThrowResourceNotChangeException() {
        long customerId = 10;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, null);

        Customer customer = new Customer(name, age, email);

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
                null, existingEmail, null
        );

        Customer customer = new Customer(name, age, email);

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
