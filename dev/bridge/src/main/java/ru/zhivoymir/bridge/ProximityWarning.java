package ru.zhivoymir.bridge;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import net.conczin.mca.server.world.data.Village;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;
import java.util.Set;

/** Колонию можно ставить где угодно, но у самой деревни MCA игрок получает предупреждение. */
final class ProximityWarning {
    private ProximityWarning() {}

    /** Ратуша, стартовый лагерь, стартовый корабль. */
    private static final Set<ResourceLocation> FOUNDING_ITEMS = Set.of(
            ResourceLocation.fromNamespaceAndPath("minecolonies", "blockhuttownhall"),
            ResourceLocation.fromNamespaceAndPath("minecolonies", "supplycampdeployer"),
            ResourceLocation.fromNamespaceAndPath("minecolonies", "supplychestdeployer"));

    static void onPlayerTick(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;
        if (!isFounding(player.getMainHandItem()) && !isFounding(player.getOffhandItem())) return;
        warning(player.serverLevel(), player.blockPosition()).ifPresent(msg -> player.displayClientMessage(msg, true));
    }

    static void onColonyCreated(ColonyCreatedModEvent e) {
        IColony colony = e.getColony();
        if (!(colony.getWorld() instanceof ServerLevel level)) return;
        ServerPlayer owner = level.getServer().getPlayerList().getPlayer(colony.getPermissions().getOwner());
        if (owner == null) return;
        warning(level, colony.getCenter()).ifPresent(msg -> owner.sendSystemMessage(msg.copy()
                .append(Component.literal(" Колонию можно оставить здесь, но жители деревни могут занимать дома и кровати колонистов.")
                        .withStyle(ChatFormatting.GRAY))));
    }

    private static boolean isFounding(ItemStack stack) {
        return !stack.isEmpty() && FOUNDING_ITEMS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    /** Текст предупреждения, если точка ближе {@code warnDistance} к границе деревни. */
    static Optional<Component> warning(ServerLevel level, BlockPos pos) {
        int limit = Bridge.WARN_DISTANCE.get();
        if (limit <= 0) return Optional.empty();
        Optional<Village> village = Villages.nearest(level, pos, limit);
        if (village.isEmpty()) return Optional.empty();
        Village v = village.get();
        int d = (int) Math.round(Villages.borderDistance(v, pos));
        Component text = d == 0
                ? Component.literal("⚠ Вы внутри деревни «" + v.getName() + "»: колония здесь будет делить дома с жителями.")
                .withStyle(ChatFormatting.RED)
                : Component.literal("⚠ До деревни «" + v.getName() + "» " + d + " бл. — ближе " + limit
                + " бл. жители могут занимать дома колонистов.").withStyle(ChatFormatting.GOLD);
        return Optional.of(text);
    }
}
