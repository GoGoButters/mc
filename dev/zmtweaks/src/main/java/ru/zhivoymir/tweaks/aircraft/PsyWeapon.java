package ru.zhivoymir.tweaks.aircraft;

import immersive_aircraft.entity.GyrodyneEntity;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import ru.zhivoymir.tweaks.ZmTweaks;
import ru.zhivoymir.tweaks.client.PsyClient;

/**
 * Психологическое оружие винтокрыла: «Полёт валькирий», слышно до 150 блоков. Клиент шлёт toggle,
 * сервер сам рассылает игрокам в радиусе start/pos/stop — не зависит от дальности отслеживания
 * сущностей (view-distance 8 = 128 блоков).
 */
public final class PsyWeapon {
    public static final double RANGE = 150.0;
    private static final int POS_INTERVAL = 5;
    private static final int TOGGLE_COOLDOWN = 10;

    private PsyWeapon() {}

    public record Toggle() implements CustomPacketPayload {
        public static final Type<Toggle> TYPE = new Type<>(id("psy_toggle"));
        static final StreamCodec<ByteBuf, Toggle> CODEC = StreamCodec.unit(new Toggle());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Начать музыку винтокрыла entityId (или поправить позицию, если уже играет). */
    public record Start(int entityId, double x, double y, double z) implements CustomPacketPayload {
        public static final Type<Start> TYPE = new Type<>(id("psy_start"));
        static final StreamCodec<ByteBuf, Start> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Start::entityId, ByteBufCodecs.DOUBLE, Start::x,
                ByteBufCodecs.DOUBLE, Start::y, ByteBufCodecs.DOUBLE, Start::z, Start::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Позиция винтокрыла для слушателя, у которого сущность может быть не загружена. */
    public record Pos(int entityId, double x, double y, double z) implements CustomPacketPayload {
        public static final Type<Pos> TYPE = new Type<>(id("psy_pos"));
        static final StreamCodec<ByteBuf, Pos> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Pos::entityId, ByteBufCodecs.DOUBLE, Pos::x,
                ByteBufCodecs.DOUBLE, Pos::y, ByteBufCodecs.DOUBLE, Pos::z, Pos::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Stop(int entityId) implements CustomPacketPayload {
        public static final Type<Stop> TYPE = new Type<>(id("psy_stop"));
        static final StreamCodec<ByteBuf, Stop> CODEC = ByteBufCodecs.VAR_INT.map(Stop::new, Stop::entityId);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ZmTweaks.MOD_ID, path);
    }

    /**
     * Мод обязателен на обеих сторонах — каналы не optional. На выделенном сервере S2C-типы
     * регистрируются с пустыми обработчиками, чтобы client.PsyClient там не мог загрузиться.
     */
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
        boolean client = FMLEnvironment.dist.isClient();
        e.registrar("1")
                .playToServer(Toggle.TYPE, Toggle.CODEC, (p, ctx) -> {
                    if (ctx.player() instanceof ServerPlayer player) toggle(player);
                })
                .playToClient(Start.TYPE, Start.CODEC, client ? (p, ctx) -> PsyClient.onStart(p.entityId(), p.x(), p.y(), p.z()) : (p, ctx) -> {})
                .playToClient(Pos.TYPE, Pos.CODEC, client ? (p, ctx) -> PsyClient.onStart(p.entityId(), p.x(), p.y(), p.z()) : (p, ctx) -> {})
                .playToClient(Stop.TYPE, Stop.CODEC, client ? (p, ctx) -> PsyClient.onStop(p.entityId()) : (p, ctx) -> {});
    }

    /** Нажатие клавиши: работает с любого места винтокрыла. */
    static void toggle(ServerPlayer player) {
        if (!(player.getVehicle() instanceof GyrodyneEntity gyrodyne) || !gyrodyne.isAlive()) return;
        PsyCarrier.State state = ((PsyCarrier) gyrodyne).zmtweaks$psy();
        if (gyrodyne.tickCount - state.lastToggle < TOGGLE_COOLDOWN) return;
        state.lastToggle = gyrodyne.tickCount;
        setOn(gyrodyne, state, !state.on);
    }

    private static void setOn(GyrodyneEntity gyrodyne, PsyCarrier.State state, boolean on) {
        state.on = on;
        Component message = Component.translatable(on ? "zmtweaks.psy.on" : "zmtweaks.psy.off");
        for (Entity passenger : gyrodyne.getPassengers()) {
            if (passenger instanceof ServerPlayer p) p.displayClientMessage(message, true);
        }
        if (on) {
            broadcast(gyrodyne, state);
        } else {
            stopAll(gyrodyne, state);
        }
    }

    /** Из тика винтокрыла на сервере. */
    public static void serverTick(GyrodyneEntity gyrodyne) {
        PsyCarrier.State state = ((PsyCarrier) gyrodyne).zmtweaks$psy();
        if (!state.on) return;
        if (!gyrodyne.isVehicle()) {
            setOn(gyrodyne, state, false);  // в винтокрыле никого нет
            return;
        }
        if (gyrodyne.tickCount % POS_INTERVAL == 0) broadcast(gyrodyne, state);
    }

    /** Вошедшим в радиус — start, остальным в радиусе — pos, вышедшим (или в другом измерении) — stop. */
    private static void broadcast(GyrodyneEntity gyrodyne, PsyCarrier.State state) {
        if (!(gyrodyne.level() instanceof ServerLevel level)) return;
        double x = gyrodyne.getX(), y = gyrodyne.getY() + 1.0, z = gyrodyne.getZ();
        Set<UUID> inRange = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(x, y, z) > RANGE * RANGE) continue;
            inRange.add(player.getUUID());
            PacketDistributor.sendToPlayer(player, state.listeners.add(player.getUUID())
                    ? new Start(gyrodyne.getId(), x, y, z) : new Pos(gyrodyne.getId(), x, y, z));
        }
        for (Iterator<UUID> it = state.listeners.iterator(); it.hasNext(); ) {
            UUID id = it.next();
            if (inRange.contains(id)) continue;
            it.remove();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
            if (player != null) PacketDistributor.sendToPlayer(player, new Stop(gyrodyne.getId()));
        }
    }

    private static void stopAll(GyrodyneEntity gyrodyne, PsyCarrier.State state) {
        if (gyrodyne.level().getServer() == null) return;
        for (UUID id : state.listeners) {
            ServerPlayer player = gyrodyne.level().getServer().getPlayerList().getPlayer(id);
            if (player != null) PacketDistributor.sendToPlayer(player, new Stop(gyrodyne.getId()));
        }
        state.listeners.clear();
    }

    /**
     * Уничтожение, выгрузка, смена измерения, выход игрока вместе с машиной — музыка у всех замолкает.
     * Флаг остаётся в NBT: машина, вернувшаяся с пилотом (вход игрока, портал), заиграет снова.
     */
    public static void onEntityLeave(EntityLeaveLevelEvent e) {
        if (e.getLevel().isClientSide() || !(e.getEntity() instanceof GyrodyneEntity gyrodyne)) return;
        stopAll(gyrodyne, ((PsyCarrier) gyrodyne).zmtweaks$psy());
    }
}
