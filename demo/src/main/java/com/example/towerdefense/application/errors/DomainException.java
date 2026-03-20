package com.example.towerdefense.application.errors;

public class DomainException extends RuntimeException {
    private final int httpStatus;
    private final String code;

    public DomainException(int httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }
}
