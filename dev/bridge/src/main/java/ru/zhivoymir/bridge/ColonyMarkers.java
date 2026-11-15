package ru.zhivoymir.bridge;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Метки колоний на карте: сервер рассылает клиентам (zmclient) полный список колоний, клиент ставит метки JourneyMap.
 * При основании колонии — объявление в чат всем.
 */
final class ColonyMarkers {
    private ColonyMarkers() {}

    /** Одна колония. Протокол общий с zmclient — порядок полей не менять. */
    record Entry(int id, ResourceLocation dim, BlockPos pos, String name, String owner) {}

    record Payload(List<Entry> entries) implements CustomPacketPayload {
        static final Type<Payload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("zhivoymir", "colonies"));
        static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.of(Payload::write, Payload::read);

        private static void write(FriendlyByteBuf buf, Payload p) {
            buf.writeVarInt(p.entries.size());
            for (Entry e : p.entries) {
                buf.writeVarInt(e.id());
                buf.writeResourceLocation(e.dim());
                buf.writeBlockPos(e.pos());
                buf.writeUtf(e.name(), 128);
                buf.writeUtf(e.owner(), 64);
            }
        }

        private static Payload read(FriendlyByteBuf buf) {
            int n = buf.readVarInt();
            List<Entry> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                list.add(new Entry(buf.readVarInt(), buf.readResourceLocation(), buf.readBlockPos(),
                        buf.readUtf(128), buf.readUtf(64)));
            }
            return new Payload(list);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Последний разосланный список; null — ещё не рассылали. */
    private static List<Entry> last;
    private static volatile boolean dirty;
    /** Новые колонии для объявления на следующем тике. */
    private static final Queue<IColony> founded = new ConcurrentLinkedQueue<>();

    static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
        // сервер этот пакет не принимает — обработчик пустой
        e.registrar("1").optional().playToClient(Payload.TYPE, Payload.CODEC, (p, ctx) -> {});
    }

    static void markDirty() {
        dirty = true;
    }

    static void onColonyCreated(ColonyCreatedModEvent e) {
        dirty = true;
        if (Bridge.COLONY_ANNOUNCE.get()) founded.add(e.getColony());
    }

    static void onServerTick(ServerTickEvent.Post e) {
        MinecraftServer server = e.getServer();
        IColony colony;
        while ((colony = founded.poll()) != null) announce(server, colony);
        if (!dirty && server.getTickCount() % 100 != 0) return;
        dirty = false;
        List<Entry> now = snapshot();
        if (now.equals(last)) return;
        last = now;
        Payload p = new Payload(now);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) send(player, p);
    }

    static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!Bridge.COLONY_MARKERS.get()) return;
        if (e.getEntity() instanceof ServerPlayer player) send(player, new Payload(snapshot()));
    }

    static void onServerStopped(ServerStoppedEvent e) {
        last = null;
        dirty = false;
        founded.clear();
    }

    private static void send(ServerPlayer player, Payload p) {
        if (player.connection.hasChannel(Payload.TYPE)) PacketDistributor.sendToPlayer(player, p);
    }

    /** Все колонии по возрастанию id; при выключенных метках — пустой список (клиент уберёт метки). */
    private static List<Entry> snapshot() {
        if (!Bridge.COLONY_MARKERS.get()) return List.of();
        List<Entry> list = new ArrayList<>();
        for (IColony c : IColonyManager.getInstance().getAllColonies()) {
            list.add(new Entry(c.getID(), c.getDimension().location(), c.getCenter(),
                    cut(c.getName(), 128), cut(c.getPermissions().getOwnerName(), 64)));
        }
        list.sort(Comparator.comparingInt(Entry::id));
        return list;
    }

    private static void announce(MinecraftServer server, IColony c) {
        BlockPos pos = c.getCenter();
        Component msg = Component.literal("⚑ " + nz(c.getPermissions().getOwnerName()) + " основал(а) колонию «"
                + nz(c.getName()) + "» (" + pos.getX() + ", " + pos.getZ() + ")").withStyle(ChatFormatting.GOLD);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) player.sendSystemMessage(msg);
    }

    private static String nz(String s) {
        return s == null ? "?" : s;
    }

    private static String cut(String s, int max) {
        s = nz(s);
        return s.length() > max ? s.substring(0, max) : s;
    }
}
