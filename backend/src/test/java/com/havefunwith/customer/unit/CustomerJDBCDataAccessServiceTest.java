package com.havefunwith.customer.unit;

import com.havefunwith.AbstractTestcontainers;
import com.havefunwith.customer.Customer;
import com.havefunwith.customer.CustomerJDBCDataAccessService;
import com.havefunwith.customer.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
    Data Access Layer or DAO (Data Access Object) Layer test
 */
class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    // The row mapper is responsible for mapping rows of a result set to a
    // Customer object.
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    // The method runs before each test to setup the testing environment.
    @BeforeEach
    void setUp() {
        // Initializing the underTest object with a JdbcTemplate and a
        // customerRowMapper.
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Generate fake data and create a new customer object.
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                20,
                FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID()
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve all customers from the database.
        List<Customer> actualCustomers = underTest.selectAllCustomers();

        // Assert that list of customers is not empty.
        assertThat(actualCustomers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Generate fake customer data and create new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );
        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Select customer using the given ID.
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);

        // Assert that the customer is present
        // and their attributes are the same.
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Generate fake customer's ID.
        long customerId = 0;

        // Retrieve customer using the given ID.
        var actual = underTest.selectCustomerById(customerId);

        // Assert that the value is empty.
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Generate fake customer data and create new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Check if a customer with the specified email exists in the system.
        var actual = underTest.existsPersonWithEmail(email);

        // Assert that the value is true.
        assertThat(actual).isTrue();
    }

    @Test
    void deleteCustomer() {
        // Generate fake customer data and create a new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();
        // Delete customer using the given ID.
        underTest.deleteCustomer(customerId);

        // Retrieve customer using the given ID.
        var actual = underTest.selectCustomerById(customerId);

        // Assert that the customer is not present.
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Generate attributes to be updated.
        String newName = "John Doe";

        // Generate fake customer data and create a new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );
        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve customer's ID using the their email.
        var customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Create a payload for updating the customer with a modified name.
        Customer updateCustomer = new Customer();

        // Set the ID for the customer update payload .
        updateCustomer.setId(customerId);
        updateCustomer.setName(newName);

        // Update the customer's information in the database.
        underTest.updateCustomer(updateCustomer);

        // Retrieve the customer using the given ID.
        var actual = underTest.selectCustomerById(customerId);

        // Assert that only the name has changed,
        // while the other attributes remain the same.
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(newName); // new name
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Generate attributes to be updated.
        String newEmail = FAKER.internet().safeEmailAddress() + "_updated";

        // Generate fake customer data and create a new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Create a payload for updating the customer with a modified email.
        Customer updateCustomer = new Customer();

        // Set the ID for the customer update payload.
        updateCustomer.setId(customerId);
        updateCustomer.setEmail(newEmail);

        // Update the customer's information in the database.
        underTest.updateCustomer(updateCustomer);

        // Retrieve the customer using the given ID.
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        // Assert that only the email has changed,
        // while the other attributes remain the same.
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail); // new email
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerAge() {
        // Generate attributes to be updated.
        int newAge = 80;

        // Generate fake customer data and create new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Create a payload for updating the customer with a modified age value.
        Customer updateCustomer = new Customer();

        // Set the ID for the customer update payload.
        updateCustomer.setId(customerId);
        updateCustomer.setAge(newAge);

        // Update the customer's information in the database.
        underTest.updateCustomer(updateCustomer);

        // Retrieve the customer using the given ID.
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        // Assert that only the age value has been updated,
        // while other attributes remain unchanged.
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateAllCustomerProperties() {
        // Generate fake customer data and create new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Generate updated attributes.
        String newName = "Updated " + name;
        String newEmail = "updated-" + email;
        int newAge = 100;

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Create a new customer object with all new attributes.
        Customer updateCustomer = new Customer(
                customerId,
                newName,
                newAge,
                newEmail
        );

        // Update the customer's information in the database.
        underTest.updateCustomer(updateCustomer);

        // Retrieve the customer using the given ID.
        var actual = underTest.selectCustomerById(customerId);

        // Assert that all the values were changed after the update.
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(updateCustomer.getId());
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Add a new customer to the database.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        int age = 20;
        Customer customer = new Customer(
                name,
                age,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve the customer's ID using their email.
        long customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Create a payload for updating the customer without making any changes.
        Customer updateCustomer = new Customer();
        updateCustomer.setId(customerId);

        // Update the customer's information in the database.
        underTest.updateCustomer(updateCustomer);

        // Retrieve the customer using the given ID.
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        // Assert that the values remain unchanged after the update.
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existsPersonWithEmail() {
        // Generate fake customer data and create a new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Check if a customer with the specified email exists in the system.
        var actual = underTest.existsPersonWithEmail(email);

        // Assert that the value is true and customer exists.
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExist() {
        // Initialize an email that is not present in the database.
        String email = "non-existent-email@email.com";

        // Pass the email to the method of the mocked class.
        boolean actual = underTest.existsPersonWithEmail(email);

        // Assert that the email check returns false.
        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonById() {
        // Generate fake customer data and create a new customer object.
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                20,
                email
        );

        // Add a new customer to the database.
        underTest.insertCustomer(customer);

        // Retrieve customer's ID using their email.
        var customerId = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // Checks if a customer with given ID exists in the system.
        var actual = underTest.existsPersonById(customerId);

        // Assert that the value is true and the customer exists.
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenIdNotPresent() {
        // Create a fake ID variable.
        long customerId = -1;

        // Check if a customer with the specified ID exists.
        boolean actual = underTest.existsPersonById(customerId);

        // Assert that the ID check returns false.
        assertThat(actual).isFalse();
    }
}