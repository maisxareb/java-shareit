package ru.practicum.shareit.exception;

public class ResponseError {
    private String error;

    public ResponseError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
