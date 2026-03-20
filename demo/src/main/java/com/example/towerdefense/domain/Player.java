package com.example.towerdefense.domain;

public class Player {
    private int coins;
    private int baseHealth;

    public Player(int coins, int baseHealth) {
        this.coins = coins;
        this.baseHealth = baseHealth;
    }

    public int getCoins() {
        return coins;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void spendCoins(int amount) {
        this.coins -= amount;
    }

    public void damageBase(int amount) {
        this.baseHealth = Math.max(0, this.baseHealth - amount);
    }
}
