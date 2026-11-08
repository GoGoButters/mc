package ru.zhivoymir.god;

import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import com.minecolonies.api.eventbus.events.colony.buildings.BuildingConstructionModEvent;
import com.minecolonies.api.eventbus.events.colony.citizens.CitizenDiedModEvent;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/** Летопись: события мира, о которых бог узнаёт, и вера, которая от них меняется. */
final class Chronicle {
    /** Сколько веры теряет убийца жителя. */
    static final int MURDER_FAITH = -10;
    static final int BOSS_FAITH = 5;

    private Chronicle() {}

    static void onDeath(LivingDeathEvent e) {
        LivingEntity victim = e.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        MinecraftServer server = level.getServer();
        GodData data = GodData.get(server);
        if (victim instanceof ServerPlayer p) {
            data.event(server, "Погиб игрок " + name(p) + ": " + e.getSource().getLocalizedDeathMessage(p).getString());
            return;
        }
        if (!(e.getSource().getEntity() instanceof ServerPlayer killer)) return;
        ResourceLocation type = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());
        boolean citizen = type.getNamespace().equals("minecolonies");
        boolean villager = victim instanceof AbstractVillager || type.getNamespace().equals("mca") && type.getPath().contains("villager");
        if (citizen || villager) {
            data.event(server, name(killer) + " убил " + (citizen ? "колониста " : "жителя деревни ") + victim.getName().getString());
            data.addFaith(killer.getUUID(), MURDER_FAITH);
            data.mark("sin|" + killer.getUUID(), GodData.now(server));
        } else if (victim.getType().is(Tags.EntityTypes.BOSSES)) {
            data.event(server, name(killer) + " одолел " + victim.getName().getString());
            data.addFaith(killer.getUUID(), BOSS_FAITH);
        }
    }

    static void onAdvancement(AdvancementEvent.AdvancementEarnEvent e) {
        DisplayInfo display = e.getAdvancement().value().display().orElse(null);
        if (display == null || !display.shouldAnnounceChat() || !(e.getEntity() instanceof ServerPlayer p)) return;
        GodData.get(p.server).event(p.server, name(p) + " получил достижение «" + display.getTitle().getString() + "»");
    }

    static void onCitizenDied(CitizenDiedModEvent e) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        String cause = e.getDamageSource() == null ? "" : " (" + e.getDamageSource().getMsgId() + ")";
        GodData.get(server).event(server, "В колонии «" + e.getColony().getName() + "» погиб колонист " + e.getCitizen().getName() + cause);
    }

    static void onColonyCreated(ColonyCreatedModEvent e) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        GodData.get(server).event(server, "Основана колония «" + e.getColony().getName() + "», владелец "
                + e.getColony().getPermissions().getOwnerName());
    }

    static void onBuilt(BuildingConstructionModEvent e) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        var order = e.getWorkOrder();
        String what = order.getWorkOrderType() == WorkOrderType.REMOVE ? "снесено" : "построено";
        GodData.get(server).event(server, "В колонии «" + e.getColony().getName() + "» " + what + ": "
                + order.getDisplayName().getString() + " (ур. " + order.getTargetLevel() + ")");
    }

    static String name(ServerPlayer p) {
        String n = p.getGameProfile().getName();
        GodData data = GodData.get(p.server);
        if (!n.equals(data.names.get(p.getUUID()))) {
            data.names.put(p.getUUID(), n);
            data.setDirty();
        }
        return n;
    }
}
