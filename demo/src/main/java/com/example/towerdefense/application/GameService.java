package com.example.towerdefense.application;

import com.example.towerdefense.application.errors.ConflictException;
import com.example.towerdefense.application.errors.NotFoundException;
import com.example.towerdefense.application.errors.ValidationException;
import com.example.towerdefense.domain.Enemy;
import com.example.towerdefense.domain.GameState;
import com.example.towerdefense.domain.Tower;
import com.example.towerdefense.domain.UpgradeType;
import com.example.towerdefense.domain.Wave;
import com.example.towerdefense.domain.WaveStatus;
import com.example.towerdefense.infra.InMemoryGameSessionStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameService {
    private final InMemoryGameSessionStore store;
    private final TowerFactory towerFactory;
    private final GameEventPublisher eventPublisher;

    public GameService(InMemoryGameSessionStore store, TowerFactory towerFactory, GameEventPublisher eventPublisher) {
        this.store = store;
        this.towerFactory = towerFactory;
        this.eventPublisher = eventPublisher;
    }

    public synchronized GameState getState() {
        return store.getGameState();
    }

    public synchronized GameState startGame(GameCommand command) {
        if (isDuplicate(command)) {
            return store.getGameState();
        }
        validateExpectedVersion(command, store.getGameState().getVersion());
        store.reset();
        GameState state = store.getGameState();
        state.startGame();
        state.incrementVersion();
        markCommand(command);
        publish("game_started", Map.of("version", state.getVersion()));
        return state;
    }

    public synchronized GameState buildTower(GameCommand command, String requestedTowerId) {
        if (isDuplicate(command)) {
            return store.getGameState();
        }
        GameState state = store.getGameState();
        ensureGameStarted(state);
        validateExpectedVersion(command, state.getVersion());
        ensureCoins(state, GameConfig.BUILD_TOWER_COST, "build_tower");

        String towerId = requestedTowerId == null || requestedTowerId.isBlank()
                ? store.nextTowerId()
                : requestedTowerId.trim();

        if (state.getTower(towerId) != null) {
            throw new ConflictException("tower_exists", "Tower with id " + towerId + " already exists.");
        }

        Tower tower = towerFactory.createBasicTower(towerId);
        state.getPlayer().spendCoins(GameConfig.BUILD_TOWER_COST);
        state.putTower(tower);
        state.incrementVersion();
        markCommand(command);

        publish("tower_built", Map.of(
                "towerId", tower.getId(),
                "coins", state.getPlayer().getCoins(),
                "version", state.getVersion()
        ));
        return state;
    }

    public synchronized GameState addUpgrade(GameCommand command, String towerId, UpgradeType upgradeType) {
        if (isDuplicate(command)) {
            return store.getGameState();
        }
        GameState state = store.getGameState();
        ensureGameStarted(state);
        validateExpectedVersion(command, state.getVersion());

        Tower tower = state.getTower(towerId);
        if (tower == null) {
            throw new NotFoundException("tower_not_found", "Tower with id " + towerId + " was not found.");
        }

        int cost = towerFactory.getUpgradeCost(upgradeType);
        ensureCoins(state, cost, "upgrade_" + upgradeType.name().toLowerCase());

        Tower upgraded = towerFactory.applyUpgrade(tower, upgradeType);
        state.getPlayer().spendCoins(cost);
        state.replaceTower(upgraded);
        state.incrementVersion();
        markCommand(command);

        publish("upgrade_applied", Map.of(
                "towerId", upgraded.getId(),
                "upgrade", upgradeType.name(),
                "coins", state.getPlayer().getCoins(),
                "version", state.getVersion()
        ));
        return state;
    }

    public synchronized GameState startWave(GameCommand command) {
        if (isDuplicate(command)) {
            return store.getGameState();
        }
        GameState state = store.getGameState();
        ensureGameStarted(state);
        validateExpectedVersion(command, state.getVersion());

        Wave existingWave = state.getWave();
        if (existingWave != null && existingWave.getStatus() == WaveStatus.IN_PROGRESS) {
            throw new ConflictException("wave_already_in_progress", "There is already an active wave.");
        }

        List<Enemy> enemies = new ArrayList<>();
        for (int i = 1; i <= GameConfig.WAVE_ENEMY_COUNT; i++) {
            enemies.add(new Enemy(
                    "enemy-" + i,
                    GameConfig.WAVE_ENEMY_HEALTH,
                    GameConfig.WAVE_ENEMY_SPEED,
                    GameConfig.WAVE_ENEMY_DISTANCE + (i * 0.5),
                    GameConfig.WAVE_ENEMY_REWARD
            ));
        }

        Wave wave = new Wave(store.nextWaveIndex(), enemies);
        wave.start();
        state.setWave(wave);
        state.incrementVersion();
        markCommand(command);

        publish("wave_started", Map.of(
                "waveIndex", wave.getIndex(),
                "enemyCount", enemies.size(),
                "version", state.getVersion()
        ));
        return state;
    }

    public synchronized GameState resolveWave(GameCommand command) {
        if (isDuplicate(command)) {
            return store.getGameState();
        }
        GameState state = store.getGameState();
        ensureGameStarted(state);
        validateExpectedVersion(command, state.getVersion());

        Wave wave = state.getWave();
        if (wave == null || wave.getStatus() != WaveStatus.IN_PROGRESS) {
            throw new ValidationException("wave_not_in_progress", "No active wave to resolve.");
        }

        List<Tower> towers = new ArrayList<>(state.getTowers().values());
        towers.sort(Comparator.comparing(Tower::getId));

        for (Tower tower : towers) {
            for (int attack = 0; attack < tower.getAttacksPerResolve(); attack++) {
                Enemy target = firstAliveEnemy(wave);
                if (target == null) {
                    break;
                }
                target.applyDamage(tower.getDamage());
                if (tower.getFreezeTurns() > 0) {
                    target.applyFreeze(tower.getFreezeTurns());
                }
                if (!target.isAlive()) {
                    state.getPlayer().addCoins(target.getRewardCoins());
                    publish("enemy_killed", Map.of(
                            "enemyId", target.getId(),
                            "rewardCoins", target.getRewardCoins()
                    ));
                }
            }
        }

        for (Enemy enemy : wave.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            enemy.moveStep();
            if (!enemy.isAlive()) {
                state.getPlayer().damageBase(1);
                publish("enemy_escaped", Map.of(
                        "enemyId", enemy.getId(),
                        "baseHealth", state.getPlayer().getBaseHealth()
                ));
            }
        }

        if (!wave.hasAliveEnemies()) {
            wave.complete();
            publish("wave_completed", Map.of("waveIndex", wave.getIndex()));
        }

        state.incrementVersion();
        markCommand(command);
        publish("wave_resolved", Map.of(
                "version", state.getVersion(),
                "coins", state.getPlayer().getCoins(),
                "baseHealth", state.getPlayer().getBaseHealth()
        ));
        return state;
    }

    private void ensureGameStarted(GameState state) {
        if (!state.isGameStarted()) {
            throw new ValidationException("game_not_started", "Start the game before issuing commands.");
        }
    }

    private void ensureCoins(GameState state, int needed, String action) {
        if (state.getPlayer().getCoins() < needed) {
            throw new ValidationException(
                    "insufficient_coins",
                    "Not enough coins to " + action + ". Need " + needed + "."
            );
        }
    }

    private Enemy firstAliveEnemy(Wave wave) {
        for (Enemy enemy : wave.getEnemies()) {
            if (enemy.isAlive()) {
                return enemy;
            }
        }
        return null;
    }

    private boolean isDuplicate(GameCommand command) {
        if (command == null || command.getCommandId() == null || command.getCommandId().isBlank()) {
            return false;
        }
        return store.isProcessed(command.getCommandId());
    }

    private void markCommand(GameCommand command) {
        if (command != null) {
            store.markProcessed(command.getCommandId());
        }
    }

    private void validateExpectedVersion(GameCommand command, long actualVersion) {
        if (command == null || command.getExpectedVersion() == null) {
            return;
        }
        if (command.getExpectedVersion() != actualVersion) {
            throw new ConflictException(
                    "version_conflict",
                    "Expected version " + command.getExpectedVersion() + " but current is " + actualVersion + "."
            );
        }
    }

    private void publish(String type, Map<String, Object> payload) {
        Map<String, Object> safePayload = new HashMap<>(payload);
        eventPublisher.publish(new GameEvent(type, safePayload));
    }
}
