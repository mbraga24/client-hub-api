package com.clienthub.customer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "customer",
    uniqueConstraints = {
            @UniqueConstraint(
                name = "customer_email_unique",
                columnNames = "email"
            ),
            @UniqueConstraint(
                name = "customer_phone_number_unique",
                columnNames = "phone_number"
            ),
            @UniqueConstraint(
                name = "customer_username_unique",
                columnNames = "username"
            ),
            @UniqueConstraint(
                name = "customer_app_user_id_unique",
                columnNames = "app_user_id"
            )
    }
)
public class Customer {

    @Id
    @SequenceGenerator(
        name = "customer_id_seq",
        sequenceName = "customer_id_seq",
        allocationSize = 1 // will match increment size in the entity mapping to the associated database sequence
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "customer_id_seq"
    )
    private Long id;
     @Column(
        name = "app_user_id",
        nullable = false
    )
    private Long appUserId;
    @Column(
        nullable = false
    )
    private String username;
    @Column(
        nullable = false
    )
    private String firstName;
    @Column(
        nullable = false
    )
    private String lastName;
    @Column(
        nullable = false
    )
    private Integer age;
    @Column(
        nullable = false
//      unique = true // it's being defined at @Table annotation
    )
    private String email;
     @Column(
        name = "phone_number",
        nullable = false
    )
    private String phoneNumber;

    public Customer(Long appUserId, String username, String firstName, String lastName, Integer age, String email, String phoneNumber) {
        this.appUserId = appUserId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(appUserId, customer.appUserId) &&
                Objects.equals(username, customer.username) &&
                Objects.equals(firstName, customer.firstName) &&
                Objects.equals(lastName, customer.lastName) &&
                Objects.equals(age, customer.age) &&
                Objects.equals(email, customer.email) &&
                Objects.equals(phoneNumber, customer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, appUserId, username, firstName, lastName, age, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", appUserId=" + appUserId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
