package ru.zhivoymir.client;

import net.conczin.mca.client.tts.SpeechManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Общая очередь озвучки жителей MCA: одновременно звучит один голос. Звук качается сразу,
 * играет после окончания предыдущей реплики. Реплики, прождавшие слишком долго, пропускаются;
 * {@link #interrupt()} обрывает текущую и сбрасывает ожидающие (новый ответ собеседника).
 */
public final class SpeechQueue {
    private static final long GAP_MS = 300;
    private static final long STALE_MS = 20_000;
    private static final long MAX_LINE_MS = 60_000;

    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "zmclient-speech");
        t.setDaemon(true);
        return t;
    });
    private static final AtomicInteger GENERATION = new AtomicInteger();
    private static CompletableFuture<Void> tail = CompletableFuture.completedFuture(null);
    private static volatile SoundInstance playing;

    private SpeechQueue() {}

    /**
     * Ставит реплику в очередь. {@code fetch} скачивает звук и возвращает его адрес или null.
     * {@code prefetch} — качать сразу; нельзя для некэшируемых реплик: MCA пишет их все в один файл.
     */
    public static synchronized void enqueue(Entity entity, float pitch, boolean prefetch, Supplier<ResourceLocation> fetch) {
        int gen = GENERATION.get();
        long queuedAt = System.currentTimeMillis();
        CompletableFuture<ResourceLocation> sound = prefetch
                ? CompletableFuture.supplyAsync(() -> current(gen, queuedAt) ? fetch.get() : null, POOL) : null;
        tail = tail.handle((v, e) -> null).thenAcceptAsync(v -> {
            if (!current(gen, queuedAt)) return;
            ResourceLocation loc = sound != null ? sound.exceptionally(e -> null).join() : fetch.get();
            if (loc != null && current(gen, queuedAt)) playAndWait(entity, pitch, loc, gen);
        }, POOL).exceptionally(e -> null);
    }

    /** Обрывает текущую реплику и сбрасывает ожидающие. */
    public static void interrupt() {
        GENERATION.incrementAndGet();
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            SoundInstance s = playing;
            if (s != null) mc.getSoundManager().stop(s);
        });
    }

    private static boolean current(int gen, long queuedAt) {
        return gen == GENERATION.get() && System.currentTimeMillis() - queuedAt < STALE_MS;
    }

    private static void playAndWait(Entity entity, float pitch, ResourceLocation loc, int gen) {
        Minecraft mc = Minecraft.getInstance();
        SoundInstance inst = mc.submit(() -> {
            if (gen != GENERATION.get()) return null;
            SoundInstance s = SpeechManager.INSTANCE.getSound(pitch, entity, loc);
            playing = s;
            mc.getSoundManager().play(s);
            return s;
        }).join();
        if (inst == null) return;
        long end = System.currentTimeMillis() + MAX_LINE_MS;
        try {
            Thread.sleep(200);
            while (gen == GENERATION.get() && System.currentTimeMillis() < end
                    && mc.submit(() -> mc.getSoundManager().isActive(inst)).join()) {
                Thread.sleep(100);
            }
            if (gen == GENERATION.get()) Thread.sleep(GAP_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (playing == inst) playing = null;
        }
    }
}
