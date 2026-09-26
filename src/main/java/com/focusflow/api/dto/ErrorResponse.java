package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path,
    Map<String, String> fields
) {
    public ErrorResponse(Integer status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path, null);
    }

    public ErrorResponse(Integer status, String error, String message, String path, Map<String, String> fields) {
        this(Instant.now(), status, error, message, path, fields);
    }
}
