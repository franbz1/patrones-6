package com.example.towerdefense.api;

import com.example.towerdefense.api.dto.GameStateResponse;
import com.example.towerdefense.domain.Enemy;
import com.example.towerdefense.domain.GameState;
import com.example.towerdefense.domain.Tower;
import com.example.towerdefense.domain.UpgradeType;
import com.example.towerdefense.domain.Wave;

import java.util.Comparator;
import java.util.List;

public class GameStateMapper {
    public GameStateResponse toResponse(GameState state) {
        List<GameStateResponse.TowerView> towers = state.getTowers().values().stream()
                .sorted(Comparator.comparing(Tower::getId))
                .map(this::toTowerView)
                .toList();

        Wave wave = state.getWave();
        GameStateResponse.WaveView waveView = null;
        if (wave != null) {
            List<GameStateResponse.EnemyView> enemies = wave.getEnemies().stream()
                    .map(this::toEnemyView)
                    .toList();
            waveView = new GameStateResponse.WaveView(wave.getIndex(), wave.getStatus().name(), enemies);
        }

        return new GameStateResponse(
                state.getVersion(),
                state.isGameStarted(),
                new GameStateResponse.PlayerView(state.getPlayer().getCoins(), state.getPlayer().getBaseHealth()),
                towers,
                waveView
        );
    }

    private GameStateResponse.TowerView toTowerView(Tower tower) {
        List<String> upgrades = tower.getUpgrades().stream().map(UpgradeType::name).toList();
        return new GameStateResponse.TowerView(
                tower.getId(),
                tower.getName(),
                tower.getDamage(),
                tower.getAttacksPerResolve(),
                tower.getFreezeTurns(),
                tower.getDurability(),
                upgrades
        );
    }

    private GameStateResponse.EnemyView toEnemyView(Enemy enemy) {
        return new GameStateResponse.EnemyView(
                enemy.getId(),
                enemy.getHealth(),
                enemy.getDistanceToBase(),
                enemy.getFrozenTurns(),
                enemy.getStatus().name()
        );
    }
}
