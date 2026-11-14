package ru.zhivoymir.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;

import java.util.List;

/**
 * Единственный класс, который знает про Xaero's Minimap: ставит метки колоний как сторонние (third-party) —
 * они не пишутся на диск и видны и на миникарте, и на World Map. Грузится, только если Xaero установлен.
 */
final class XaeroColonies {
    private static final ResourceLocation ORIGIN = ResourceLocation.fromNamespaceAndPath("zmclient", "colonies");

    /** Корень мира, в который метки ставили последний раз: сменился — значит новая сессия, ставим заново. */
    private static MinimapWorldRootContainer lastRoot;

    private XaeroColonies() {}

    /** Ставит метки; false — сессия Xaero ещё не готова, повторить позже. */
    static boolean apply(List<ColonyWaypoints.Entry> entries, boolean dirty) {
        MinimapSession s = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (s == null) return false;
        MinimapWorldRootContainer root = s.getWorldManager().getAutoRootContainer();
        if (root == null) return false;
        if (!dirty && root == lastRoot) return true;
        for (MinimapWorldContainer c : root.getSubContainers()) c.getThirdPartyWaypointManager().clearOrigin(ORIGIN);
        for (ColonyWaypoints.Entry e : entries) {
            String dir = s.getDimensionHelper().getDimensionDirectoryName(ResourceKey.create(Registries.DIMENSION, e.dim()));
            MinimapWorldContainer c = root.addSubContainer(root.getPath().resolve(dir));
            BlockPos p = e.pos();
            c.getThirdPartyWaypointManager().get(ORIGIN).add("colony-" + e.id(), new Waypoint(p.getX(), p.getY(), p.getZ(),
                    e.name() + " (" + e.owner() + ")", "К", WaypointColor.GOLD, WaypointPurpose.NORMAL));
        }
        lastRoot = root;
        return true;
    }
}
