package ru.zhivoymir.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Клавиша «Говорить с жителями»: в голосовом чате игроки говорят друг с другом с активацией голосом,
 * а жители (и бог) слышат только то, что сказано с зажатой клавишей. Состояние клавиши уходит на сервер
 * (livingvoice) пакетом при каждой смене; пока она зажата — на экране подсказка.
 */
final class NpcTalk {
    static final KeyMapping KEY = new KeyMapping("key.zmclient.talk", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_CAPS_LOCK, "key.categories.zmclient");

    /** Последнее отправленное серверу состояние. */
    private static boolean sent;

    private NpcTalk() {}

    /** Протокол общий с livingvoice — поля не менять. */
    record Payload(boolean held) implements CustomPacketPayload {
        static final Type<Payload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("zhivoymir", "npc_talk"));
        static final StreamCodec<ByteBuf, Payload> CODEC = ByteBufCodecs.BOOL.map(Payload::new, Payload::held);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    static void onRegisterKeys(RegisterKeyMappingsEvent e) {
        e.register(KEY);
    }

    static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
        e.registrar("1").optional().playToServer(Payload.TYPE, Payload.CODEC, (p, ctx) -> {});
    }

    static void onRegisterLayers(RegisterGuiLayersEvent e) {
        e.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(ZhivoyMirClient.MOD_ID, "npc_talk"),
                (GuiGraphics g, DeltaTracker delta) -> {
                    if (!sent) return;
                    Minecraft mc = Minecraft.getInstance();
                    g.drawCenteredString(mc.font, Component.translatable("zmclient.talk.active").withStyle(ChatFormatting.GOLD),
                            g.guiWidth() / 2, g.guiHeight() / 2 + 12, 0xFFFFFF);
                });
    }

    static void onClientTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        update(mc.screen == null && mc.isWindowActive() && KEY.isDown());
    }

    /** Любое окно (чат, инвентарь, меню) — клавиша больше не считается зажатой. */
    static void onScreenOpening(ScreenEvent.Opening e) {
        if (e.getNewScreen() != null) update(false);
    }

    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut e) {
        sent = false;
    }

    private static void update(boolean held) {
        if (held == sent) return;
        ClientPacketListener con = Minecraft.getInstance().getConnection();
        if (con == null || !con.hasChannel(Payload.TYPE)) {
            sent = false;  // сервер без livingvoice: слать некуда, подсказку не показываем
            return;
        }
        con.send(new Payload(held));
        sent = held;
    }
}
