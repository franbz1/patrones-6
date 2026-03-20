package com.example.towerdefense.application;

public final class GameConfig {
    public static final int INITIAL_COINS = 100;
    public static final int INITIAL_BASE_HEALTH = 10;

    public static final int BUILD_TOWER_COST = 40;
    public static final int UPGRADE_RAPID_FIRE_COST = 30;
    public static final int UPGRADE_FREEZE_SHOT_COST = 35;
    public static final int UPGRADE_REINFORCED_COST = 25;

    public static final int WAVE_ENEMY_COUNT = 6;
    public static final double WAVE_ENEMY_HEALTH = 40.0;
    public static final double WAVE_ENEMY_SPEED = 1.0;
    public static final double WAVE_ENEMY_DISTANCE = 6.0;
    public static final int WAVE_ENEMY_REWARD = 10;

    private GameConfig() {
    }
}
