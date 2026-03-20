package com.example.towerdefense.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameState {
    private final Player player;
    private final Map<String, Tower> towers;
    private Wave wave;
    private long version;
    private boolean gameStarted;

    public GameState(Player player) {
        this.player = player;
        this.towers = new LinkedHashMap<>();
        this.wave = null;
        this.version = 0L;
        this.gameStarted = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Tower> getTowers() {
        return Collections.unmodifiableMap(towers);
    }

    public Wave getWave() {
        return wave;
    }

    public long getVersion() {
        return version;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        this.gameStarted = true;
    }

    public void putTower(Tower tower) {
        towers.put(tower.getId(), tower);
    }

    public void replaceTower(Tower tower) {
        towers.put(tower.getId(), tower);
    }

    public Tower getTower(String towerId) {
        return towers.get(towerId);
    }

    public void setWave(Wave wave) {
        this.wave = wave;
    }

    public void incrementVersion() {
        this.version += 1;
    }
}
