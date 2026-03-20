package com.example.towerdefense.domain;

public class Enemy {
    private final String id;
    private double health;
    private final double baseSpeed;
    private double distanceToBase;
    private final int rewardCoins;
    private int frozenTurns;
    private EnemyStatus status;

    public Enemy(String id, double health, double baseSpeed, double distanceToBase, int rewardCoins) {
        this.id = id;
        this.health = health;
        this.baseSpeed = baseSpeed;
        this.distanceToBase = distanceToBase;
        this.rewardCoins = rewardCoins;
        this.frozenTurns = 0;
        this.status = EnemyStatus.ALIVE;
    }

    public String getId() {
        return id;
    }

    public double getHealth() {
        return health;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getDistanceToBase() {
        return distanceToBase;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    public int getFrozenTurns() {
        return frozenTurns;
    }

    public EnemyStatus getStatus() {
        return status;
    }

    public boolean isAlive() {
        return status == EnemyStatus.ALIVE;
    }

    public void applyDamage(double damage) {
        if (!isAlive()) {
            return;
        }
        health -= damage;
        if (health <= 0) {
            health = 0;
            status = EnemyStatus.DEAD;
        }
    }

    public void applyFreeze(int turns) {
        if (!isAlive() || turns <= 0) {
            return;
        }
        frozenTurns = Math.max(frozenTurns, turns);
    }

    public void moveStep() {
        if (!isAlive()) {
            return;
        }
        if (frozenTurns > 0) {
            frozenTurns -= 1;
            return;
        }
        distanceToBase -= baseSpeed;
        if (distanceToBase <= 0) {
            distanceToBase = 0;
            status = EnemyStatus.ESCAPED;
        }
    }
}
