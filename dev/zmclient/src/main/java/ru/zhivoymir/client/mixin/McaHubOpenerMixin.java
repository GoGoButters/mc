package ru.zhivoymir.client.mixin;

import net.conczin.mca.client.gui.InteractScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * В окне «Говорить» ответ MCA выполняет «say» (реплика жителя) и сразу «next» (возврат к списку тем
 * со вступительной фразой «Слушаю. О чём ещё?»). Обе реплики приходят в один тик: вступление
 * затирает ответ на экране, а озвучка звучит одновременно. Вступление, пришедшее следом за другой
 * репликой, пропускаем: на экране остаётся ответ, кнопки тем приходят отдельным пакетом.
 */
@Mixin(value = InteractScreen.class, remap = false)
public class McaHubOpenerMixin {
    @Unique
    private static final long ZM_WINDOW_MS = 1500;
    @Unique
    private long zmclient$lastPhraseAt;

    @Inject(method = "setLastPhrase", at = @At("HEAD"), cancellable = true)
    private void zmclient$skipHubOpener(Component text, boolean silent, CallbackInfo ci) {
        long now = System.currentTimeMillis();
        boolean opener = text.getContents() instanceof TranslatableContents tc
                && tc.getKey().endsWith("dialogue.conversations");
        if (opener && now - zmclient$lastPhraseAt < ZM_WINDOW_MS) {
            ci.cancel();
            return;
        }
        zmclient$lastPhraseAt = now;
    }
}
