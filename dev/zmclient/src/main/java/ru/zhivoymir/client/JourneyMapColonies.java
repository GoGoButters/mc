package ru.zhivoymir.client;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Единственный класс, который знает про JourneyMap: плагин API v2 (JourneyMap находит его по аннотации)
 * и метки колоний. Метки не пишутся на диск; если игрок удалит или переименует метку, через секунду она вернётся.
 * Грузится, только если JourneyMap установлен.
 */
@JourneyMapPlugin(apiVersion = "2.0.0")
public final class JourneyMapColonies implements IClientPlugin {
    private static final String MOD_ID = ZhivoyMirClient.MOD_ID;
    private static final String GROUP = "Колонии";
    /** Золотой, как §6. */
    private static final int GOLD = 0xFFAA00;

    private static IClientAPI api;
    /** Что JourneyMap показал сразу после нашей последней расстановки (он может хранить имя иначе, чем мы его дали). */
    private static Set<String> applied = Set.of();

    @Override
    public void initialize(IClientAPI jm) {
        api = jm;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    /** Ставит метки; false — JourneyMap ещё не готов (нет мира или метки не приняты), повторить позже. */
    static boolean apply(List<ColonyWaypoints.Entry> entries, boolean dirty) {
        if (api == null || Minecraft.getInstance().level == null) return false;
        Set<String> want = new HashSet<>();
        for (ColonyWaypoints.Entry e : entries) want.add(key(label(e), e.pos()));
        Set<String> have = current();
        // совпало с заказанным или с тем, что JourneyMap принял в прошлый раз, — не трогаем, чтобы метки не мигали
        if (!dirty && (want.equals(have) || (!have.isEmpty() && have.equals(applied)))) return true;

        api.removeAllWaypoints(MOD_ID);
        WaypointGroup group = null;
        if (!entries.isEmpty()) {
            group = api.getWaypointGroupByName(MOD_ID, GROUP);
            if (group == null) {
                group = WaypointFactory.createWaypointGroup(MOD_ID, GROUP);
                group.setPersistent(false);
                api.addWaypointGroup(group);
            }
        }
        for (ColonyWaypoints.Entry e : entries) {
            Waypoint wp = WaypointFactory.createClientWaypoint(MOD_ID, e.pos(), label(e),
                    ResourceKey.create(Registries.DIMENSION, e.dim()), false);
            wp.setColor(GOLD);
            api.addWaypoint(MOD_ID, wp);
            group.addWaypoint(wp);
        }
        applied = current();
        return want.isEmpty() || !applied.isEmpty();
    }

    /** Что сейчас стоит в JourneyMap от нашего мода. */
    private static Set<String> current() {
        Set<String> have = new HashSet<>();
        for (Waypoint wp : api.getWaypoints(MOD_ID)) have.add(key(wp.getName(), wp.getBlockPos()));
        return have;
    }

    private static String label(ColonyWaypoints.Entry e) {
        return e.name() + " (" + e.owner() + ")";
    }

    /** Сверяем по имени и месту: формат измерения в JourneyMap не трогаем. */
    private static String key(String name, BlockPos p) {
        return name + "@" + p.getX() + "," + p.getY() + "," + p.getZ();
    }
}
