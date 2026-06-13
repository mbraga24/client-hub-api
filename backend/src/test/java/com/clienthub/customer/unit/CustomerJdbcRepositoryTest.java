package com.clienthub.customer.unit;

import com.clienthub.AbstractTestcontainers;
import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerJdbcRepository;
import com.clienthub.customer.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
    Data Access Layer test
 */
class CustomerJdbcRepositoryTest extends AbstractTestcontainers {

    private CustomerJdbcRepository underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJdbcRepository(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                20,
                FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID()
        );

        underTest.insertCustomer(customer);

        List<Customer> actualCustomers = underTest.selectAllCustomers();

        assertThat(actualCustomers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );
        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        long customerId = 0;

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        var actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void deleteCustomer() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomer(customerId);

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        String newName = "John Doe";

        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );
        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setName(newName);

        underTest.updateCustomer(updateCustomer);

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        String newEmail = FAKER.internet().safeEmailAddress() + "_updated";

        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setEmail(newEmail);

        underTest.updateCustomer(updateCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerAge() {
        int newAge = 80;

        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setAge(newAge);

        underTest.updateCustomer(updateCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateAllCustomerProperties() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        String newName = "Updated " + name;
        String newEmail = "updated-" + email;
        int newAge = 100;

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer(
                customerId,
                newName,
                newAge,
                newEmail
        );

        underTest.updateCustomer(updateCustomer);

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        int age = 20;
        Customer customer = new Customer(
                name,
                age,
                email
        );

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);

        underTest.updateCustomer(updateCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existsPersonWithEmail() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        var actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExist() {
        String email = "non-existent-email@email.com";

        boolean actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonById() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var actual = underTest.existsPersonById(customerId);

        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenIdNotPresent() {
        long customerId = -1;

        boolean actual = underTest.existsPersonById(customerId);

        assertThat(actual).isFalse();
    }
}
