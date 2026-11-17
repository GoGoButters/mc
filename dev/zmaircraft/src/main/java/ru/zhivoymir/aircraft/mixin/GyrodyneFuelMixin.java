package ru.zhivoymir.aircraft.mixin;

import immersive_aircraft.config.Config;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.GyrodyneEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Винтокрыл на обычном топливе (котёл) вместо сытости пилота.
 * isFuelLow/getFuelUtilization повторяют EngineVehicle 1.7.0 (super из миксина не вызвать:
 * виртуальный вызов вернётся в GyrodyneEntity). Раскрутка в updateController не тронута.
 */
@Mixin(GyrodyneEntity.class)
public abstract class GyrodyneFuelMixin {
    @Inject(method = "getFuelType", at = @At("HEAD"), cancellable = true)
    private void zmaircraft$fuelType(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("fuel");
    }

    @Inject(method = "getGuiStyle", at = @At("HEAD"), cancellable = true)
    private void zmaircraft$guiStyle(CallbackInfoReturnable<EngineVehicle.GUI_STYLE> cir) {
        cir.setReturnValue(EngineVehicle.GUI_STYLE.ENGINE);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void zmaircraft$noExhaustion(ServerPlayer player, float exhaustion) {
        // пилот не голодает: топливо тратит EngineVehicle.tick
    }

    @Inject(method = "isFuelLow", at = @At("HEAD"), cancellable = true)
    private void zmaircraft$isFuelLow(CallbackInfoReturnable<Boolean> cir) {
        GyrodyneEntity self = (GyrodyneEntity) (Object) this;
        if (!Config.getInstance().burnFuelInCreative && self.isPilotCreative()) {
            cir.setReturnValue(false);
            return;
        }
        if (self.level().isClientSide) {
            cir.setReturnValue(self.getEntityData().get(EngineVehicleAccessor.zmaircraft$lowOnFuel()));
            return;
        }
        boolean low = true;
        for (int i : ((EngineVehicleAccessor) self).zmaircraft$getFuel()) {
            if (i <= 900) continue;
            low = false;
            break;
        }
        self.getEntityData().set(EngineVehicleAccessor.zmaircraft$lowOnFuel(), low);
        cir.setReturnValue(low);
    }

    @Inject(method = "getFuelUtilization", at = @At("HEAD"), cancellable = true)
    private void zmaircraft$fuelUtilization(CallbackInfoReturnable<Float> cir) {
        GyrodyneEntity self = (GyrodyneEntity) (Object) this;
        int[] fuel = ((EngineVehicleAccessor) self).zmaircraft$getFuel();
        if (Config.getInstance().fuelConsumption == 0.0f
                || (!Config.getInstance().burnFuelInCreative && self.isPilotCreative())
                || fuel.length == 0) {
            cir.setReturnValue(1.0f);
            return;
        }
        if (self.level().isClientSide) {
            cir.setReturnValue(self.getEntityData().get(EngineVehicleAccessor.zmaircraft$utilization()));
            return;
        }
        int running = 0;
        for (int i : fuel) {
            if (i <= 0) continue;
            ++running;
        }
        float utilization = (float) running / (float) fuel.length * (self.isFuelLow() ? 0.75f : 1.0f);
        self.getEntityData().set(EngineVehicleAccessor.zmaircraft$utilization(), utilization);
        cir.setReturnValue(utilization);
    }
}
