package com.example.towerdefense.api;

import com.example.towerdefense.api.dto.ApplyUpgradeRequest;
import com.example.towerdefense.api.dto.BuildTowerRequest;
import com.example.towerdefense.api.dto.CommandRequest;
import com.example.towerdefense.api.dto.ErrorResponse;
import com.example.towerdefense.application.GameCommand;
import com.example.towerdefense.application.GameEvent;
import com.example.towerdefense.application.GameService;
import com.example.towerdefense.application.TowerFactory;
import com.example.towerdefense.application.errors.DomainException;
import com.example.towerdefense.infra.SseBroadcaster;
import com.google.gson.Gson;
import spark.Request;

import javax.servlet.AsyncContext;
import java.util.Map;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

public class ApiServer {
    private final Gson gson;
    private final GameService gameService;
    private final TowerFactory towerFactory;
    private final GameStateMapper mapper;
    private final SseBroadcaster broadcaster;

    public ApiServer(Gson gson, GameService gameService, TowerFactory towerFactory, GameStateMapper mapper, SseBroadcaster broadcaster) {
        this.gson = gson;
        this.gameService = gameService;
        this.towerFactory = towerFactory;
        this.mapper = mapper;
        this.broadcaster = broadcaster;
    }

    public void start() {
        port(4567);

        after((req, res) -> {
            if (!"/game/events".equals(req.pathInfo())) {
                res.type("application/json");
            }
        });

        exception(DomainException.class, (ex, req, res) -> {
            DomainException domainException = (DomainException) ex;
            res.status(domainException.getHttpStatus());
            res.body(gson.toJson(new ErrorResponse(
                    domainException.getCode(),
                    domainException.getMessage(),
                    null,
                    gameService.getState().getVersion()
            )));
        });

        exception(Exception.class, (ex, req, res) -> {
            res.status(500);
            res.body(gson.toJson(new ErrorResponse(
                    "internal_error",
                    ex.getMessage(),
                    null,
                    gameService.getState().getVersion()
            )));
        });

        get("/health", (req, res) -> gson.toJson(Map.of("status", "ok")));

        path("/game", () -> {
            post("/start", (req, res) -> {
                CommandRequest body = readBody(req, CommandRequest.class);
                var state = gameService.startGame(toCommand(body));
                broadcaster.publish(new GameEvent("state_updated", Map.of("version", state.getVersion())));
                return gson.toJson(mapper.toResponse(state));
            });

            get("/state", (req, res) -> gson.toJson(mapper.toResponse(gameService.getState())));

            get("/events", (req, res) -> {
                res.raw().setStatus(200);
                res.raw().setHeader("Content-Type", "text/event-stream");
                res.raw().setHeader("Cache-Control", "no-cache");
                res.raw().setHeader("Connection", "keep-alive");
                AsyncContext async = req.raw().startAsync();
                async.setTimeout(0);
                broadcaster.register(async);
                broadcaster.publish(new GameEvent("sse_connected", Map.of("message", "SSE client connected")));
                return "";
            });
        });

        path("/towers", () -> {
            post("", (req, res) -> {
                BuildTowerRequest body = readBody(req, BuildTowerRequest.class);
                var state = gameService.buildTower(toCommand(body), body.getTowerId());
                broadcaster.publish(new GameEvent("state_updated", Map.of("version", state.getVersion())));
                return gson.toJson(mapper.toResponse(state));
            });

            post("/:towerId/upgrades", (req, res) -> {
                ApplyUpgradeRequest body = readBody(req, ApplyUpgradeRequest.class);
                String towerId = req.params("towerId");
                var state = gameService.addUpgrade(
                        toCommand(body),
                        towerId,
                        towerFactory.parseUpgrade(body.getUpgradeType())
                );
                broadcaster.publish(new GameEvent("state_updated", Map.of("version", state.getVersion())));
                return gson.toJson(mapper.toResponse(state));
            });
        });

        path("/wave", () -> {
            post("/start", (req, res) -> {
                CommandRequest body = readBody(req, CommandRequest.class);
                var state = gameService.startWave(toCommand(body));
                broadcaster.publish(new GameEvent("state_updated", Map.of("version", state.getVersion())));
                return gson.toJson(mapper.toResponse(state));
            });

            post("/resolve", (req, res) -> {
                CommandRequest body = readBody(req, CommandRequest.class);
                var state = gameService.resolveWave(toCommand(body));
                broadcaster.publish(new GameEvent("state_updated", Map.of("version", state.getVersion())));
                return gson.toJson(mapper.toResponse(state));
            });
        });

        init();
    }

    private GameCommand toCommand(CommandRequest request) {
        if (request == null) {
            return new GameCommand(null, null);
        }
        return new GameCommand(request.getCommandId(), request.getExpectedVersion());
    }

    private <T> T readBody(Request request, Class<T> clazz) {
        String body = request.body();
        if (body == null || body.isBlank()) {
            return null;
        }
        return gson.fromJson(body, clazz);
    }
}
