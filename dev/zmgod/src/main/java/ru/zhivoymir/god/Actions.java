package ru.zhivoymir.god;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Исполнитель решений бога: только действия белого списка, с потолками силы и кулдаунами. */
final class Actions {
    private static final long MIN = 1200;  // тиков в реальной минуте
    private static final int MAX_ACTIONS = 2;
    private static final int MAX_FAITH_DELTA = 10;
    private static final int MAX_SPEECH = 300;
    private static final int MAX_NOTE = 200;
    /** Действия начинаются, когда бог уже заговорил. */
    private static final int ACTION_DELAY = 60;

    /** Кулдауны действий на игрока (тики). */
    private static final Map<String, Long> COOLDOWN = Map.of(
            "omen", 5 * MIN, "bless", 20 * MIN, "heal", 30 * MIN, "gift", 60 * MIN,
            "curse", 60 * MIN, "invasion", 20 * MIN /* игровые сутки */);
    private static final long WEATHER_COOLDOWN = 20 * MIN;
    /** Проклятие допустимо, если игрок грешил не дольше этого назад (или его вера отрицательная). */
    private static final long SIN_MEMORY = 60 * MIN;
    /** Нашествие — только после проклятия: не раньше и не позже этого. */
    private static final long CURSE_TO_INVASION_MIN = 5 * MIN, CURSE_TO_INVASION_MAX = 180 * MIN;
    static final int INVASION_FAITH = -30;

    private static final Map<String, Holder<MobEffect>> BLESSINGS = Map.ofEntries(
            Map.entry("regeneration", MobEffects.REGENERATION), Map.entry("speed", MobEffects.MOVEMENT_SPEED),
            Map.entry("haste", MobEffects.DIG_SPEED), Map.entry("luck", MobEffects.LUCK),
            Map.entry("resistance", MobEffects.DAMAGE_RESISTANCE), Map.entry("saturation", MobEffects.SATURATION),
            Map.entry("night_vision", MobEffects.NIGHT_VISION), Map.entry("fire_resistance", MobEffects.FIRE_RESISTANCE),
            Map.entry("absorption", MobEffects.ABSORPTION), Map.entry("water_breathing", MobEffects.WATER_BREATHING),
            Map.entry("hero", MobEffects.HERO_OF_THE_VILLAGE));
    private static final Map<String, Holder<MobEffect>> CURSES = Map.of(
            "slowness", MobEffects.MOVEMENT_SLOWDOWN, "weakness", MobEffects.WEAKNESS,
            "mining_fatigue", MobEffects.DIG_SLOWDOWN, "hunger", MobEffects.HUNGER,
            "unluck", MobEffects.UNLUCK, "darkness", MobEffects.DARKNESS);
    private static final Map<String, EntityType<?>> MOBS = Map.of(
            "zombie", EntityType.ZOMBIE, "skeleton", EntityType.SKELETON, "spider", EntityType.SPIDER,
            "husk", EntityType.HUSK, "pillager", EntityType.PILLAGER);
    /** Дары: предмет → наибольшее количество за раз. */
    private static final Map<String, Integer> GIFTS = new LinkedHashMap<>();

    static {
        GIFTS.put("bread", 16);
        GIFTS.put("cooked_beef", 8);
        GIFTS.put("golden_carrot", 8);
        GIFTS.put("apple", 8);
        GIFTS.put("golden_apple", 1);
        GIFTS.put("cake", 1);
        GIFTS.put("wheat_seeds", 16);
        GIFTS.put("oak_sapling", 4);
        GIFTS.put("torch", 32);
        GIFTS.put("iron_ingot", 8);
        GIFTS.put("gold_ingot", 4);
        GIFTS.put("emerald", 5);
        GIFTS.put("lapis_lazuli", 8);
        GIFTS.put("diamond", 1);
        GIFTS.put("experience_bottle", 8);
        GIFTS.put("ender_pearl", 2);
        GIFTS.put("name_tag", 1);
    }

    private Actions() {}

    static String giftList() {
        StringBuilder sb = new StringBuilder();
        GIFTS.forEach((k, v) -> sb.append(sb.isEmpty() ? "" : ", ").append(k).append(" ").append(v));
        return sb.toString();
    }

    /**
     * Проверяет решение и (если execute) исполняет: слова сразу, действия чуть позже.
     * prayer — кто молился (ему бог отвечает, если не сказано иное). Возвращает отчёт по пунктам.
     */
    static List<String> apply(MinecraftServer server, JsonObject d, @Nullable UUID prayer, boolean execute) {
        GodData data = GodData.get(server);
        List<String> report = new ArrayList<>();
        List<Runnable> run = new ArrayList<>();

        String speech = str(d, "speech").trim();
        if (speech.length() > MAX_SPEECH) speech = speech.substring(0, MAX_SPEECH) + "…";
        List<ServerPlayer> listeners = new ArrayList<>();
        String to = str(d, "to").trim();
        if (to.equalsIgnoreCase("all")) listeners.addAll(server.getPlayerList().getPlayers());
        else if (player(server, to) != null) listeners.add(player(server, to));
        else if (prayer != null && server.getPlayerList().getPlayer(prayer) != null) listeners.add(server.getPlayerList().getPlayer(prayer));
        if (!speech.isEmpty() && !listeners.isEmpty()) {
            report.add("слова → " + (to.equalsIgnoreCase("all") ? "всем" : listeners.get(0).getGameProfile().getName()));
            if (execute) {
                String text = speech;
                for (ServerPlayer p : listeners) GodVoice.say(p, text);
            }
        }

        if (d.get("actions") != null && d.get("actions").isJsonArray()) {
            int n = 0;
            for (JsonElement e : d.getAsJsonArray("actions")) {
                if (!e.isJsonObject()) continue;
                if (++n > MAX_ACTIONS) {
                    report.add("лишнее действие отброшено");
                    break;
                }
                try {
                    report.add(act(server, data, e.getAsJsonObject(), execute, run));
                } catch (RuntimeException ex) {
                    report.add("ошибка: " + ex);
                }
            }
        }

        if (d.get("faith") != null && d.get("faith").isJsonObject()) {
            for (var f : d.getAsJsonObject("faith").entrySet()) {
                UUID id = known(server, data, f.getKey());
                if (id == null || !f.getValue().isJsonPrimitive()) continue;
                int delta = Math.max(-MAX_FAITH_DELTA, Math.min(MAX_FAITH_DELTA, f.getValue().getAsInt()));
                if (delta == 0) continue;
                report.add("вера " + f.getKey() + " " + (delta > 0 ? "+" : "") + delta);
                if (execute) data.addFaith(id, delta);
            }
        }
        if (d.get("note") != null && d.get("note").isJsonObject()) {
            for (var f : d.getAsJsonObject("note").entrySet()) {
                UUID id = known(server, data, f.getKey());
                if (id == null || !f.getValue().isJsonPrimitive()) continue;
                String note = f.getValue().getAsString().trim();
                if (note.length() > MAX_NOTE) note = note.substring(0, MAX_NOTE);
                report.add("заметка о " + f.getKey());
                if (execute) {
                    data.notes.put(id, note);
                    data.setDirty();
                }
            }
        }

        if (execute) {
            for (Runnable r : run) Director.later(server, speech.isEmpty() ? 0 : ACTION_DELAY, r);
            boolean acted = !speech.isEmpty() || !run.isEmpty();
            if (acted) {
                StringBuilder h = new StringBuilder(str(d, "thought"));
                if (!speech.isEmpty()) h.append(h.isEmpty() ? "" : " | ").append("сказал «").append(speech).append("»");
                for (String r : report) if (!r.startsWith("отклонено") && !r.startsWith("вера") && !r.startsWith("заметка") && !r.startsWith("слова"))
                    h.append(" | ").append(r);
                data.history(server, h.toString());
            } else if (prayer != null) {
                Director.silence(server, prayer);
            }
        }
        if (report.isEmpty()) report.add("ничего");
        return report;
    }

    private static String act(MinecraftServer server, GodData data, JsonObject a, boolean execute, List<Runnable> run) {
        String type = str(a, "type").toLowerCase(Locale.ROOT);
        long now = GodData.now(server);
        if (type.equals("weather")) {
            if (data.mark("weather") > now) return "отклонено: погода (кулдаун)";
            String kind = str(a, "kind");
            if (!List.of("clear", "rain", "thunder").contains(kind)) return "отклонено: погода " + kind;
            int minutes = clamp(num(a, "minutes", 10), 5, 30);
            if (execute) {
                data.mark("weather", now + WEATHER_COOLDOWN);
                int ticks = minutes * (int) MIN;
                run.add(() -> server.overworld().setWeatherParameters(kind.equals("clear") ? ticks : 0,
                        kind.equals("clear") ? 0 : ticks, !kind.equals("clear"), kind.equals("thunder")));
            }
            return "погода " + kind + " " + minutes + " мин";
        }
        if (type.equals("none") || type.isEmpty()) return "ничего";
        if (!COOLDOWN.containsKey(type)) return "отклонено: неизвестное действие " + type;

        String targetName = str(a, "target");
        ServerPlayer target = player(server, targetName);
        if (target == null) return "отклонено: " + type + " — нет игрока " + targetName;
        UUID id = target.getUUID();
        String cd = "cd|" + type + "|" + id;
        if (data.mark(cd) > now) return "отклонено: " + type + " " + targetName + " — кулдаун ещё " + Prompt.minutes(data.mark(cd) - now);
        String desc;
        Runnable r;
        switch (type) {
            case "omen" -> {
                String kind = str(a, "kind");
                if (!List.of("lightning", "thunder", "bell", "stars", "cave").contains(kind)) return "отклонено: знамение " + kind;
                desc = "знамение " + kind + " у " + targetName;
                r = () -> omen(target, kind);
            }
            case "bless" -> {
                Holder<MobEffect> effect = BLESSINGS.get(str(a, "effect"));
                if (effect == null) return "отклонено: благословение " + str(a, "effect");
                int minutes = clamp(num(a, "minutes", 3), 1, 10), level = clamp(num(a, "level", 1), 1, 2);
                desc = "благословение " + str(a, "effect") + " " + level + " на " + minutes + " мин → " + targetName;
                r = () -> {
                    target.addEffect(new MobEffectInstance(effect, minutes * (int) MIN, level - 1));
                    sparkle(target, true);
                };
            }
            case "heal" -> {
                desc = "исцеление " + targetName;
                r = () -> {
                    target.setHealth(target.getMaxHealth());
                    target.getFoodData().eat(20, 1.0f);
                    target.removeEffect(MobEffects.POISON);
                    target.removeEffect(MobEffects.WITHER);
                    sparkle(target, true);
                };
            }
            case "gift" -> {
                String item = str(a, "item").replace("minecraft:", "");
                Integer max = GIFTS.get(item);
                if (max == null) return "отклонено: дар " + item;
                int count = clamp(num(a, "count", 1), 1, max);
                Item it = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(item));
                desc = "дар " + item + " ×" + count + " → " + targetName;
                r = () -> {
                    ItemStack stack = new ItemStack(it, count);
                    if (!target.getInventory().add(stack)) target.drop(stack, false);
                    sparkle(target, true);
                };
            }
            case "curse" -> {
                Holder<MobEffect> effect = CURSES.get(str(a, "effect"));
                if (effect == null) return "отклонено: проклятие " + str(a, "effect");
                if (data.faith(id) >= 0 && now - data.mark("sin|" + id) > SIN_MEMORY)
                    return "отклонено: проклятие " + targetName + " — не грешник";
                int minutes = clamp(num(a, "minutes", 2), 1, effect == MobEffects.DARKNESS ? 1 : 5);
                desc = "проклятие " + str(a, "effect") + " на " + minutes + " мин → " + targetName;
                if (execute) data.mark("curse|" + id, now);
                r = () -> {
                    target.addEffect(new MobEffectInstance(effect, minutes * (int) MIN, 0));
                    sparkle(target, false);
                };
            }
            case "invasion" -> {
                EntityType<?> mob = MOBS.get(str(a, "mob"));
                if (mob == null) return "отклонено: нашествие " + str(a, "mob");
                long sinceCurse = now - data.mark("curse|" + id);
                if (data.faith(id) > INVASION_FAITH || sinceCurse < CURSE_TO_INVASION_MIN || sinceCurse > CURSE_TO_INVASION_MAX)
                    return "отклонено: нашествие на " + targetName + " — нужна вера ≤ " + INVASION_FAITH + " и недавнее проклятие";
                if (target.serverLevel().getDifficulty() == Difficulty.PEACEFUL) {
                    // На мирной чудовища исчезают сразу — вместо нашествия тяжкая кара.
                    desc = "кара вместо нашествия (мирная сложность) → " + targetName;
                    r = () -> wrath(target);
                    break;
                }
                int count = clamp(num(a, "count", 3), 2, 5);
                desc = "нашествие " + str(a, "mob") + " ×" + count + " на " + targetName;
                r = () -> invasion(target, mob, count);
            }
            default -> {
                return "отклонено: " + type;
            }
        }
        if (execute) {
            data.mark(cd, now + COOLDOWN.get(type));
            run.add(() -> {
                if (!target.isRemoved() && target.isAlive()) r.run();
            });
        }
        return desc;
    }

    private static void omen(ServerPlayer p, String kind) {
        ServerLevel level = p.serverLevel();
        switch (kind) {
            case "lightning" -> {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt == null) return;
                BlockPos pos = around(level, p, 8, 14);
                bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
            case "thunder" -> sound(p, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.7f);
            case "bell" -> {
                sound(p, SoundEvents.BELL_BLOCK, 0.6f);
                sound(p, SoundEvents.BELL_RESONATE, 0.8f);
            }
            case "cave" -> sound(p, SoundEvents.AMBIENT_CAVE.value(), 0.8f);
            case "stars" -> level.sendParticles(ParticleTypes.END_ROD, p.getX(), p.getY() + 1.5, p.getZ(), 80, 2.5, 1.5, 2.5, 0.02);
            default -> {}
        }
    }

    private static void sound(ServerPlayer p, SoundEvent s, float pitch) {
        p.serverLevel().playSound(null, p.getX(), p.getY(), p.getZ(), s, SoundSource.AMBIENT, 2.0f, pitch);
    }

    /** Знак благодати (золотые искры) или гнева (дым). */
    static void sparkle(ServerPlayer p, boolean good) {
        p.serverLevel().sendParticles(good ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.LARGE_SMOKE,
                p.getX(), p.getY() + 1, p.getZ(), good ? 30 : 20, 0.6, 0.8, 0.6, 0.02);
        if (good) p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 0.8f);
        else p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.4f, 0.6f);
    }

    private static void invasion(ServerPlayer p, EntityType<?> type, int count) {
        ServerLevel level = p.serverLevel();
        for (int i = 0; i < count; i++) {
            Entity e = type.create(level);
            if (!(e instanceof Mob mob)) return;
            BlockPos pos = around(level, p, 14, 22);
            mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ThreadLocalRandom.current().nextFloat() * 360, 0);
            EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
            mob.setTarget(p);
            level.addFreshEntity(mob);
        }
        p.displayClientMessage(Component.literal("Земля дрогнула под ногами…").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), true);
    }

    /** Кара на мирной сложности: проклятия второй степени на 8 минут, тьма и молния-знамение. */
    private static void wrath(ServerPlayer p) {
        int ticks = 8 * (int) MIN;
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.HUNGER, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, (int) MIN, 0));
        omen(p, "lightning");
        sparkle(p, false);
        p.displayClientMessage(Component.literal("Небеса отвернулись от тебя…").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), true);
    }

    /** Точка на поверхности в кольце [min, max] блоков от игрока. */
    private static BlockPos around(ServerLevel level, ServerPlayer p, int min, int max) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        double ang = rnd.nextDouble(Math.PI * 2), r = rnd.nextDouble(min, max);
        BlockPos xz = BlockPos.containing(p.getX() + Math.cos(ang) * r, p.getY(), p.getZ() + Math.sin(ang) * r);
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, xz);
        // В пещере или в Незере поверхность далеко: тогда на уровне игрока.
        return Math.abs(top.getY() - p.getBlockY()) > 12 || level.dimensionType().hasCeiling() ? xz : top;
    }

    /** Действия на будущее: что сейчас недоступно игроку (для сводки). */
    static String unavailable(MinecraftServer server, ServerPlayer p) {
        GodData data = GodData.get(server);
        long now = GodData.now(server);
        List<String> out = new ArrayList<>();
        for (String type : COOLDOWN.keySet()) {
            long until = data.mark("cd|" + type + "|" + p.getUUID());
            if (until > now) out.add(type + " (ещё " + Prompt.minutes(until - now) + ")");
        }
        UUID id = p.getUUID();
        if (data.faith(id) >= 0 && now - data.mark("sin|" + id) > SIN_MEMORY && out.stream().noneMatch(x -> x.startsWith("curse")))
            out.add("curse (не грешник)");
        long sinceCurse = now - data.mark("curse|" + id);
        if (data.faith(id) > INVASION_FAITH || sinceCurse < CURSE_TO_INVASION_MIN || sinceCurse > CURSE_TO_INVASION_MAX)
            out.add("invasion");
        out.sort(null);
        return String.join(", ", out);
    }

    @Nullable
    private static ServerPlayer player(MinecraftServer server, String name) {
        return name == null || name.isEmpty() ? null : server.getPlayerList().getPlayerByName(name);
    }

    @Nullable
    private static UUID known(MinecraftServer server, GodData data, String name) {
        ServerPlayer p = player(server, name);
        if (p != null) return p.getUUID();
        for (var e : data.names.entrySet()) if (e.getValue().equalsIgnoreCase(name)) return e.getKey();
        return null;
    }

    static String str(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() ? e.getAsString() : "";
    }

    private static int num(JsonObject o, String key, int def) {
        try {
            JsonElement e = o.get(key);
            return e != null && e.isJsonPrimitive() ? (int) Math.round(e.getAsDouble()) : def;
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
