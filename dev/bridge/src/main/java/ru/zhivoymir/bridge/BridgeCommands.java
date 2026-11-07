package ru.zhivoymir.bridge;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.mojang.brigadier.CommandDispatcher;
import net.conczin.mca.resources.Tasks;
import net.conczin.mca.server.world.data.Village;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Optional;

/** /bridge info — что мост видит вокруг игрока (для отладки). */
final class BridgeCommands {
    private BridgeCommands() {}

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("bridge").requires(s -> s.hasPermission(2))
                .then(Commands.literal("info").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    ctx.getSource().sendSuccess(() -> Component.literal(info(player)), false);
                    return 1;
                })));
    }

    private static String info(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        StringBuilder sb = new StringBuilder();
        Optional<Village> near = Villages.nearest(level, player.blockPosition(), 512);
        if (near.isEmpty()) {
            sb.append("Деревень MCA в 512 бл. нет.");
        } else {
            Village v = near.get();
            String key = BridgeData.villageKey(level, v.getId());
            BridgeData data = BridgeData.get(level.getServer());
            sb.append("Деревня «").append(v.getName()).append("» (").append(key).append(")")
                    .append("\n  до границы: ").append(Math.round(Villages.borderDistance(v, player.blockPosition()))).append(" бл.")
                    .append("\n  жители: ").append(v.getPopulation()).append(", кроватей: ").append(v.getMaxPopulation())
                    .append("\n  ваша репутация: ").append(v.getReputation(player)).append(", ранг: ").append(Tasks.getRank(v, player))
                    .append("\n  отложено: ").append(data.pending.getOrDefault(key, Map.of()).getOrDefault(player.getUUID(), 0));
            Long last = data.lastRelocation.get(key);
            if (last != null) sb.append("\n  последний переезд: ").append((level.getGameTime() - last) / 24000.0).append(" дн. назад");
        }
        IColony colony = IMinecoloniesAPI.getInstance().getColonyManager().getClosestIColony(level, player.blockPosition());
        if (colony != null) {
            sb.append("\nКолония «").append(colony.getName()).append("»: ")
                    .append(colony.getCitizenManager().getCurrentCitizenCount()).append("/")
                    .append(colony.getCitizenManager().getPotentialMaxCitizens()).append(" колонистов, ваш ранг: ")
                    .append(colony.getPermissions().getRank(player).getName());
            ProximityWarning.warning(level, colony.getCenter()).ifPresent(w -> sb.append("\n  ").append(w.getString()));
        }
        sb.append("\nПереселенцев всего: ").append(BridgeData.get(level.getServer()).settlers.size());
        return sb.toString();
    }
}
