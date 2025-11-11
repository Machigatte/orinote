package com.chalkim.orinote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidRequestException extends ErrorResponseException {

    public InvalidRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST,
              createProblemDetail(message),
              null);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST,
              createProblemDetail(message),
              cause);
    }

    private static ProblemDetail createProblemDetail(String message) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail(message);
        pd.setTitle("Invalid Request");
        // 可选：设置 type 或 instance
        // pd.setType(URI.create("https://example.com/problems/invalid-request"));
        return pd;
    }
}
