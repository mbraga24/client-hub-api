package com.clienthub.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final CustomerRowMapper customerRowMapper;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age 
                FROM customer;
                """;
        log.info("selectAllCustomers :: FETCHING ALL CUSTOMERS");
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                WHERE id = ?
                """;
        log.info("selectCustomerById :: FETCHING CUSTOMER WITH ID: [{}]", id);
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Long insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        Long customerId = jdbcTemplate.queryForObject(sql, Long.class,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );
        log.info("insertCustomer :: [{}] ROW ADDED", customerId);
        return customerId;
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("deleteCustomer :: DELETE CUSTOMER OF ID [{}]", id);
        var sql = """
                DELETE 
                FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, id);
        log.info("deleteCustomer :: [{}] ROW DELETED", result);
    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = "";

        if (customer.getEmail() != null) {
            sql = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ? 
                    """;
            jdbcTemplate.update(
                    sql,
                    customer.getEmail(),
                    customer.getId());
            log.info("updateCustomer :: CUSTOMER [{}] UPDATED", customer.getEmail());
        }

        if (customer.getName() != null) {
            sql = """
                    UPDATE customer 
                    SET name = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(
                    sql,
                    customer.getName(),
                    customer.getId());
            log.info("updateCustomer :: CUSTOMER [{}] UPDATED", customer.getName());
        }

        if (customer.getAge() != null) {
            sql = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(
                    sql,
                    customer.getAge(),
                    customer.getId());
            log.info("updateCustomer :: CUSTOMER [{}] UPDATED", customer.getAge());
        }
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        log.info("existsPersonWithEmail :: CHECKING IF CUSTOMER WITH EMAIL [{}] EXISTS", email);
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
        log.info("existsPersonById :: CHECKING IF CUSTOMER WITH ID [{}] EXISTS", id);
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?
                """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
        return count != null && count > 0;
    }
}
