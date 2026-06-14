package com.clienthub;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestcontainers {

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
        System.out.println();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("spring-rise-dao-unit-test")
                    .withUsername("marlon")
                    .withPassword("password");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url",
                () -> postgreSQLContainer.getJdbcUrl());
        dynamicPropertyRegistry.add("spring.datasource.username",
                postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password",
                postgreSQLContainer::getPassword);
    }

    private static DataSource getDataSource() {
        DataSourceBuilder builder = DataSourceBuilder
                .create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());
        return builder.build();
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected static Faker FAKER = new Faker();

}
