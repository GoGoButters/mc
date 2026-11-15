package ru.zhivoymir.bridge.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Ванильный SleepInBed берёт HOME через Optional.get(); MCA может стереть дом в том же тике — сервер падал. Нет дома — не спим. */
@Mixin(value = SleepInBed.class, remap = false)
public abstract class SleepInBedHomeMixin {
    @Inject(method = "checkExtraStartConditions", at = @At("HEAD"), cancellable = true)
    private void zmbridge$noHome(ServerLevel level, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (owner.getBrain().getMemory(MemoryModuleType.HOME).isEmpty()) cir.setReturnValue(false);
    }
}
