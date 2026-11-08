package ru.zhivoymir.god;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** Слова бога: текст в чат и, при Living Voice, голос «в голове» игрока. */
final class GodVoice {
    private GodVoice() {}

    static void say(ServerPlayer p, String text) {
        p.sendSystemMessage(Component.literal("Голос свыше: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("«" + text + "»").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC)));
        if (God.LIVING_VOICE) LvBridge.speak(p, text);
    }
}
