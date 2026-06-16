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
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID(),
                FAKER.phoneNumber().cellPhone()
        );

        underTest.insertCustomer(customer);

        List<Customer> actualCustomers = underTest.selectAllCustomers();

        assertThat(actualCustomers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
            assertThat(c.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(c.getLastName()).isEqualTo(customer.getLastName());
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
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
        );

        underTest.insertCustomer(customer);

        var actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void deleteCustomer() {
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
    void updateCustomerFirstName() {
        String newFirstName = "Johnny";

        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
        );
        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);
        updateCustomer.setFirstName(newFirstName);

        underTest.updateCustomer(updateCustomer);

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getFirstName()).isEqualTo(newFirstName);
            assertThat(c.getLastName()).isEqualTo(customer.getLastName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        String newEmail = FAKER.internet().safeEmailAddress() + "_updated";

        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
            assertThat(c.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(c.getLastName()).isEqualTo(customer.getLastName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        int newAge = 80;

        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
            assertThat(c.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(c.getLastName()).isEqualTo(customer.getLastName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateAllCustomerProperties() {
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
        );

        String newFirstName = "UpdatedFirst";
        String newLastName = "UpdatedLast";
        String newEmail = "updated-" + email;
        int newAge = 100;
        String newPhone = "555-9999";

        underTest.insertCustomer(customer);

        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Customer updateCustomer = new Customer(
                customerId,
                null,
                null,
                newFirstName,
                newLastName,
                newAge,
                newEmail,
                newPhone
        );

        underTest.updateCustomer(updateCustomer);

        var actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getFirstName()).isEqualTo(newFirstName);
            assertThat(c.getLastName()).isEqualTo(newLastName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getPhoneNumber()).isEqualTo(newPhone);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
            assertThat(c.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(c.getLastName()).isEqualTo(customer.getLastName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existsPersonWithEmail() {
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                20,
                email,
                FAKER.phoneNumber().cellPhone()
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
