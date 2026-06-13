package com.clienthub.singleton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Product createProduct() {
        return new Product("Shoes", 50.0);
    }

}
