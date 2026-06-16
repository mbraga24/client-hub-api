package com.clienthub;

import com.github.javafaker.Faker;
import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        Random random = new Random();

        return args -> {
            String username = firstName.toLowerCase() + "." + lastName.toLowerCase() + "_" + UUID.randomUUID();
            Customer customer = new Customer(
                    random.nextLong(1, 10000),
                    username,
                    firstName,
                    lastName,
                    random.nextInt(18, 99),
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "_" + UUID.randomUUID() + "@email.com",
                    faker.phoneNumber().cellPhone());

            customerRepository.save(customer);
        };
    }
}
