package com.example.towerdefense.application.errors;

public class ValidationException extends DomainException {
    public ValidationException(String code, String message) {
        super(422, code, message);
    }
}
