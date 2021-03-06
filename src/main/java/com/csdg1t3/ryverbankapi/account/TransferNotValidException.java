package com.csdg1t3.ryverbankapi.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transfer is invalid. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferNotValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransferNotValidException(String message) {
        super(message);
    }
}