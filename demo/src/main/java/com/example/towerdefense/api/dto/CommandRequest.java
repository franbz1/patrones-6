package com.example.towerdefense.api.dto;

public class CommandRequest {
    private String commandId;
    private Long expectedVersion;

    public String getCommandId() {
        return commandId;
    }

    public Long getExpectedVersion() {
        return expectedVersion;
    }
}
