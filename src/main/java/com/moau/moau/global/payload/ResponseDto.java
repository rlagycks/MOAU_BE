package com.moau.moau.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    private final Instant timestamp;
    private final String message;
    private final T data;

    protected ResponseDto(Instant timestamp, String message, T data) {
        this.timestamp = timestamp;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> message(String message) {
        return new ResponseDto<>(Instant.now(), message, null);
    }

    public static <T> ResponseDto<T> data(T body) {
        return new ResponseDto<>(Instant.now(), null, body);
    }

    public static <T> ResponseDto<T> dataWithMessage(T body, String message) {
        return new ResponseDto<>(Instant.now(), message, body);
    }
}