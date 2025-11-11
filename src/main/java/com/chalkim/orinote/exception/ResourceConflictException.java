package com.chalkim.orinote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ResourceConflictException extends ErrorResponseException {

    public ResourceConflictException() {
        super(HttpStatus.CONFLICT);
    }

    public ResourceConflictException(String message) {
        super(HttpStatus.CONFLICT,
              createProblemDetail(message),
              null);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT,
              createProblemDetail(message),
              cause);
    }

    private static ProblemDetail createProblemDetail(String message) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setDetail(message);
        pd.setTitle("Resource Conflict");
        // 可选：设置 type 或 instance
        // pd.setType(URI.create("https://example.com/problems/resource-conflict"));
        return pd;
    }
}
