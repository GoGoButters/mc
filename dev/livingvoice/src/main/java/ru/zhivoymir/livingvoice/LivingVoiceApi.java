package ru.zhivoymir.livingvoice;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

/** Для других модов сервера (ИИ-бог): свои фразы из речи игрока и голос без источника в мире. */
public final class LivingVoiceApi {
    private LivingVoiceApi() {
    }

    /**
     * Видит каждую распознанную фразу раньше жителей, даже когда рядом никого нет; вызывается в серверном потоке.
     * Вернул true — фраза его, жителям она не уходит.
     */
    public static volatile BiPredicate<ServerPlayer, String> phraseHook;

    /**
     * Голос «в голове» игрока: звучит без направления и расстояния. {@code pitch} &lt; 1 — ниже и медленнее,
     * {@code echo} — гулкое эхо. Без голосового чата у игрока или без сервиса озвучки завершается сразу.
     */
    public static CompletableFuture<Void> speakTo(ServerPlayer player, String text, String voice, float pitch, boolean echo) {
        VoicechatServerApi api = VoicePlugin.api;
        if (api == null || LivingVoice.TTS_URL.get().isEmpty()) return CompletableFuture.completedFuture(null);
        VoicechatConnection con = api.getConnectionOf(player.getUUID());
        if (con == null || !con.isConnected()) return CompletableFuture.completedFuture(null);
        StaticAudioChannel channel = api.createStaticAudioChannel(UUID.randomUUID());
        if (channel == null) return CompletableFuture.completedFuture(null);
        channel.addTarget(con);
        channel.setBypassGroupIsolation(true);
        return Http.speak(text, voice).thenCompose(pcm -> {
            // Звук 22050 Гц, выданный за более низкую частоту, при переводе в 48 кГц звучит ниже и медленнее.
            short[] out = Http.resample(pcm, Math.round(22050 * Math.max(0.5f, Math.min(1.5f, pitch))), 48000);
            if (echo) out = echo(out);
            CompletableFuture<Void> done = new CompletableFuture<>();
            AudioPlayer ap = api.createAudioPlayer(channel, api.createEncoder(), out);
            ap.setOnStopped(() -> done.complete(null));
            ap.startPlaying();
            return done;
        });
    }

    /** Два затухающих отражения (140 и 290 мс) и запас тишины в конце под хвост эха. */
    private static short[] echo(short[] in) {
        int d1 = 48000 * 140 / 1000, d2 = 48000 * 290 / 1000;
        short[] out = new short[in.length + d2];
        for (int i = 0; i < out.length; i++) {
            double v = (i < in.length ? in[i] * 0.8 : 0)
                    + (i >= d1 && i - d1 < in.length ? in[i - d1] * 0.32 : 0)
                    + (i >= d2 && i - d2 < in.length ? in[i - d2] * 0.15 : 0);
            out[i] = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, Math.round(v)));
        }
        return out;
    }
}
