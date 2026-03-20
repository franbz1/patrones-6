package com.example.towerdefense.infra;

import com.example.towerdefense.application.GameConfig;
import com.example.towerdefense.domain.GameState;
import com.example.towerdefense.domain.Player;

import java.util.HashSet;
import java.util.Set;

public class InMemoryGameSessionStore {
    private GameState gameState = new GameState(new Player(GameConfig.INITIAL_COINS, GameConfig.INITIAL_BASE_HEALTH));
    private final Set<String> processedCommandIds = new HashSet<>();
    private int waveCounter = 0;
    private int towerCounter = 0;

    public GameState getGameState() {
        return gameState;
    }

    public void reset() {
        this.gameState = new GameState(new Player(GameConfig.INITIAL_COINS, GameConfig.INITIAL_BASE_HEALTH));
        this.processedCommandIds.clear();
        this.waveCounter = 0;
        this.towerCounter = 0;
    }

    public boolean isProcessed(String commandId) {
        return commandId != null && processedCommandIds.contains(commandId);
    }

    public void markProcessed(String commandId) {
        if (commandId != null && !commandId.isBlank()) {
            processedCommandIds.add(commandId);
        }
    }

    public String nextTowerId() {
        towerCounter += 1;
        return "tower-" + towerCounter;
    }

    public int nextWaveIndex() {
        waveCounter += 1;
        return waveCounter;
    }
}
