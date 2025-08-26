package ru.yandex.practicum.response;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {
    private final String error;
    private final String description;
    private final HttpStatus httpStatus;

    public ExceptionResponse(String error, String description, HttpStatus httpStatus) {
        this.error = error;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
