package ru.zhivoymir.client;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Метки колоний на карте: сервер (zmbridge) присылает полный список колоний, здесь он хранится
 * и раз в секунду сверяется с JourneyMap. Класс не ссылается на JourneyMap — без него клиент не падает.
 */
final class ColonyWaypoints {
    private static final Logger LOG = LogUtils.getLogger();

    private ColonyWaypoints() {}

    /** Одна колония. Протокол общий с zmbridge — порядок полей не менять. */
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

    /** Последний присланный список. */
    private static List<Entry> entries = List.of();
    /** Список сменился и ещё не передан в JourneyMap. */
    private static boolean dirty;
    /** JourneyMap бросил ошибку — больше не трогаем до перезапуска. */
    private static boolean broken;
    private static int ticks;

    static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
        e.registrar("1").optional().playToClient(Payload.TYPE, Payload.CODEC, ColonyWaypoints::onPayload);
    }

    private static void onPayload(Payload p, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            entries = List.copyOf(p.entries());
            dirty = true;
        });
    }

    static void onClientTick(ClientTickEvent.Post e) {
        if (broken || ++ticks % 20 != 0) return;
        if (!ModList.get().isLoaded("journeymap")) return;
        try {
            if (JourneyMapColonies.apply(entries, dirty)) dirty = false;
        } catch (Throwable t) {
            broken = true;
            LOG.error("Метки колоний на карте JourneyMap отключены до перезапуска", t);
        }
    }

    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut e) {
        // метки не пишутся на диск; в следующем мире первая же расстановка снимет оставшиеся
        entries = List.of();
        dirty = true;
    }
}
