package com.example.towerdefense.api.dto;

public class ErrorResponse {
    private final String code;
    private final String message;
    private final Object details;
    private final long stateVersion;

    public ErrorResponse(String code, String message, Object details, long stateVersion) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.stateVersion = stateVersion;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }

    public long getStateVersion() {
        return stateVersion;
    }
}
