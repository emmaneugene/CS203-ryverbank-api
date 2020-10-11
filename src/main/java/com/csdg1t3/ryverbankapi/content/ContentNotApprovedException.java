package com.csdg1t3.ryverbankapi.content;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotApprovedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ContentNotApprovedException(Long id) {
        super("Content " + id + " is unapproved");
    }
}
