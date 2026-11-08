package ru.zhivoymir.god;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/** /god — отладка бога (уровень прав 2). */
final class GodCommands {
    private GodCommands() {}

    static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("god").requires(s -> s.hasPermission(2))
                .then(Commands.literal("status").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal(status(ctx.getSource())), false);
                    return 1;
                }))
                .then(Commands.literal("events").executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    GodData data = GodData.get(server);
                    long now = GodData.now(server);
                    StringBuilder sb = new StringBuilder("События:");
                    data.events.stream().skip(Math.max(0, data.events.size() - 15))
                            .forEach(e -> sb.append("\n  ").append(Prompt.ago(now - e.time())).append(": ").append(e.text()));
                    sb.append("\nВмешательства:");
                    data.history.forEach(e -> sb.append("\n  ").append(Prompt.ago(now - e.time())).append(": ").append(e.text()));
                    ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
                    return 1;
                }))
                .then(Commands.literal("visit")
                        .executes(ctx -> visit(ctx.getSource(), false))
                        .then(Commands.literal("dry").executes(ctx -> visit(ctx.getSource(), true))))
                .then(Commands.literal("prompt").executes(ctx -> {
                    String s = Prompt.summary(ctx.getSource().getServer(), new Director.Trigger("проверка сводки", null, true, null));
                    ctx.getSource().sendSuccess(() -> Component.literal(s), false);
                    return 1;
                }))
                .then(Commands.literal("pray").then(Commands.argument("text", StringArgumentType.greedyString()).executes(ctx -> {
                    Prayers.pray(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "text"), "команда", true);
                    return 1;
                })))
                .then(Commands.literal("say").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("text", StringArgumentType.greedyString()).executes(ctx -> {
                            String text = StringArgumentType.getString(ctx, "text");
                            for (ServerPlayer p : EntityArgument.getPlayers(ctx, "targets")) GodVoice.say(p, text);
                            return 1;
                        }))))
                .then(Commands.literal("faith").then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("value", IntegerArgumentType.integer(GodData.MIN_FAITH, GodData.MAX_FAITH)).executes(ctx -> {
                            ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                            GodData data = GodData.get(p.server);
                            data.addFaith(p.getUUID(), IntegerArgumentType.getInteger(ctx, "value") - data.faith(p.getUUID()));
                            ctx.getSource().sendSuccess(() -> Component.literal("Вера " + p.getGameProfile().getName() + ": " + data.faith(p.getUUID())), true);
                            return 1;
                        }))))
                .then(Commands.literal("do").then(Commands.argument("json", StringArgumentType.greedyString()).executes(ctx -> {
                    JsonObject o = JsonParser.parseString(StringArgumentType.getString(ctx, "json")).getAsJsonObject();
                    ServerPlayer self = ctx.getSource().getPlayer();
                    List<String> r = Actions.apply(ctx.getSource().getServer(), o, self == null ? null : self.getUUID(), true);
                    ctx.getSource().sendSuccess(() -> Component.literal("→ " + String.join("; ", r)), true);
                    return 1;
                }))));
    }

    private static int visit(CommandSourceStack src, boolean dry) {
        Director.request(src.getServer(), new Director.Trigger(dry ? "проверка (без действий)" : "обход по воле администратора", null, dry, src));
        src.sendSuccess(() -> Component.literal("Бог смотрит на мир…"), false);
        return 1;
    }

    private static String status(CommandSourceStack src) {
        MinecraftServer server = src.getServer();
        GodData data = GodData.get(server);
        long now = GodData.now(server);
        StringBuilder sb = new StringBuilder();
        sb.append("Бог: ").append(God.ENABLED.get() ? "бодрствует" : "спит (только молитвы)").append(God.DRY_RUN.get() ? ", пробный режим" : "")
                .append("\n  следующий обход через ").append(Prompt.minutes(Math.max(0, data.nextVisit - now)))
                .append("\n  событий в летописи: ").append(data.events.size()).append(", вмешательств: ").append(data.history.size())
                .append("\n  Living Voice: ").append(God.LIVING_VOICE ? "есть" : "нет")
                .append(", ключ брокера: ").append(God.apiKey().isEmpty() ? "НЕТ" : "есть");
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            int f = data.faith(p.getUUID());
            sb.append("\n").append(p.getGameProfile().getName()).append(": вера ").append(f).append(" (").append(Prompt.faithWord(f)).append(")")
                    .append(Prayers.nearAltar(p) ? ", у алтаря" : "");
            String note = data.notes.get(p.getUUID());
            if (note != null) sb.append("\n  заметка: ").append(note);
            String blocked = Actions.unavailable(server, p);
            if (!blocked.isEmpty()) sb.append("\n  недоступно: ").append(blocked);
        }
        return sb.toString();
    }
}
