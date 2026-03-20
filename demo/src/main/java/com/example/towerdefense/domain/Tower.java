package com.example.towerdefense.domain;

import java.util.List;

public interface Tower {
    String getId();
    String getName();
    double getDamage();
    int getAttacksPerResolve();
    int getFreezeTurns();
    int getDurability();
    List<UpgradeType> getUpgrades();
}
