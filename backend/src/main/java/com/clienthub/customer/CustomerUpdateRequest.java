package com.clienthub.customer;

public record CustomerUpdateRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        Integer age,
        String phoneNumber
) {
}
