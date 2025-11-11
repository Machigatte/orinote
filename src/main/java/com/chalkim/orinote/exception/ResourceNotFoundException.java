package com.chalkim.orinote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ResourceNotFoundException extends ErrorResponseException {

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND,
              createProblemDetail(message),
              null);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND,
              createProblemDetail(message),
              cause);
    }

    private static ProblemDetail createProblemDetail(String message) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setDetail(message);
        pd.setTitle("Resource Not Found");
        // pd.setType(URI.create("https://example.com/problems/resource-not-found"));
        return pd;
    }
}
