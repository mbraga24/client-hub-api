package com.clienthub.customer.unit;

import com.clienthub.AbstractTestcontainers;
import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeEach :: getBeanDefinitionCount() ===> " + applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        String phoneNumber = FAKER.phoneNumber().cellPhone();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                firstName,
                lastName,
                20,
                email,
                phoneNumber
        );
        underTest.save(customer);

        boolean actual = underTest.existsCustomerByEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        String email = "foo";

        boolean actual = underTest.existsCustomerByEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        String email = FAKER.internet().safeEmailAddress() + "." + UUID.randomUUID();
        String phoneNumber = FAKER.phoneNumber().cellPhone();
        Customer customer = new Customer(
                1L,
                "user_" + UUID.randomUUID(),
                firstName,
                lastName,
                20,
                email,
                phoneNumber
        );

        underTest.save(customer);

        long customerId = underTest.findAll().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var actual = underTest.existsCustomerById(customerId);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        long customerId = -1;

        var actual = underTest.existsCustomerById(customerId);

        assertThat(actual).isFalse();
    }
}
