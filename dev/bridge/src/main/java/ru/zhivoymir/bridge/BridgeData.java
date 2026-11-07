package ru.zhivoymir.bridge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Состояние моста в сохранении мира (хранится в overworld). */
final class BridgeData extends SavedData {
    private static final String NAME = "zmbridge";

    /** Ключ деревни: измерение + id деревни MCA. */
    static String villageKey(net.minecraft.server.level.ServerLevel level, int villageId) {
        return level.dimension().location() + "#" + villageId;
    }

    /** Когда (игровое время) из деревни последний раз уехал житель. */
    final Map<String, Long> lastRelocation = new HashMap<>();
    /** Отложенные изменения репутации: деревня → игрок → сердца. Применяются, когда игрок рядом с деревней. */
    final Map<String, Map<UUID, Integer>> pending = new HashMap<>();
    /** Сколько репутации за защиту получено сегодня: деревня|игрок → {день, сумма}. */
    final Map<String, long[]> dailyKills = new HashMap<>();
    /** Переселенцы: кто откуда пришёл (для родни и ИИ во второй очереди). */
    final List<CompoundTag> settlers = new ArrayList<>();

    static BridgeData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(BridgeData::new, BridgeData::load, null), NAME);
    }

    void addPending(String village, UUID player, int delta) {
        pending.computeIfAbsent(village, k -> new HashMap<>()).merge(player, delta, Integer::sum);
        setDirty();
    }

    private static BridgeData load(CompoundTag tag, HolderLookup.Provider registries) {
        BridgeData d = new BridgeData();
        CompoundTag last = tag.getCompound("lastRelocation");
        for (String k : last.getAllKeys()) d.lastRelocation.put(k, last.getLong(k));
        CompoundTag pend = tag.getCompound("pending");
        for (String v : pend.getAllKeys()) {
            CompoundTag players = pend.getCompound(v);
            Map<UUID, Integer> m = new HashMap<>();
            for (String p : players.getAllKeys()) m.put(UUID.fromString(p), players.getInt(p));
            d.pending.put(v, m);
        }
        CompoundTag daily = tag.getCompound("dailyKills");
        for (String k : daily.getAllKeys()) d.dailyKills.put(k, daily.getCompound(k).contains("day")
                ? new long[]{daily.getCompound(k).getLong("day"), daily.getCompound(k).getLong("sum")} : new long[2]);
        for (Tag t : tag.getList("settlers", Tag.TAG_COMPOUND)) d.settlers.add((CompoundTag) t);
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag last = new CompoundTag();
        lastRelocation.forEach(last::putLong);
        tag.put("lastRelocation", last);
        CompoundTag pend = new CompoundTag();
        pending.forEach((v, m) -> {
            if (m.isEmpty()) return;
            CompoundTag players = new CompoundTag();
            m.forEach((p, d) -> players.putInt(p.toString(), d));
            pend.put(v, players);
        });
        tag.put("pending", pend);
        CompoundTag daily = new CompoundTag();
        dailyKills.forEach((k, a) -> {
            CompoundTag e = new CompoundTag();
            e.putLong("day", a[0]);
            e.putLong("sum", a[1]);
            daily.put(k, e);
        });
        tag.put("dailyKills", daily);
        ListTag list = new ListTag();
        list.addAll(settlers);
        tag.put("settlers", list);
        return tag;
    }
}
