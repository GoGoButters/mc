package ru.zhivoymir.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Автопосадка: игрок срубил дерево под корень (FallingTree валит его целиком) — через пару секунд на месте пня
 * сажается саженец из выпавших с листвы, а если их нет — из инвентаря игрока. Постройки из брёвен не трогает:
 * нужна естественная (не поставленная игроком) листва на макушке ствола.
 */
final class Replant {
    private static final int DELAY_TICKS = 50;
    private static final int MAX_TRUNK = 40;
    private static final double PICKUP_RADIUS = 12;

    private record Pending(ServerLevel level, BlockPos pos, ServerPlayer player, Item wanted, int at) {}

    private static final List<Pending> PENDING = new ArrayList<>();

    private Replant() {}

    static void onBreak(BlockEvent.BreakEvent e) {
        if (!Bridge.REPLANT.get()) return;  // FallingTree может отменить событие и рубить сам — итог проверяем позже
        if (!(e.getLevel() instanceof ServerLevel level) || !(e.getPlayer() instanceof ServerPlayer player)) return;
        if (player.isShiftKeyDown() || player.isCreative()) return;  // присев FallingTree рубит одно бревно
        BlockState state = e.getState();
        BlockPos pos = e.getPos();
        if (!state.is(BlockTags.LOGS) || !level.getBlockState(pos.below()).is(BlockTags.DIRT)) return;
        if (!naturalCrown(level, pos)) return;
        PENDING.add(new Pending(level, pos.immutable(), player, sapling(state.getBlock()), level.getServer().getTickCount() + DELAY_TICKS));
    }

    static void onTick(ServerTickEvent.Post e) {
        if (PENDING.isEmpty()) return;
        int now = e.getServer().getTickCount();
        PENDING.removeIf(p -> {
            if (p.at() > now) return false;
            plant(p);
            return true;
        });
    }

    /** Вверх по стволу до макушки; рядом с ней должна быть листва, выросшая сама. */
    private static boolean naturalCrown(ServerLevel level, BlockPos base) {
        BlockPos top = base;
        for (int i = 0; i < MAX_TRUNK && level.getBlockState(top.above()).is(BlockTags.LOGS); i++) top = top.above();
        if (top.equals(base)) return false;  // одно бревно — не дерево
        for (BlockPos p : BlockPos.betweenClosed(top.offset(-3, -2, -3), top.offset(3, 3, 3))) {
            BlockState s = level.getBlockState(p);
            if (s.is(BlockTags.LEAVES) && !(s.hasProperty(LeavesBlock.PERSISTENT) && s.getValue(LeavesBlock.PERSISTENT))) return true;
        }
        return false;
    }

    /** Саженец той же породы: oak_log → oak_sapling, mangrove → propagule; null — не угадали. */
    private static Item sapling(Block log) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(log);
        String wood = id.getPath().replaceAll("^stripped_", "").replaceAll("_(log|wood|stem)$", "");
        for (String path : List.of(wood + "_sapling", wood + "_propagule")) {
            Item it = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path));
            if (it != net.minecraft.world.item.Items.AIR) return it;
        }
        return null;
    }

    private static void plant(Pending p) {
        ServerLevel level = p.level();
        BlockPos pos = p.pos();
        // Дерево должно исчезнуть целиком: ствол, повисший над пнём, — не повод сажать.
        if (!level.isLoaded(pos) || !level.getBlockState(pos).isAir() || level.getBlockState(pos.above()).is(BlockTags.LOGS)) return;
        AABB box = new AABB(pos).inflate(PICKUP_RADIUS);
        List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class, box,
                ie -> ie.isAlive() && ie.getItem().is(ItemTags.SAPLINGS) && ie.getItem().getItem() instanceof BlockItem);
        drops.sort(Comparator.comparingInt((ItemEntity ie) -> ie.getItem().is(p.wanted() == null ? null : p.wanted()) ? 0 : 1)
                .thenComparingDouble(ie -> ie.distanceToSqr(pos.getCenter())));
        for (ItemEntity ie : drops) {
            if (place(level, pos, ie.getItem())) {
                ie.getItem().shrink(1);
                if (ie.getItem().isEmpty()) ie.discard();
                return;
            }
        }
        // Листва ещё не осыпалась или саженцы не выпали — из инвентаря, только той же породы.
        if (p.wanted() == null || p.player().isRemoved()) return;
        var inv = p.player().getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(p.wanted()) && place(level, pos, s)) {
                s.shrink(1);
                return;
            }
        }
    }

    private static boolean place(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem bi)) return false;
        BlockState s = bi.getBlock().defaultBlockState();
        if (!s.canSurvive(level, pos)) return false;
        level.setBlockAndUpdate(pos, s);
        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0f, 0.9f);
        return true;
    }
}
