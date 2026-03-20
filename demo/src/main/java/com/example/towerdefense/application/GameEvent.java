package com.example.towerdefense.application;

import java.util.Map;
import java.util.UUID;

public class GameEvent {
    private final String id;
    private final String type;
    private final long timestampEpochMillis;
    private final Map<String, Object> payload;

    public GameEvent(String type, Map<String, Object> payload) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.timestampEpochMillis = System.currentTimeMillis();
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public long getTimestampEpochMillis() {
        return timestampEpochMillis;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
