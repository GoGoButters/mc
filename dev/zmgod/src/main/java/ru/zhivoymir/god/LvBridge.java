package ru.zhivoymir.god;

import net.minecraft.server.level.ServerPlayer;
import ru.zhivoymir.livingvoice.LivingVoiceApi;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/** Связь с Living Voice; класс загружается, только если мод установлен. */
final class LvBridge {
    /** Речи бога одному игроку идут по очереди. */
    private static final Map<UUID, CompletableFuture<Void>> QUEUES = new ConcurrentHashMap<>();

    private LvBridge() {}

    static void hook() {
        LivingVoiceApi.phraseHook = Prayers::onVoice;
    }

    static void unhook() {
        LivingVoiceApi.phraseHook = null;
        QUEUES.clear();
    }

    static void speak(ServerPlayer p, String text) {
        String voice = God.VOICE.get();
        float pitch = God.VOICE_PITCH.get().floatValue();
        QUEUES.compute(p.getUUID(), (id, tail) -> (tail == null ? CompletableFuture.<Void>completedFuture(null) : tail)
                .thenCompose(v -> LivingVoiceApi.speakTo(p, text, voice, pitch, true))
                .exceptionally(err -> {
                    God.LOG.warn("[zmgod] voice failed: {}", err.toString());
                    return null;
                }));
    }
}
