package ru.zhivoymir.livingvoice;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.PlayerDisconnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStoppedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Плагин Simple Voice Chat: собирает фразы игроков (между нажатием и отпусканием кнопки) и отдаёт их жителям. */
@ForgeVoicechatPlugin
public final class VoicePlugin implements VoicechatPlugin {
    private static final int RATE = 48000;
    private static final int MAX_SAMPLES = RATE * 30;
    private static final int MIN_SAMPLES = RATE * 3 / 10;

    static volatile VoicechatServerApi api;
    private static final Map<UUID, Phrase> PHRASES = new ConcurrentHashMap<>();
    private static ScheduledExecutorService ticker;

    /** Фраза, которую игрок сейчас произносит. */
    private static final class Phrase {
        final ServerPlayer player;
        final OpusDecoder decoder;
        short[] pcm = new short[RATE * 4];
        int len;
        long last;
        int packets;
        long maxGap;
        volatile Entity looked;
        volatile boolean anyone;
        volatile boolean targetKnown;

        Phrase(ServerPlayer player, OpusDecoder decoder) {
            this.player = player;
            this.decoder = decoder;
        }
    }

    @Override
    public String getPluginId() {
        return LivingVoice.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        LivingVoice.LOG.info("[livingvoice] Simple Voice Chat plugin loaded");
    }

    @Override
    public void registerEvents(EventRegistration reg) {
        reg.registerEvent(VoicechatServerStartedEvent.class, e -> {
            api = e.getVoicechat();
            ticker = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "livingvoice-phrases");
                t.setDaemon(true);
                return t;
            });
            ticker.scheduleWithFixedDelay(VoicePlugin::finishPhrases, 100, 100, TimeUnit.MILLISECONDS);
            ColonistVoice.reset();
        });
        reg.registerEvent(VoicechatServerStoppedEvent.class, e -> {
            if (ticker != null) ticker.shutdownNow();
            PHRASES.values().forEach(p -> p.decoder.close());
            PHRASES.clear();
            api = null;
        });
        reg.registerEvent(PlayerDisconnectedEvent.class, e -> {
            Phrase p = PHRASES.remove(e.getPlayerUuid());
            if (p != null) p.decoder.close();
        });
        reg.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    private void onMicrophone(MicrophonePacketEvent e) {
        VoicechatConnection con = e.getSenderConnection();
        VoicechatServerApi a = api;
        if (con == null || a == null || LivingVoice.STT_URL.get().isEmpty()) return;
        if (!(con.getPlayer().getPlayer() instanceof ServerPlayer player)) return;
        byte[] opus = e.getPacket().getOpusEncodedData();
        if (opus.length == 0) return;
        Phrase p = PHRASES.computeIfAbsent(player.getUUID(), id -> new Phrase(player, a.createDecoder()));
        synchronized (p) {
            if (p.len == 0) {  // начало фразы: кому игрок говорит — тот, на кого смотрит сейчас
                p.targetKnown = false;
                player.server.execute(() -> {
                    p.looked = Targets.looked(player);
                    p.anyone = p.looked != null || !Targets.nearby(player).isEmpty();
                    p.targetKnown = true;
                });
            }
            short[] frame;
            try {
                frame = p.decoder.decode(opus);
            } catch (Exception ex) {  // битый пакет не должен ломать голосовой чат
                LivingVoice.LOG.debug("[livingvoice] opus decode failed: {}", ex.toString());
                return;
            }
            if (p.len + frame.length > MAX_SAMPLES) return;
            if (p.len + frame.length > p.pcm.length) p.pcm = Arrays.copyOf(p.pcm, Math.min(MAX_SAMPLES, p.pcm.length * 2));
            long now = System.currentTimeMillis();
            if (p.len > 0) p.maxGap = Math.max(p.maxGap, now - p.last);
            System.arraycopy(frame, 0, p.pcm, p.len, frame.length);
            p.len += frame.length;
            p.packets++;
            p.last = now;
        }
    }

    /** Фраза закончилась, если пакеты не приходят silenceMs (игрок отпустил кнопку). */
    private static void finishPhrases() {
        long now = System.currentTimeMillis();
        int silence = LivingVoice.SILENCE_MS.get();
        for (Phrase p : PHRASES.values()) {
            short[] pcm;
            int len;
            Entity looked;
            boolean anyone;
            String stats;
            synchronized (p) {
                if (p.len == 0 || now - p.last < silence || !p.targetKnown) continue;
                pcm = p.pcm;
                len = p.len;
                looked = p.looked;
                anyone = p.anyone;
                stats = p.packets + " packets, max gap " + p.maxGap + " ms";
                p.pcm = new short[RATE * 4];
                p.len = 0;
                p.packets = 0;
                p.maxGap = 0;
                p.decoder.resetState();
            }
            ServerPlayer player = p.player;
            String dump = Dump.save(player.getGameProfile().getName(), pcm, len, RATE, stats);
            if (len < MIN_SAMPLES) continue;
            if (!anyone) {
                player.server.execute(() -> nobody(player));
                continue;
            }
            long t0 = System.currentTimeMillis();
            Http.recognize(Http.down48to16(pcm, len)).whenComplete((text, err) -> player.server.execute(() -> {
                Dump.text(dump, err != null ? "ERROR " + err : text);
                if (err != null) {
                    LivingVoice.LOG.warn("[livingvoice] STT failed: {}", err.toString());
                    player.displayClientMessage(Component.literal("Не удалось распознать речь: сервис недоступен").withStyle(ChatFormatting.RED), true);
                    return;
                }
                if (text.isEmpty()) {
                    player.displayClientMessage(Component.literal("Не разобрал, повторите").withStyle(ChatFormatting.GRAY), true);
                    return;
                }
                Entity target = Targets.resolve(player, looked, text);
                LivingVoice.LOG.info("[livingvoice] {} -> {} ({} s audio, {}, STT {} ms): {}", player.getGameProfile().getName(),
                        target == null ? "nobody" : target.getName().getString(), String.format("%.1f", len / (double) RATE), stats,
                        System.currentTimeMillis() - t0, text);
                if (target == null) {
                    nobody(player);
                    return;
                }
                Targets.say(player, target, text);
            }));
        }
    }

    private static void nobody(ServerPlayer player) {
        player.displayClientMessage(Component.literal(
                "Вас никто не слушает — посмотрите на жителя или позовите его по имени").withStyle(ChatFormatting.GRAY), true);
    }
}
