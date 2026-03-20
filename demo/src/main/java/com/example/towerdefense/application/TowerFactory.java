package com.example.towerdefense.application;

import com.example.towerdefense.application.errors.ValidationException;
import com.example.towerdefense.domain.BasicTower;
import com.example.towerdefense.domain.FreezeShotDecorator;
import com.example.towerdefense.domain.RapidFireDecorator;
import com.example.towerdefense.domain.ReinforcedDecorator;
import com.example.towerdefense.domain.Tower;
import com.example.towerdefense.domain.UpgradeType;

public class TowerFactory {
    public Tower createBasicTower(String towerId) {
        return new BasicTower(towerId);
    }

    public Tower applyUpgrade(Tower current, UpgradeType upgradeType) {
        return switch (upgradeType) {
            case RAPID_FIRE -> new RapidFireDecorator(current);
            case FREEZE_SHOT -> new FreezeShotDecorator(current);
            case REINFORCED -> new ReinforcedDecorator(current);
        };
    }

    public int getUpgradeCost(UpgradeType upgradeType) {
        return switch (upgradeType) {
            case RAPID_FIRE -> GameConfig.UPGRADE_RAPID_FIRE_COST;
            case FREEZE_SHOT -> GameConfig.UPGRADE_FREEZE_SHOT_COST;
            case REINFORCED -> GameConfig.UPGRADE_REINFORCED_COST;
        };
    }

    public UpgradeType parseUpgrade(String rawUpgrade) {
        try {
            return UpgradeType.valueOf(rawUpgrade.trim().toUpperCase());
        } catch (Exception ex) {
            throw new ValidationException("invalid_upgrade", "Unsupported upgrade type: " + rawUpgrade);
        }
    }
}
