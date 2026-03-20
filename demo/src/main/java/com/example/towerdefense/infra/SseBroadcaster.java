package com.example.towerdefense.infra;

import com.example.towerdefense.application.GameEvent;
import com.example.towerdefense.application.GameEventPublisher;
import com.google.gson.Gson;
import javax.servlet.AsyncContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SseBroadcaster implements GameEventPublisher {
    private final Gson gson;
    private final List<AsyncContext> clients;

    public SseBroadcaster(Gson gson) {
        this.gson = gson;
        this.clients = new CopyOnWriteArrayList<>();
    }

    public void register(AsyncContext asyncContext) {
        clients.add(asyncContext);
    }

    @Override
    public void publish(GameEvent event) {
        String payload = gson.toJson(event);
        for (AsyncContext client : clients) {
            try {
                PrintWriter writer = client.getResponse().getWriter();
                writer.write("event: " + event.getType() + "\n");
                writer.write("data: " + payload + "\n\n");
                writer.flush();
            } catch (IOException ex) {
                closeClient(client);
            }
        }
    }

    public void publishHeartbeat() {
        for (AsyncContext client : clients) {
            try {
                PrintWriter writer = client.getResponse().getWriter();
                writer.write(": heartbeat\n\n");
                writer.flush();
            } catch (IOException ex) {
                closeClient(client);
            }
        }
    }

    private void closeClient(AsyncContext asyncContext) {
        clients.remove(asyncContext);
        try {
            asyncContext.complete();
        } catch (Exception ignored) {
            // Best-effort cleanup for disconnected clients.
        }
    }
}
