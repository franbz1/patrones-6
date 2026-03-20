package com.example.towerdefense.domain;

public class RapidFireDecorator extends TowerDecorator {
    public RapidFireDecorator(Tower delegate) {
        super(delegate);
    }

    @Override
    public int getAttacksPerResolve() {
        return delegate.getAttacksPerResolve() + 1;
    }

    @Override
    protected UpgradeType getUpgradeType() {
        return UpgradeType.RAPID_FIRE;
    }
}
