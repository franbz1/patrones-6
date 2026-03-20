package com.example.towerdefense.domain;

public class FreezeShotDecorator extends TowerDecorator {
    public FreezeShotDecorator(Tower delegate) {
        super(delegate);
    }

    @Override
    public int getFreezeTurns() {
        return Math.max(delegate.getFreezeTurns(), 1);
    }

    @Override
    protected UpgradeType getUpgradeType() {
        return UpgradeType.FREEZE_SHOT;
    }
}
