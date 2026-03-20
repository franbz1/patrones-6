package com.example.towerdefense.application.errors;

public class ConflictException extends DomainException {
    public ConflictException(String code, String message) {
        super(409, code, message);
    }
}
