package com.example.towerdefense.domain;

import java.util.Collections;
import java.util.List;

public class BasicTower implements Tower {
    private final String id;

    public BasicTower(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Basic Tower";
    }

    @Override
    public double getDamage() {
        return 15.0;
    }

    @Override
    public int getAttacksPerResolve() {
        return 1;
    }

    @Override
    public int getFreezeTurns() {
        return 0;
    }

    @Override
    public int getDurability() {
        return 100;
    }

    @Override
    public List<UpgradeType> getUpgrades() {
        return Collections.emptyList();
    }
}
