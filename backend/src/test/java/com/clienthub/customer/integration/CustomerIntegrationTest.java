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

    @Test
    void canRegisterACustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = faker.phoneNumber().cellPhone();
        CustomerCreateRequest request = new CustomerCreateRequest(
                firstName, lastName, email, age, phoneNumber
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

        Customer expectedCustomer = new Customer(
                null, null, firstName, lastName, age, email, phoneNumber
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "appUserId", "username")
                .contains(expectedCustomer);

        long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(customerId);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = faker.phoneNumber().cellPhone();
        CustomerCreateRequest request = new CustomerCreateRequest(
                firstName, lastName, email, age, phoneNumber
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
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String firstName = fakerName.firstName();
        String lastName = fakerName.lastName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        String phoneNumber = faker.phoneNumber().cellPhone();

        CustomerCreateRequest request = new CustomerCreateRequest(firstName, lastName, email, age, phoneNumber);

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

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                fakerName.firstName() + " Updated",
                fakerName.lastName() + " Updated",
                faker.internet().safeEmailAddress(),
                RANDOM.nextInt(18, 99),
                faker.phoneNumber().cellPhone());

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
