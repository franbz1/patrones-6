package com.example;

import com.example.towerdefense.api.ApiServer;
import com.example.towerdefense.api.GameStateMapper;
import com.example.towerdefense.application.GameService;
import com.example.towerdefense.application.TowerFactory;
import com.example.towerdefense.infra.InMemoryGameSessionStore;
import com.example.towerdefense.infra.SseBroadcaster;
import com.google.gson.Gson;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();
        InMemoryGameSessionStore store = new InMemoryGameSessionStore();
        TowerFactory towerFactory = new TowerFactory();
        SseBroadcaster broadcaster = new SseBroadcaster(gson);
        GameService gameService = new GameService(store, towerFactory, broadcaster);

        ApiServer apiServer = new ApiServer(gson, gameService, towerFactory, new GameStateMapper(), broadcaster);
        apiServer.start();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(broadcaster::publishHeartbeat, 10, 10, TimeUnit.SECONDS);
    }
}