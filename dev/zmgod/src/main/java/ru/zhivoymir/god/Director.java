package ru.zhivoymir.god;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Режиссёр: собирает сводку, спрашивает LLM, передаёт решение исполнителю. Один запрос за раз, остальные ждут. */
final class Director {
    /** Повод обратиться к богу. prayer — кто молится (на него бог и отвечает). */
    record Trigger(String reason, @Nullable UUID prayer, boolean dry, @Nullable CommandSourceStack reply) {}

    private static final int MAX_PENDING = 4;
    private static final Deque<Trigger> PENDING = new ArrayDeque<>();
    private static boolean busy;
    /** Отложенные действия: исполняются после слов бога, а не одновременно с ними. */
    private static final List<Object[]> LATER = new ArrayList<>();

    private Director() {}

    static void reset() {
        PENDING.clear();
        LATER.clear();
        busy = false;
    }

    static void later(MinecraftServer server, int ticks, Runnable r) {
        LATER.add(new Object[]{server.getTickCount() + ticks, r});
    }

    static void onServerTick(MinecraftServer server) {
        if (!LATER.isEmpty()) {
            for (Object[] l : new ArrayList<>(LATER)) {
                if ((int) l[0] > server.getTickCount()) continue;
                LATER.remove(l);
                try {
                    ((Runnable) l[1]).run();
                } catch (RuntimeException e) {
                    God.LOG.warn("[zmgod] action failed", e);
                }
            }
        }
        if (server.getTickCount() % 20 != 0) return;
        GodData data = GodData.get(server);
        long now = GodData.now(server);
        if (data.nextVisit == 0 || data.nextVisit - now > 1200L * God.INTERVAL_MAX.get()) scheduleVisit(data, now);
        if (now < data.nextVisit) return;
        scheduleVisit(data, now);
        if (God.ENABLED.get() && !server.getPlayerList().getPlayers().isEmpty()) {
            request(server, new Trigger("плановый обход мира", null, false, null));
        }
    }

    private static void scheduleVisit(GodData data, long now) {
        int min = God.INTERVAL_MIN.get(), max = Math.max(min, God.INTERVAL_MAX.get());
        data.nextVisit = now + 1200L * ThreadLocalRandom.current().nextInt(min, max + 1);
        data.setDirty();
    }

    /** Вызывать в серверном потоке. */
    static void request(MinecraftServer server, Trigger t) {
        if (busy) {
            if (PENDING.size() < MAX_PENDING) PENDING.add(t);
            else God.LOG.info("[zmgod] dropped trigger: {}", t.reason());
            return;
        }
        busy = true;
        String user = Prompt.summary(server, t);
        String model = t.prayer() != null ? God.PRAYER_MODEL.get() : God.MODEL.get();
        long t0 = System.currentTimeMillis();
        God.LOG.debug("[zmgod] prompt:\n{}", user);
        Broker.chat(model, Prompt.SYSTEM, user).whenComplete((answer, err) -> server.execute(() -> {
            busy = false;
            try {
                long ms = System.currentTimeMillis() - t0;
                if (err != null) {
                    God.LOG.warn("[zmgod] broker failed ({} ms): {}", ms, err.toString());
                    tell(t, Component.literal("Бог не ответил: " + err.getMessage()).withStyle(ChatFormatting.RED));
                    if (t.prayer() != null) silence(server, t.prayer());
                } else {
                    handle(server, t, answer, ms);
                }
            } finally {
                Trigger next = PENDING.poll();
                if (next != null) request(server, next);
            }
        }));
    }

    private static void handle(MinecraftServer server, Trigger t, String answer, long ms) {
        JsonObject decision;
        try {
            int a = answer.indexOf('{'), b = answer.lastIndexOf('}');
            decision = JsonParser.parseString(answer.substring(a, b + 1)).getAsJsonObject();
        } catch (RuntimeException e) {
            God.LOG.warn("[zmgod] bad answer ({} ms): {}", ms, answer);
            tell(t, Component.literal("Ответ не JSON: " + answer).withStyle(ChatFormatting.RED));
            if (t.prayer() != null) silence(server, t.prayer());
            return;
        }
        boolean dry = t.dry() || God.DRY_RUN.get();
        List<String> done = Actions.apply(server, decision, t.prayer(), !dry);
        God.LOG.info("[zmgod] {} ({} ms, {}): {} -> {}", t.reason(), ms, dry ? "dry" : "live", decision, done);
        tell(t, Component.literal(decision + "\n→ " + String.join("; ", done) + " (" + ms + " мс)").withStyle(ChatFormatting.GRAY));
    }

    /** Молитва осталась без ответа: игрок всё же должен понять, что его услышали. */
    static void silence(MinecraftServer server, UUID player) {
        ServerPlayer p = server.getPlayerList().getPlayer(player);
        if (p != null) p.displayClientMessage(Component.literal("Небеса молчат…").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
    }

    private static void tell(Trigger t, Component msg) {
        if (t.reply() != null) t.reply().sendSuccess(() -> msg, false);
    }
}
