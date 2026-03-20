package com.example.towerdefense.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wave {
    private final int index;
    private final List<Enemy> enemies;
    private WaveStatus status;

    public Wave(int index, List<Enemy> enemies) {
        this.index = index;
        this.enemies = new ArrayList<>(enemies);
        this.status = WaveStatus.NOT_STARTED;
    }

    public int getIndex() {
        return index;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public WaveStatus getStatus() {
        return status;
    }

    public void start() {
        this.status = WaveStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = WaveStatus.COMPLETED;
    }

    public boolean hasAliveEnemies() {
        return enemies.stream().anyMatch(Enemy::isAlive);
    }
}
