package com.clienthub;

import com.github.javafaker.Faker;
import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

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
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    random.nextInt(18, 99),
                    firstName + "." + lastName + "@email.com");

            customerRepository.save(customer);
        };
    }
}
