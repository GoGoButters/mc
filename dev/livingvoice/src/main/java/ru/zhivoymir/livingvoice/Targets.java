package ru.zhivoymir.livingvoice;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Кому игрок говорит. По порядку: тот, кого он назвал по имени, затем по профессии («эй, строитель!»),
 * затем тот, на кого он смотрит. Если игрок на кого-то смотрит, обращение засчитывается только в начале фразы,
 * иначе «что думаешь об Иване?» ушло бы Ивану, а не собеседнику.
 */
public final class Targets {
    private static final boolean MCA = ModList.get().isLoaded("mca");
    private static final boolean COLONISTS = ModList.get().isLoaded("livingcolonists");
    /** Насколько можно промахнуться взглядом мимо собеседника, блоков. */
    private static final double SLACK = 0.6;
    /** Сколько первых слов фразы считаются обращением, когда игрок смотрит на собеседника. */
    private static final int VOCATIVE_WORDS = 3;

    private Targets() {
    }

    static boolean isTalker(Entity e) {
        return (MCA && McaBridge.isVillager(e)) || (COLONISTS && ColonistBridge.isColonist(e));
    }

    /** Ближайший собеседник вдоль взгляда (не дальше стены), с небольшим допуском. Только в серверном потоке. */
    public static Entity looked(ServerPlayer player) {
        double range = LivingVoice.RANGE.get();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = eye.add(look.scale(range));
        HitResult wall = player.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double limit = wall.getType() == HitResult.Type.MISS ? range : wall.getLocation().distanceTo(eye);
        AABB box = new AABB(eye, end).inflate(SLACK + 1.0);
        Entity best = null;
        double bestAlong = Double.MAX_VALUE;
        for (Entity e : player.level().getEntities(player, box, Targets::isTalker)) {
            AABB hb = e.getBoundingBox().inflate(SLACK);
            Vec3 hit = hb.clip(eye, end).orElse(hb.contains(eye) ? eye : null);
            if (hit == null) continue;
            double along = hit.distanceTo(eye);
            if (along > limit + SLACK || along >= bestAlong) continue;
            best = e;
            bestAlong = along;
        }
        return best;
    }

    /** Собеседники в радиусе обращения, от ближнего к дальнему. */
    public static List<Entity> nearby(ServerPlayer player) {
        double r = LivingVoice.CALL_RANGE.get();
        List<Entity> list = new ArrayList<>(player.level().getEntities(player, player.getBoundingBox().inflate(r),
                e -> isTalker(e) && e.distanceTo(player) <= r));
        list.sort(Comparator.comparingDouble(e -> e.distanceToSqr(player)));
        return list;
    }

    /** Кому адресована фраза; null — никому. Только в серверном потоке. */
    public static Entity resolve(ServerPlayer player, Entity looked, String text) {
        List<Entity> near = nearby(player);
        if (looked != null && !near.contains(looked)) near.add(0, looked);
        if (near.isEmpty() || !COLONISTS) return looked;  // сопоставление имён и профессий — из Living Colonists
        String call = looked == null ? text : firstWords(text, VOCATIVE_WORDS);
        List<String> names = new ArrayList<>();
        List<Collection<String>> jobs = new ArrayList<>();
        String locale = player.clientInformation().language();
        for (Entity e : near) {
            if (MCA && McaBridge.isVillager(e)) {
                names.add(McaBridge.name(player, e));
                jobs.add(McaBridge.jobNames(e));
            } else {
                names.add(ColonistBridge.name(e));
                jobs.add(ColonistBridge.jobNames(e, locale));
            }
        }
        Entity byName = pick(near, ColonistBridge.mentioned(call, names), looked);
        if (byName != null) return byName;
        Entity byJob = pick(near, ColonistBridge.byJob(call, jobs), looked);
        if (byJob != null) return byJob;
        Entity bySound = pick(near, Names.fuzzy(call, names), looked);  // «Джейлин» — Jalynn
        return bySound != null ? bySound : looked;
    }

    /** Названия профессии собеседника (для отладки). */
    static Collection<String> jobsOf(ServerPlayer player, Entity e) {
        if (MCA && McaBridge.isVillager(e)) return McaBridge.jobNames(e);
        return COLONISTS ? ColonistBridge.jobNames(e, player.clientInformation().language()) : java.util.List.of();
    }

    /** Из подходящих — тот, на кого смотрят, иначе ближайший. */
    private static Entity pick(List<Entity> near, List<Integer> found, Entity looked) {
        if (found.isEmpty()) return null;
        for (int i : found) if (near.get(i) == looked) return looked;
        return near.get(found.stream().min(Integer::compare).get());
    }

    private static String firstWords(String text, int n) {
        String[] w = text.trim().split("\\s+");
        return String.join(" ", java.util.Arrays.copyOf(w, Math.min(n, w.length)));
    }

    /** Передать распознанную фразу собеседнику. Только в серверном потоке. */
    public static void say(ServerPlayer player, Entity target, String text) {
        if (!target.isAlive() || target.level() != player.level()) return;
        player.displayClientMessage(Component.literal("«" + text + "» → ").append(target.getName()).withStyle(ChatFormatting.GRAY), true);
        if (MCA && McaBridge.isVillager(target)) {
            McaBridge.say(player, target, text);
        } else if (COLONISTS && ColonistBridge.isColonist(target)) {
            ColonistBridge.say(player, target, text);
        }
    }
}
