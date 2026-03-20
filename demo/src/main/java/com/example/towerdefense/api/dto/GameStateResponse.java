package com.example.towerdefense.api.dto;

import java.util.List;

public class GameStateResponse {
    private final long stateVersion;
    private final boolean gameStarted;
    private final PlayerView player;
    private final List<TowerView> towers;
    private final WaveView wave;

    public GameStateResponse(long stateVersion, boolean gameStarted, PlayerView player, List<TowerView> towers, WaveView wave) {
        this.stateVersion = stateVersion;
        this.gameStarted = gameStarted;
        this.player = player;
        this.towers = towers;
        this.wave = wave;
    }

    public long getStateVersion() {
        return stateVersion;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public PlayerView getPlayer() {
        return player;
    }

    public List<TowerView> getTowers() {
        return towers;
    }

    public WaveView getWave() {
        return wave;
    }

    public record PlayerView(int coins, int baseHealth) { }

    public record TowerView(
            String id,
            String name,
            double damage,
            int attacksPerResolve,
            int freezeTurns,
            int durability,
            List<String> upgrades
    ) { }

    public record EnemyView(
            String id,
            double health,
            double distanceToBase,
            int frozenTurns,
            String status
    ) { }

    public record WaveView(int index, String status, List<EnemyView> enemies) { }
}
