package com.clienthub.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
