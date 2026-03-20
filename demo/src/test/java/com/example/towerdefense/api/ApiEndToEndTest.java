package com.example.towerdefense.api;

import com.example.towerdefense.application.GameService;
import com.example.towerdefense.application.TowerFactory;
import com.example.towerdefense.infra.InMemoryGameSessionStore;
import com.example.towerdefense.infra.SseBroadcaster;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiEndToEndTest {
    private static final String BASE_URL = "http://localhost:4567";
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    @BeforeAll
    static void startServer() {
        InMemoryGameSessionStore store = new InMemoryGameSessionStore();
        TowerFactory towerFactory = new TowerFactory();
        SseBroadcaster broadcaster = new SseBroadcaster(GSON);
        GameService service = new GameService(store, towerFactory, broadcaster);
        ApiServer apiServer = new ApiServer(GSON, service, towerFactory, new GameStateMapper(), broadcaster);
        apiServer.start();
        Spark.awaitInitialization();
    }

    @AfterAll
    static void stopServer() {
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    void shouldSimulateFrontendFlowThroughHttpApi() throws Exception {
        JsonObject gameStarted = post("/game/start", "{\"commandId\":\"e2e-start\"}");
        long version = gameStarted.get("stateVersion").getAsLong();
        assertTrue(gameStarted.get("gameStarted").getAsBoolean());

        JsonObject built = post("/towers", "{\"commandId\":\"e2e-build\",\"expectedVersion\":" + version + ",\"towerId\":\"tower-http\"}");
        version = built.get("stateVersion").getAsLong();
        JsonArray towersAfterBuild = built.getAsJsonArray("towers");
        assertEquals(1, towersAfterBuild.size());

        JsonObject upgraded = post("/towers/tower-http/upgrades",
                "{\"commandId\":\"e2e-upgrade\",\"expectedVersion\":" + version + ",\"upgradeType\":\"rapid_fire\"}");
        version = upgraded.get("stateVersion").getAsLong();

        JsonObject waveStarted = post("/wave/start", "{\"commandId\":\"e2e-wave\",\"expectedVersion\":" + version + "}");
        version = waveStarted.get("stateVersion").getAsLong();
        assertEquals("IN_PROGRESS", waveStarted.getAsJsonObject("wave").get("status").getAsString());

        for (int i = 0; i < 12; i++) {
            JsonObject resolved = post("/wave/resolve", "{\"commandId\":\"e2e-resolve-" + i + "\",\"expectedVersion\":" + version + "}");
            version = resolved.get("stateVersion").getAsLong();
            if ("COMPLETED".equals(resolved.getAsJsonObject("wave").get("status").getAsString())) {
                break;
            }
        }

        JsonObject state = get("/game/state");
        assertTrue(state.get("stateVersion").getAsLong() >= version);
        assertEquals(true, state.get("gameStarted").getAsBoolean());
        assertEquals(1, state.getAsJsonArray("towers").size());
        assertTrue(state.getAsJsonObject("player").get("coins").getAsInt() >= 0);
    }

    private static JsonObject post(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(response.statusCode() >= 200 && response.statusCode() < 300, response.body());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static JsonObject get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }
}
