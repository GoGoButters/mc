package ru.zhivoymir.livingvoice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Запросы к сервисам распознавания и озвучки (через LLM-брокер). */
public final class Http {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .proxy(HttpClient.Builder.NO_PROXY)
            .build();

    private Http() {
    }

    /** Распознать фразу: PCM 16 кГц моно. */
    public static CompletableFuture<String> recognize(short[] pcm16k) {
        String url = LivingVoice.STT_URL.get();
        if (url.isEmpty()) return CompletableFuture.completedFuture("");
        ByteBuffer buf = ByteBuffer.allocate(pcm16k.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        for (short s : pcm16k) buf.putShort(s);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url + (url.contains("?") ? "&" : "?") + "rate=16000"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(buf.array()))
                .build();
        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(r -> {
            if (r.statusCode() != 200) throw new IllegalStateException("STT HTTP " + r.statusCode());
            JsonElement text = JsonParser.parseString(r.body()).getAsJsonObject().get("text");
            return text == null ? "" : text.getAsString().trim();
        });
    }

    /** Список голосов сервиса озвучки: [id, gender]. */
    public static CompletableFuture<List<String[]>> voices() {
        String base = LivingVoice.TTS_URL.get();
        if (base.isEmpty()) return CompletableFuture.completedFuture(List.of());
        HttpRequest req = HttpRequest.newBuilder(URI.create(base + "v1/tts/piper/voices")).timeout(Duration.ofSeconds(15)).build();
        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(r -> {
            List<String[]> out = new ArrayList<>();
            for (JsonElement e : JsonParser.parseString(r.body()).getAsJsonObject().getAsJsonArray("voices")) {
                JsonObject o = e.getAsJsonObject();
                out.add(new String[]{o.get("id").getAsString(), o.get("gender").getAsString()});
            }
            return out;
        });
    }

    /** Озвучить фразу: PCM 22050 Гц моно. */
    public static CompletableFuture<short[]> speak(String text, String voice) {
        JsonObject body = new JsonObject();
        body.addProperty("text", text);
        body.addProperty("voice", voice);
        HttpRequest req = HttpRequest.newBuilder(URI.create(LivingVoice.TTS_URL.get() + "v1/tts/piper/speak"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()).thenApply(r -> {
            if (r.statusCode() != 200) throw new IllegalStateException("TTS HTTP " + r.statusCode());
            ByteBuffer b = ByteBuffer.wrap(r.body()).order(ByteOrder.LITTLE_ENDIAN);
            short[] pcm = new short[r.body().length / 2];
            for (int i = 0; i < pcm.length; i++) pcm[i] = b.getShort();
            return pcm;
        });
    }

    /** 48 кГц -> 16 кГц: среднее по тройкам отсчётов (грубый ФНЧ перед прореживанием). */
    public static short[] down48to16(short[] in, int len) {
        short[] out = new short[len / 3];
        for (int i = 0; i < out.length; i++) out[i] = (short) ((in[3 * i] + in[3 * i + 1] + in[3 * i + 2]) / 3);
        return out;
    }

    /** Линейная передискретизация (22050 -> 48000 для голосового чата). */
    public static short[] resample(short[] in, int from, int to) {
        int n = (int) ((long) in.length * to / from);
        short[] out = new short[n];
        for (int i = 0; i < n; i++) {
            double x = (double) i * from / to;
            int j = (int) x;
            double f = x - j;
            int a = in[Math.min(j, in.length - 1)], c = in[Math.min(j + 1, in.length - 1)];
            out[i] = (short) Math.round(a + (c - a) * f);
        }
        return out;
    }
}
