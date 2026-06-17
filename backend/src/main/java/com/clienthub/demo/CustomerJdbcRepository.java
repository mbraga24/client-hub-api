package com.clienthub.demo;

import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerDataAccess;
import com.clienthub.customer.CustomerRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
    Data Access Layer
 */
@Slf4j
@Repository("jdbc")
public class CustomerJdbcRepository implements CustomerDataAccess {

    private final JdbcTemplate jdbcTemplate;
    private final @NonNull CustomerRowMapper customerRowMapper;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        log.info("selectAllCustomers :: fetching all customers");
        var sql = """
                SELECT id, app_user_id, username, first_name, last_name, email, age, phone_number 
                FROM customer;
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        log.info("selectCustomerById :: fetching customer with id: [{}]", id);
        var sql = """
                SELECT id, app_user_id, username, first_name, last_name, email, age, phone_number
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByPhoneNumber(String phoneNumber) {
        log.info("selectCustomerByPhoneNumber :: checking if customer with phone number [{}] exists", phoneNumber);
        var sql = """
                SELECT id, app_user_id, username, first_name, last_name, email, age, phone_number
                FROM customer
                WHERE phone_number = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, phoneNumber)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByEmail(String email) {
        log.info("selectCustomerByEmail :: checking if customer with email [{}] exists", email);
        var sql = """
                SELECT id, app_user_id, username, first_name, last_name, email, age, phone_number
                FROM customer
                WHERE email = ?    
                """;
        return jdbcTemplate.query(sql, customerRowMapper, email)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByUsername(String username) {
        log.info("selectCustomerByUsername :: checking if customer with username [{}] exists", username);
        var sql = """
                SELECT id, app_user_id, username, first_name, last_name, email, age, phone_number
                FROM customer
                WHERE username = ?        
                """;
        return jdbcTemplate.query(sql, customerRowMapper, username)
                .stream()
                .findFirst();
    }

    @Override
    public Long insertCustomer(Customer customer) {
        log.info("insertCustomer :: inserting customer with username [{}]", customer.getUsername());
        var sql = """
                INSERT INTO customer (app_user_id, username, first_name, last_name, email, age, phone_number)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        Long customerId = jdbcTemplate.queryForObject(sql, Long.class,
                customer.getAppUserId(),
                customer.getUsername(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getPhoneNumber()
        );
        log.info("insertCustomer :: customer inserted with id [{}]", customerId);
        return customerId;
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("deleteCustomer :: deleting customer with id [{}]", id);
        var sql = """
                DELETE 
                FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, id);
        log.info("deleteCustomer :: customer deleted, [{}] row affected", result);
    }

    @Override
    public void updateCustomer(Customer customer) {
        log.info("updateCustomer :: updating customer with id [{}]", customer.getId());
        var sql = "";

        if (customer.getUsername() != null) {
                sql = """
                        UPDATE customer
                        SET username = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(sql, customer.getUsername(), customer.getId());
        }

        if (customer.getEmail() != null) {
                sql = """
                        UPDATE customer
                        SET email = ?
                        WHERE id = ? 
                        """;
                jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
        }

        if (customer.getFirstName() != null) {
                sql = """
                        UPDATE customer 
                        SET first_name = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(sql, customer.getFirstName(), customer.getId());
        }

        if (customer.getLastName() != null) {
                sql = """
                        UPDATE customer 
                        SET last_name = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(sql, customer.getLastName(), customer.getId());
        }

        if (customer.getPhoneNumber() != null) {
                sql = """
                        UPDATE customer
                        SET phone_number = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(sql, customer.getPhoneNumber(), customer.getId());
        }

        if (customer.getAge() != null) {
                sql = """
                        UPDATE customer
                        SET age = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(sql, customer.getAge(), customer.getId());
        }

        log.info("updateCustomer :: customer with id [{}] updated", customer.getId());
    }

    @Override
    public Boolean existsPersonWithUsername(String username) {
        log.info("existsPersonWithUsername :: checking if customer with username [{}] exists", username);
        var sql = """
                SELECT COUNT(username)
                FROM customer
                WHERE username = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, username);
        return count != null && count > 0;
    } 

    @Override
    public boolean existsPersonWithEmail(String email) {
        log.info("existsPersonWithEmail :: checking if customer with email [{}] exists", email);
        var sql = """
                SELECT COUNT(email)
                FROM customer
                WHERE email = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsPersonById(Long id) {
        log.info("existsPersonById :: checking if customer with id [{}] exists", id);
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
        return count != null && count > 0;
    }
}
