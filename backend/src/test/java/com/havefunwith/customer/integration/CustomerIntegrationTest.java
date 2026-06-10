package com.havefunwith.customer.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.havefunwith.customer.Customer;
import com.havefunwith.customer.CustomerRegistrationRequest;
import com.havefunwith.customer.CustomerUpdateRequest;
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

    /*
        Verifies the full customer registration flow end-to-end.
        POSTs a new customer, confirms it appears in the list of all customers,
        then fetches it by ID and asserts all attributes match.
    */
    @Test
    void canRegisterACustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
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

        // check all customers are present
        Customer expectedCustomer = new Customer(
                name, age, email
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // find customer id through unique email
        long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(customerId);

        // check if customer was added successfully
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

    /*
        Verifies the full customer deletion flow end-to-end.
        POSTs a new customer, retrieves its ID from the customer list,
        deletes it by ID, then confirms a subsequent fetch returns 404 Not Found.
    */
    @Test
    void canDeleteCustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
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

        // find customer id by email
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

    /*
        Verifies the full customer update flow end-to-end.
        POSTs a new customer, retrieves its ID, sends a PUT request with all
        updated fields, then fetches the customer by ID and asserts all
        attributes reflect the changes.
    */
    @Test
    void canUpdateCustomer() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "_" + UUID.randomUUID() + "@emailtesting.com";
        int age = RANDOM.nextInt(18, 99);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        // create customer
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
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

        // find customer id through email
        long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> customer.getId())
                .findFirst()
                .orElseThrow();

        // update customer properties
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                fakerName.fullName() + " Updated",
                fakerName.lastName() + "@updatedEmail.com",
                RANDOM.nextInt(18, 99));

        // update customer
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent();

        // expected customer
        Customer expectedCustomer = new Customer(
                updateRequest.name(),
                updateRequest.age(),
                updateRequest.email()
        );

        // add id to customer
        expectedCustomer.setId(customerId);

        // check if customer was updated successfully
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

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

}
