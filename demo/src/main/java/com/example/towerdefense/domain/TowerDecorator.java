package com.example.towerdefense.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class TowerDecorator implements Tower {
    protected final Tower delegate;

    protected TowerDecorator(Tower delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public double getDamage() {
        return delegate.getDamage();
    }

    @Override
    public int getAttacksPerResolve() {
        return delegate.getAttacksPerResolve();
    }

    @Override
    public int getFreezeTurns() {
        return delegate.getFreezeTurns();
    }

    @Override
    public int getDurability() {
        return delegate.getDurability();
    }

    @Override
    public List<UpgradeType> getUpgrades() {
        List<UpgradeType> upgrades = new ArrayList<>(delegate.getUpgrades());
        upgrades.add(getUpgradeType());
        return upgrades;
    }

    protected abstract UpgradeType getUpgradeType();
}
