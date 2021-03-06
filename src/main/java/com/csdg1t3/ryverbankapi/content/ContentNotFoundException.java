package com.csdg1t3.ryverbankapi.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a content is not found. This also returns a HTTP response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ContentNotFoundException(Long id) {
        super("Unable to find content " + id);
    }
}