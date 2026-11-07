package ru.zhivoymir.livingvoice.mixin;

import me.bytefox.livingcolonists.talk.ChatSpeechOutput;
import me.bytefox.livingcolonists.talk.Speaker;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.livingvoice.ColonistVoice;

import java.util.Collection;

/** Каждая реплика колониста в LC проходит через ChatSpeechOutput — отсюда её и озвучиваем. */
@Mixin(value = ChatSpeechOutput.class, remap = false)
public class ChatSpeechOutputMixin {
    @Inject(method = "colonistSaid", at = @At("HEAD"))
    private void livingvoice$said(Speaker speaker, String text, Collection<ServerPlayer> recipients, CallbackInfo ci) {
        ColonistVoice.say(speaker, recipients.isEmpty() ? null : recipients.iterator().next(), text);
    }

    @Inject(method = "colonistWhispered", at = @At("HEAD"))
    private void livingvoice$whispered(Speaker speaker, ServerPlayer player, String text, CallbackInfo ci) {
        ColonistVoice.say(speaker, player, text);
    }
}
