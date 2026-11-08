package ru.zhivoymir.god;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/** Запрос к LLM-брокеру (OpenAI-совместимый chat completions). */
final class Broker {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .proxy(HttpClient.Builder.NO_PROXY)
            .build();

    private Broker() {}

    static CompletableFuture<String> chat(String model, String system, String user) {
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("temperature", 0.8);
        body.addProperty("max_tokens", 1500);
        JsonArray messages = new JsonArray();
        messages.add(message("system", system));
        messages.add(message("user", user));
        body.add("messages", messages);
        HttpRequest req = HttpRequest.newBuilder(URI.create(God.BROKER_URL.get()))
                .timeout(Duration.ofSeconds(110))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + God.apiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(r -> {
            if (r.statusCode() != 200) throw new IllegalStateException("broker HTTP " + r.statusCode() + ": " + cut(r.body()));
            return JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("choices").get(0).getAsJsonObject()
                    .getAsJsonObject("message").get("content").getAsString();
        });
    }

    private static JsonObject message(String role, String content) {
        JsonObject m = new JsonObject();
        m.addProperty("role", role);
        m.addProperty("content", content);
        return m;
    }

    private static String cut(String s) {
        return s.length() > 300 ? s.substring(0, 300) + "…" : s;
    }
}
