package com.example.towerdefense.domain;

public class ReinforcedDecorator extends TowerDecorator {
    public ReinforcedDecorator(Tower delegate) {
        super(delegate);
    }

    @Override
    public int getDurability() {
        return delegate.getDurability() + 50;
    }

    @Override
    protected UpgradeType getUpgradeType() {
        return UpgradeType.REINFORCED;
    }
}
