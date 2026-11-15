package ru.zhivoymir.livingvoice;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Клавиша «Говорить с жителями» (zmclient): клиент сообщает, зажата ли она. Игроки между собой говорят
 * в голосовом чате как угодно, а в распознавание уходит только речь при зажатой клавише.
 */
final class TalkKey {
    /** Сколько последних кадров (по 20 мс) помнить до нажатия: начало фразы обгоняет пакет о клавише. */
    private static final int PREROLL_FRAMES = 25;
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private TalkKey() {}

    /** Протокол общий с zmclient — поля не менять. */
    record Payload(boolean held) implements CustomPacketPayload {
        static final Type<Payload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("zhivoymir", "npc_talk"));
        static final StreamCodec<ByteBuf, Payload> CODEC = ByteBufCodecs.BOOL.map(Payload::new, Payload::held);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static final class State {
        boolean held;
        long released;
        final ArrayDeque<Frame> preroll = new ArrayDeque<>();
    }

    private record Frame(long time, byte[] opus) {}

    static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
        e.registrar("1").optional().playToServer(Payload.TYPE, Payload.CODEC, TalkKey::onPayload);
    }

    private static void onPayload(Payload p, IPayloadContext ctx) {
        State s = STATES.computeIfAbsent(ctx.player().getUUID(), id -> new State());
        synchronized (s) {
            if (s.held && !p.held()) s.released = System.currentTimeMillis();
            s.held = p.held();
        }
    }

    static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
        STATES.remove(e.getEntity().getUUID());
    }

    /**
     * Какие кадры отдать распознаванию: пустой список — никакие (кадр запомнен на случай,
     * если игрок сейчас нажмёт клавишу), иначе — недавние запомненные и этот.
     */
    static List<byte[]> accept(UUID player, byte[] opus, long now) {
        if (!LivingVoice.REQUIRE_TALK_KEY.get()) return List.of(opus);
        // клиент без zmclient клавишу не пришлёт — жителям ничего; кадры копим и до первого нажатия
        State s = STATES.computeIfAbsent(player, id -> new State());
        int grace = LivingVoice.TALK_KEY_GRACE_MS.get();
        synchronized (s) {
            if (!s.held && now - s.released > grace) {
                if (s.preroll.size() >= PREROLL_FRAMES) s.preroll.pollFirst();
                s.preroll.addLast(new Frame(now, opus));
                return List.of();
            }
            List<byte[]> out = new ArrayList<>(s.preroll.size() + 1);
            for (Frame f : s.preroll) if (now - f.time() <= grace) out.add(f.opus());
            s.preroll.clear();
            out.add(opus);
            return out;
        }
    }
}
