package com.clienthub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class ResourceNotChangedException extends RuntimeException {

    public ResourceNotChangedException(String message) {
        super(message);
    }
}
