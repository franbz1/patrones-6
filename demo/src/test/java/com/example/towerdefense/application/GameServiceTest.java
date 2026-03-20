package com.example.towerdefense.application;

import com.example.towerdefense.application.errors.ConflictException;
import com.example.towerdefense.domain.GameState;
import com.example.towerdefense.domain.UpgradeType;
import com.example.towerdefense.domain.WaveStatus;
import com.example.towerdefense.infra.InMemoryGameSessionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {
    private GameService gameService;

    @BeforeEach
    void setUp() {
        InMemoryGameSessionStore store = new InMemoryGameSessionStore();
        TowerFactory towerFactory = new TowerFactory();
        List<GameEvent> publishedEvents = new ArrayList<>();
        gameService = new GameService(store, towerFactory, publishedEvents::add);
    }

    @Test
    void shouldBuildTowerAndApplyStackedDecorators() {
        GameState started = gameService.startGame(new GameCommand(UUID.randomUUID().toString(), null));
        long versionAfterStart = started.getVersion();

        GameState built = gameService.buildTower(new GameCommand(UUID.randomUUID().toString(), versionAfterStart), "tower-a");
        assertTrue(built.getTowers().containsKey("tower-a"));
        assertEquals(60, built.getPlayer().getCoins());

        GameState upgraded1 = gameService.addUpgrade(
                new GameCommand(UUID.randomUUID().toString(), built.getVersion()),
                "tower-a",
                UpgradeType.RAPID_FIRE
        );
        GameState upgraded2 = gameService.addUpgrade(
                new GameCommand(UUID.randomUUID().toString(), upgraded1.getVersion()),
                "tower-a",
                UpgradeType.REINFORCED
        );

        var tower = upgraded2.getTower("tower-a");
        assertEquals(2, tower.getAttacksPerResolve());
        assertEquals(0, tower.getFreezeTurns());
        assertEquals(150, tower.getDurability());
        assertEquals(2, tower.getUpgrades().size());
        assertEquals(UpgradeType.RAPID_FIRE, tower.getUpgrades().get(0));
        assertEquals(UpgradeType.REINFORCED, tower.getUpgrades().get(1));
    }

    @Test
    void shouldRejectVersionMismatch() {
        gameService.startGame(new GameCommand("cmd-start", null));
        assertThrows(
                ConflictException.class,
                () -> gameService.buildTower(new GameCommand("cmd-build", 999L), "tower-b")
        );
    }

    @Test
    void shouldBeIdempotentWhenCommandIdIsRepeated() {
        GameState started = gameService.startGame(new GameCommand("same-cmd", null));
        long version1 = started.getVersion();

        GameState duplicateStart = gameService.startGame(new GameCommand("same-cmd", null));
        assertEquals(version1, duplicateStart.getVersion());

        GameState built = gameService.buildTower(new GameCommand("build-cmd", duplicateStart.getVersion()), "tower-c");
        long versionAfterBuild = built.getVersion();

        GameState duplicateBuild = gameService.buildTower(new GameCommand("build-cmd", built.getVersion()), "tower-c");
        assertEquals(versionAfterBuild, duplicateBuild.getVersion());
        assertEquals(1, duplicateBuild.getTowers().size());
    }

    @Test
    void shouldResolveWaveAndEventuallyCompleteIt() {
        GameState state = gameService.startGame(new GameCommand("start-game", null));
        state = gameService.buildTower(new GameCommand("build-1", state.getVersion()), "tower-d");
        state = gameService.addUpgrade(new GameCommand("up-1", state.getVersion()), "tower-d", UpgradeType.RAPID_FIRE);
        state = gameService.addUpgrade(new GameCommand("up-2", state.getVersion()), "tower-d", UpgradeType.RAPID_FIRE);
        state = gameService.startWave(new GameCommand("wave-start", state.getVersion()));

        int safety = 50;
        while (state.getWave() != null && state.getWave().getStatus() == WaveStatus.IN_PROGRESS && safety-- > 0) {
            state = gameService.resolveWave(new GameCommand(UUID.randomUUID().toString(), state.getVersion()));
        }

        assertTrue(safety > 0, "Wave did not finish within expected iterations.");
        assertEquals(WaveStatus.COMPLETED, state.getWave().getStatus());
        assertTrue(state.getPlayer().getCoins() > 0);
    }
}
