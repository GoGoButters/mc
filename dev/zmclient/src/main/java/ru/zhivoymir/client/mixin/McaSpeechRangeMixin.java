package ru.zhivoymir.client.mixin;

import net.conczin.mca.client.tts.SpeechManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * MCA озвучивает реплики жителей звуком с дальностью затухания 16 блоков (зашито в SpeechManager.getSound),
 * и уже в нескольких блоках их почти не слышно. Удваиваем дальность: громкость вплотную та же,
 * затухание до нуля — на 32 блоках вместо 16.
 */
@Mixin(value = SpeechManager.class, remap = false)
public class McaSpeechRangeMixin {
    private static final int RANGE = 32;

    @ModifyArg(method = "getSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/Sound;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/util/valueproviders/SampledFloat;Lnet/minecraft/util/valueproviders/SampledFloat;ILnet/minecraft/client/resources/sounds/Sound$Type;ZZI)V"),
            index = 7)
    private int zmclient$range(int attenuationDistance) {
        return Math.max(attenuationDistance, RANGE);
    }
}
