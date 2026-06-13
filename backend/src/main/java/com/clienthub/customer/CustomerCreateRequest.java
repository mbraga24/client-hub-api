package com.clienthub.customer;

public record CustomerCreateRequest(
        String name,
        String email,
        Integer age
) {
}
