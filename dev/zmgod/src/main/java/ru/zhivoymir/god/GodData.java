package ru.zhivoymir.god;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Память бога в сохранении мира (overworld): летопись, вера, заметки об игроках, кулдауны. Время — игровые тики overworld. */
final class GodData extends SavedData {
    private static final String NAME = "zmgod";
    static final int MAX_EVENTS = 60;
    static final int MAX_HISTORY = 12;
    static final int MIN_FAITH = -100, MAX_FAITH = 100;

    record Entry(long time, String text) {}

    /** Что произошло в мире (новые в конце). */
    final Deque<Entry> events = new ArrayDeque<>();
    /** Что делал сам бог (новые в конце). */
    final Deque<Entry> history = new ArrayDeque<>();
    final Map<UUID, Integer> faith = new HashMap<>();
    final Map<UUID, String> notes = new HashMap<>();
    final Map<UUID, String> names = new HashMap<>();
    /** Ключ (действие|игрок, «sin|игрок», «curse|игрок»…) → момент, до которого действует или когда случилось. */
    final Map<String, Long> marks = new HashMap<>();
    /** Когда бог сам посмотрит на мир в следующий раз; 0 — не назначено. */
    long nextVisit;

    static GodData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(GodData::new, GodData::load, null), NAME);
    }

    static long now(MinecraftServer server) {
        return server.overworld().getGameTime();
    }

    void event(MinecraftServer server, String text) {
        push(events, new Entry(now(server), text), MAX_EVENTS);
        God.LOG.info("[zmgod] event: {}", text);
    }

    void history(MinecraftServer server, String text) {
        push(history, new Entry(now(server), text), MAX_HISTORY);
    }

    private void push(Deque<Entry> q, Entry e, int max) {
        q.addLast(e);
        while (q.size() > max) q.removeFirst();
        setDirty();
    }

    int faith(UUID player) {
        return faith.getOrDefault(player, 0);
    }

    void addFaith(UUID player, int delta) {
        faith.put(player, Math.max(MIN_FAITH, Math.min(MAX_FAITH, faith(player) + delta)));
        setDirty();
    }

    void mark(String key, long time) {
        marks.put(key, time);
        setDirty();
    }

    long mark(String key) {
        return marks.getOrDefault(key, Long.MIN_VALUE / 2);
    }

    private static GodData load(CompoundTag tag, HolderLookup.Provider registries) {
        GodData d = new GodData();
        for (Tag t : tag.getList("events", Tag.TAG_COMPOUND)) d.events.add(entry((CompoundTag) t));
        for (Tag t : tag.getList("history", Tag.TAG_COMPOUND)) d.history.add(entry((CompoundTag) t));
        CompoundTag players = tag.getCompound("players");
        for (String k : players.getAllKeys()) {
            UUID id = UUID.fromString(k);
            CompoundTag p = players.getCompound(k);
            if (p.contains("faith")) d.faith.put(id, p.getInt("faith"));
            if (p.contains("note")) d.notes.put(id, p.getString("note"));
            if (p.contains("name")) d.names.put(id, p.getString("name"));
        }
        CompoundTag marks = tag.getCompound("marks");
        for (String k : marks.getAllKeys()) d.marks.put(k, marks.getLong(k));
        d.nextVisit = tag.getLong("nextVisit");
        return d;
    }

    private static Entry entry(CompoundTag t) {
        return new Entry(t.getLong("t"), t.getString("text"));
    }

    private static ListTag list(Deque<Entry> q) {
        ListTag l = new ListTag();
        for (Entry e : q) {
            CompoundTag t = new CompoundTag();
            t.putLong("t", e.time());
            t.putString("text", e.text());
            l.add(t);
        }
        return l;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("events", list(events));
        tag.put("history", list(history));
        CompoundTag players = new CompoundTag();
        Map<UUID, CompoundTag> byId = new HashMap<>();
        faith.forEach((id, v) -> byId.computeIfAbsent(id, k -> new CompoundTag()).putInt("faith", v));
        notes.forEach((id, v) -> byId.computeIfAbsent(id, k -> new CompoundTag()).putString("note", v));
        names.forEach((id, v) -> byId.computeIfAbsent(id, k -> new CompoundTag()).putString("name", v));
        byId.forEach((id, t) -> players.put(id.toString(), t));
        tag.put("players", players);
        CompoundTag marks = new CompoundTag();
        this.marks.forEach(marks::putLong);
        tag.put("marks", marks);
        tag.putLong("nextVisit", nextVisit);
        return tag;
    }
}
