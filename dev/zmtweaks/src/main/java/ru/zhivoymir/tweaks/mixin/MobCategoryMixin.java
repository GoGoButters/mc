package ru.zhivoymir.tweaks.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Лимит естественного спавна враждебных мобов ×1,5 (70→105). Поле max читает только этот геттер,
 * а его единственный потребитель — NaturalSpawner.SpawnState#canSpawnForCategory (лимит × число
 * загруженных чанков / 289). Остальные категории не меняются.
 */
@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void zmtweaks$moreMonsters(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this == MobCategory.MONSTER) {
            cir.setReturnValue(Math.round(cir.getReturnValueI() * 1.5f));
        }
    }
}
