package com.clienthub.customer.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerCreateRequest;
import com.clienthub.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String CUSTOMER_URI = "api/v1/customers";
    private final Random RANDOM = new Random();
    private final Faker FAKER = new Faker();

    @Test
    void canRegisterACustomer() {
        Name fakerName = FAKER.name();

        Long appUserId = RANDOM.nextLong(1, 10000);
        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase() + "_" + UUID.randomUUID();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = FAKER.phoneNumber().cellPhone();
        CustomerCreateRequest request = new CustomerCreateRequest(
                appUserId, username, firstName, lastName, email, age, phoneNumber
        );

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerCreateRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        // find all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

       long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        Customer expectedCustomer = new Customer(
                appUserId, username, firstName, lastName, age, email, phoneNumber
        );

        expectedCustomer.setId(customerId);
        expectedCustomer.setUsername(username);

        assertThat(allCustomers).contains(expectedCustomer);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .value(actual -> assertThat(actual)
                        .isEqualTo(expectedCustomer)
                );
    }

    @Test
    void canDeleteCustomer() {
        Name fakerName = FAKER.name();

        Long appUserId = RANDOM.nextLong(1, 10000);
        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase() + "_" + UUID.randomUUID();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = FAKER.phoneNumber().cellPhone();
        CustomerCreateRequest request = new CustomerCreateRequest(
                appUserId, username, firstName, lastName, email, age, phoneNumber
        );

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerCreateRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        // find all customer
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        // delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();

        // check if customer was deleted
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        Name fakerName = FAKER.name();

        Long appUserId = RANDOM.nextLong(1, 10000);
        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase() + "_" + UUID.randomUUID();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = FAKER.phoneNumber().cellPhone();

        CustomerCreateRequest request = new CustomerCreateRequest(appUserId, username, firstName, lastName, email, age, phoneNumber);

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerCreateRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        // find all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        String newUsername = "updated_" + UUID.randomUUID();
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newUsername,
                fakerName.firstName() + " Updated",
                fakerName.lastName() + " Updated",
                FAKER.internet().safeEmailAddress(),
                RANDOM.nextInt(18, 99),
                FAKER.phoneNumber().cellPhone());

        // update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent();

        Customer expectedCustomer = new Customer(
                null, null, updateRequest.firstName(), updateRequest.lastName(),
                updateRequest.age(), updateRequest.email(), updateRequest.phoneNumber()
        );

        expectedCustomer.setId(customerId);

        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(updatedCustomer)
                .usingRecursiveComparison()
                .ignoringFields("appUserId", "username")
                .isEqualTo(expectedCustomer);
    }

}
