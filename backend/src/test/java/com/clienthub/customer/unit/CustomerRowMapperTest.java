package com.clienthub.customer.unit;

import com.clienthub.customer.Customer;
import com.clienthub.customer.CustomerRowMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper underTest = new CustomerRowMapper();
        Customer expected = new Customer(1L, 100L, "johndoe", "John", "Doe", 31, "john_doe@email.com", "555-0001");

        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.getLong("id")).thenReturn(1L);
        Mockito.when(resultSet.getLong("app_user_id")).thenReturn(100L);
        Mockito.when(resultSet.getString("username")).thenReturn("johndoe");
        Mockito.when(resultSet.getString("first_name")).thenReturn("John");
        Mockito.when(resultSet.getString("last_name")).thenReturn("Doe");
        Mockito.when(resultSet.getInt("age")).thenReturn(31);
        Mockito.when(resultSet.getString("email")).thenReturn("john_doe@email.com");
        Mockito.when(resultSet.getString("phone_number")).thenReturn("555-0001");

        // When
        Customer actual = underTest.mapRow(resultSet, 1);

        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
