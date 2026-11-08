package ru.zhivoymir.client;

import net.conczin.mca.client.tts.SpeechManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/** Очередь озвучки по жителю: звук качается сразу, играет после окончания предыдущей реплики. */
public final class SpeechQueue {
    private static final long GAP_MS = 300;
    private static final long MAX_LINE_MS = 60_000;

    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "zmclient-speech");
        t.setDaemon(true);
        return t;
    });
    private static final Map<UUID, CompletableFuture<Void>> TAILS = new ConcurrentHashMap<>();

    private SpeechQueue() {}

    /**
     * Ставит реплику в очередь жителя. {@code fetch} скачивает звук и возвращает его адрес или null.
     * {@code prefetch} — качать сразу; нельзя для некэшируемых реплик: MCA пишет их все в один файл.
     */
    public static void enqueue(Entity entity, float pitch, boolean prefetch, Supplier<ResourceLocation> fetch) {
        CompletableFuture<ResourceLocation> sound = prefetch ? CompletableFuture.supplyAsync(fetch, POOL) : null;
        TAILS.compute(entity.getUUID(), (id, prev) -> {
            CompletableFuture<Void> after = prev == null ? CompletableFuture.completedFuture(null) : prev;
            return after.handle((v, e) -> null)
                    .thenAcceptAsync(v -> {
                        ResourceLocation loc = sound != null ? sound.exceptionally(e -> null).join() : fetch.get();
                        if (loc != null) playAndWait(entity, pitch, loc);
                    }, POOL)
                    .exceptionally(e -> null);
        });
    }

    private static void playAndWait(Entity entity, float pitch, ResourceLocation loc) {
        Minecraft mc = Minecraft.getInstance();
        SoundInstance inst = mc.submit(() -> {
            SoundInstance s = SpeechManager.INSTANCE.getSound(pitch, entity, loc);
            mc.getSoundManager().play(s);
            return s;
        }).join();
        long end = System.currentTimeMillis() + MAX_LINE_MS;
        try {
            Thread.sleep(200);
            while (System.currentTimeMillis() < end && mc.submit(() -> mc.getSoundManager().isActive(inst)).join()) {
                Thread.sleep(100);
            }
            Thread.sleep(GAP_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
