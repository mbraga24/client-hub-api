package com.clienthub.customer;

public record CustomerCreateRequest(
        Long appUserId,
        String username,
        String firstName,
        String lastName,
        String email,
        Integer age,
        String phoneNumber
) {
}
