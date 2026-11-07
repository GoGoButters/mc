package ru.zhivoymir.livingvoice;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import me.bytefox.livingcolonists.talk.Audience;
import me.bytefox.livingcolonists.talk.Speaker;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/** Голоса колонистов: реплика -> сервис озвучки -> звук от колониста через голосовой чат. */
public final class ColonistVoice {
    private static volatile CompletableFuture<List<String[]>> voices;
    /** Очередь реплик каждого колониста, чтобы он не говорил двумя голосами сразу. */
    private static final Map<String, CompletableFuture<Void>> QUEUES = new ConcurrentHashMap<>();

    private ColonistVoice() {
    }

    static void reset() {
        voices = null;
        QUEUES.clear();
    }

    /** Вызывается из ChatSpeechOutputMixin в серверном потоке; near — игрок рядом (для измерения колонии). */
    public static void say(Speaker speaker, ServerPlayer near, String text) {
        VoicechatServerApi api = VoicePlugin.api;
        if (api == null || near == null || !LivingVoice.COLONIST_VOICES.get() || LivingVoice.TTS_URL.get().isEmpty()) return;
        if (!near.server.isSameThread()) {  // LC может выдать реплику из потока ИИ
            near.server.execute(() -> say(speaker, near, text));
            return;
        }
        ICitizenData citizen = Audience.resolve(near.level().dimension(), speaker.colonyId(), speaker.citizenId());
        if (citizen == null) return;
        AbstractEntityCitizen entity = citizen.getEntity().orElse(null);
        if (entity == null) return;
        EntityAudioChannel channel = api.createEntityAudioChannel(UUID.randomUUID(), api.fromEntity(entity));
        if (channel == null) return;
        channel.setDistance(LivingVoice.VOICE_DISTANCE.get().floatValue());
        String key = speaker.colonyId() + ":" + speaker.citizenId();
        boolean female = citizen.isFemale();
        QUEUES.compute(key, (k, tail) -> (tail == null ? CompletableFuture.<Void>completedFuture(null) : tail)
                .thenCompose(v -> voiceFor(key, female))
                .thenCompose(voice -> Http.speak(text, voice))
                .thenCompose(pcm -> play(api, channel, Http.resample(pcm, 22050, 48000)))
                .exceptionally(err -> {
                    LivingVoice.LOG.warn("[livingvoice] colonist voice failed: {}", err.toString());
                    return null;
                }));
    }

    /** Постоянный голос колониста: по его номеру из голосов нужного пола. */
    private static CompletableFuture<String> voiceFor(String key, boolean female) {
        CompletableFuture<List<String[]>> v = voices;
        if (v == null || v.isCompletedExceptionally()) voices = v = Http.voices();
        return v.thenApply(all -> {
            List<String> same = new ArrayList<>();
            for (String[] x : all) if (x[1].equals(female ? "female" : "male")) same.add(x[0]);
            if (same.isEmpty()) throw new IllegalStateException("no voices");
            return same.get(Math.floorMod(key.hashCode() * 31 + 7, same.size()));
        });
    }

    private static CompletableFuture<Void> play(VoicechatServerApi api, EntityAudioChannel channel, short[] pcm48k) {
        CompletableFuture<Void> done = new CompletableFuture<>();
        AudioPlayer player = api.createAudioPlayer(channel, api.createEncoder(), pcm48k);
        player.setOnStopped(() -> done.complete(null));
        LivingVoice.LOG.info("[livingvoice] colonist voice: {} s", String.format("%.1f", pcm48k.length / 48000.0));
        player.startPlaying();
        return done;
    }
}
