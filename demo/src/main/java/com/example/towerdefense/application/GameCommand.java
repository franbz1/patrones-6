package com.example.towerdefense.application;

public class GameCommand {
    private final String commandId;
    private final Long expectedVersion;

    public GameCommand(String commandId, Long expectedVersion) {
        this.commandId = commandId;
        this.expectedVersion = expectedVersion;
    }

    public String getCommandId() {
        return commandId;
    }

    public Long getExpectedVersion() {
        return expectedVersion;
    }
}
