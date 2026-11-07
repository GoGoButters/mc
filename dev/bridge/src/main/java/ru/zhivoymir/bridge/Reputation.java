package ru.zhivoymir.bridge;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import net.conczin.mca.server.world.data.Village;
import net.conczin.mca.server.world.data.VillageManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Общая репутация колонии и деревни. Колонисты, убившие чудовище у деревни, поднимают репутацию владельца колонии;
 * замеченные кражи воров колонии (Colonist Thieves) её роняют. Сердца MCA хранятся у загруженных жителей, поэтому
 * изменения, которые нельзя применить сразу, копятся и применяются, когда владелец приходит в деревню.
 */
public final class Reputation {
    private Reputation() {}

    /** На таком расстоянии от границы деревни отложенная репутация применяется. */
    private static final int APPLY_DISTANCE = 32;

    static void onDeath(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof Enemy) || !(e.getEntity().level() instanceof ServerLevel level)) return;
        if (!(e.getSource().getEntity() instanceof AbstractEntityCitizen killer)) return;
        ICitizenData citizen = killer.getCitizenData();
        if (citizen == null || citizen.getColony() == null) return;
        int reward = Bridge.GUARD_KILL_REPUTATION.get();
        if (reward <= 0) return;
        Optional<Village> village = Villages.nearest(level, e.getEntity().blockPosition(), Bridge.GUARD_KILL_MARGIN.get());
        if (village.isEmpty()) return;
        IColony colony = citizen.getColony();
        UUID owner = colony.getPermissions().getOwner();
        if (owner == null) return;

        BridgeData data = BridgeData.get(level.getServer());
        String key = BridgeData.villageKey(level, village.get().getId());
        long today = level.getGameTime() / 24000;
        data.dailyKills.values().removeIf(a -> a[0] != today);
        long[] counter = data.dailyKills.computeIfAbsent(key + "|" + owner, k -> new long[]{today, 0});
        int amount = (int) Math.min(reward, Bridge.GUARD_KILL_DAILY_CAP.get() - counter[1]);
        if (amount <= 0) return;
        boolean first = counter[1] == 0;
        counter[1] += amount;
        data.setDirty();

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);
        if (player != null && player.level() == level && Villages.addReputation(level, village.get(), player, amount)) {
            if (first) player.sendSystemMessage(Component.literal("Жители «" + village.get().getName()
                    + "» видели, как колония «" + colony.getName() + "» защищает их от чудовищ.").withStyle(ChatFormatting.GREEN));
        } else {
            data.addPending(key, owner, amount);
        }
    }

    /** Вызывается из миксина Colonist Thieves: вора колонии заметили (или поймали) в деревне. */
    public static void onTheft(ServerLevel level, BlockPos target, IColony thieves, boolean caught) {
        int penalty = Bridge.THEFT_PENALTY.get() * (caught ? 2 : 1);
        UUID owner = thieves.getPermissions().getOwner();
        if (penalty <= 0 || owner == null) return;
        Optional<Village> village = Villages.nearest(level, target, 64);
        if (village.isEmpty()) return;
        BridgeData.get(level.getServer()).addPending(BridgeData.villageKey(level, village.get().getId()), owner, -penalty);
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);
        if (player != null) player.sendSystemMessage(Component.literal("В деревне «" + village.get().getName() + "» "
                + (caught ? "поймали" : "заметили") + " вора из «" + thieves.getName() + "». Жители это запомнят.")
                .withStyle(ChatFormatting.RED));
        Bridge.LOG.info("[zmbridge] theft from {} spotted in village {} (caught={}), owner {} -{}",
                thieves.getName(), village.get().getName(), caught, owner, penalty);
    }

    /** Раз в 5 секунд: если игрок у деревни, где за ним числится отложенная репутация, — применяем её. */
    static void onPlayerTick(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer player) || player.tickCount % 100 != 0) return;
        ServerLevel level = player.serverLevel();
        BridgeData data = BridgeData.get(level.getServer());
        if (data.pending.isEmpty()) return;
        String prefix = level.dimension().location() + "#";
        for (Iterator<Map.Entry<String, Map<UUID, Integer>>> it = data.pending.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Map<UUID, Integer>> entry = it.next();
            Integer delta = entry.getValue().get(player.getUUID());
            if (delta == null || !entry.getKey().startsWith(prefix)) continue;
            int id = Integer.parseInt(entry.getKey().substring(prefix.length()));
            Optional<Village> village = VillageManager.get(level).getOrEmpty(id);
            if (village.isEmpty()) {
                entry.getValue().remove(player.getUUID());
            } else if (Villages.borderDistance(village.get(), player.blockPosition()) <= APPLY_DISTANCE
                    && Villages.addReputation(level, village.get(), player, delta)) {
                entry.getValue().remove(player.getUUID());
                player.sendSystemMessage(Component.literal(delta > 0
                        ? "Жители «" + village.get().getName() + "» благодарны вашей колонии за защиту."
                        : "Жители «" + village.get().getName() + "» косо смотрят: до них дошли вести о кражах вашей колонии.")
                        .withStyle(delta > 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
            } else {
                continue;
            }
            if (entry.getValue().isEmpty()) it.remove();
            data.setDirty();
        }
    }
}
