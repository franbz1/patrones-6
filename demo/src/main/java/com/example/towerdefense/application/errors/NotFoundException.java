package com.example.towerdefense.application.errors;

public class NotFoundException extends DomainException {
    public NotFoundException(String code, String message) {
        super(404, code, message);
    }
}
