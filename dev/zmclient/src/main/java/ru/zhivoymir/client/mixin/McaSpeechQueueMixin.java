package ru.zhivoymir.client.mixin;

import net.conczin.mca.MCA;
import net.conczin.mca.client.tts.AudioCache;
import net.conczin.mca.client.tts.OnlineSpeechManager;
import net.conczin.mca.client.tts.RealtimeSpeechManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.client.SpeechQueue;

import java.io.OutputStream;

/**
 * MCA запускает озвучку каждой реплики, как только скачан звук, и реплики одного жителя
 * накладываются. Звук качаем сразу (как MCA), а играем по очереди — через SpeechQueue.
 */
@Mixin(value = RealtimeSpeechManager.class, remap = false)
public abstract class McaSpeechQueueMixin {
    @Shadow
    public abstract void downloadAudio(OutputStream output, String voiceId, String text);

    @Inject(method = "play(Ljava/lang/String;FLnet/minecraft/world/entity/Entity;ZLjava/lang/String;)V",
            at = @At("HEAD"), cancellable = true)
    private void zmclient$queue(String phrase, float pitch, Entity entity, boolean cacheable, String voice, CallbackInfo ci) {
        ci.cancel();
        SpeechQueue.enqueue(entity, pitch, cacheable, () -> {
            String text = cacheable ? OnlineSpeechManager.cleanPhrase(phrase) : phrase;
            String hash = cacheable ? AudioCache.getHash(voice) + "/" + AudioCache.getHash(text) : "realtime";
            return AudioCache.get(hash, out -> downloadAudio(out, voice, text), cacheable)
                    ? MCA.locate("tts_cache/" + hash) : null;
        });
    }
}
