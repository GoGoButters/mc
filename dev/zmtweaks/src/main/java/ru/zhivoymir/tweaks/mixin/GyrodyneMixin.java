package ru.zhivoymir.tweaks.mixin;

import immersive_aircraft.Sounds;
import immersive_aircraft.config.Config;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.GyrodyneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zhivoymir.tweaks.ZmSounds;
import ru.zhivoymir.tweaks.aircraft.FuelTank;
import ru.zhivoymir.tweaks.aircraft.PsyCarrier;
import ru.zhivoymir.tweaks.aircraft.PsyWeapon;

/**
 * Винтокрыл как вертолёт с ДВС: топливо из котла вместо сытости пилота, мгновенный запуск
 * по клавише подъёма вместо раскрутки педалями, мотор биплана вдвое тише, дым из выхлопной трубы,
 * психологическое оружие. isFuelLow/getFuelUtilization повторяют EngineVehicle (super из миксина
 * не вызвать: виртуальный вызов вернётся в GyrodyneEntity).
 */
@Mixin(GyrodyneEntity.class)
public abstract class GyrodyneMixin implements PsyCarrier {
    @Unique
    private final PsyCarrier.State zmtweaks$psyState = new PsyCarrier.State();

    @Override
    public PsyCarrier.State zmtweaks$psy() {
        return zmtweaks$psyState;
    }

    /** Психологическое оружие: рассылка start/pos/stop слушателям в радиусе 150 блоков. */
    @Inject(method = "tick", at = @At("TAIL"))
    private void zmtweaks$psyTick(CallbackInfo ci) {
        GyrodyneEntity self = (GyrodyneEntity) (Object) this;
        if (!self.level().isClientSide) PsyWeapon.serverTick(self);
    }

    @Inject(method = "getFuelType", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$fuelType(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("fuel");
    }

    @Inject(method = "getGuiStyle", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$guiStyle(CallbackInfoReturnable<EngineVehicle.GUI_STYLE> cir) {
        cir.setReturnValue(EngineVehicle.GUI_STYLE.ENGINE);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void zmtweaks$noExhaustion(ServerPlayer player, float exhaustion) {
        // пилот не голодает: топливо тратит EngineVehicle.tick
    }

    /** Та же логика, что у всей авиации: мало топлива — бак < 1000 мБ и котёл почти пуст. */
    @Inject(method = "isFuelLow", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$isFuelLow(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(FuelTank.isFuelLow((GyrodyneEntity) (Object) this));
    }

    @Inject(method = "getFuelUtilization", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$fuelUtilization(CallbackInfoReturnable<Float> cir) {
        GyrodyneEntity self = (GyrodyneEntity) (Object) this;
        int[] fuel = ((EngineVehicleAccessor) self).zmtweaks$getFuel();
        if (Config.getInstance().fuelConsumption == 0.0f
                || (!Config.getInstance().burnFuelInCreative && self.isPilotCreative())
                || fuel.length == 0) {
            cir.setReturnValue(1.0f);
            return;
        }
        if (self.level().isClientSide) {
            cir.setReturnValue(self.getEntityData().get(EngineVehicleAccessor.zmtweaks$utilization()));
            return;
        }
        int running = 0;
        for (int i : fuel) {
            if (i <= 0) continue;
            ++running;
        }
        float utilization = (float) running / (float) fuel.length * (self.isFuelLow() ? 0.75f : 1.0f);
        self.getEntityData().set(EngineVehicleAccessor.zmtweaks$utilization(), utilization);
        cir.setReturnValue(utilization);
    }

    /**
     * Первая проверка {@code getEngineTarget() < 1} в updateController — вход в раскрутку педалями
     * (engineTarget + Z*0.05 - 0.035, тултипы gyrodyne_target*). Возвращаем 1, чтобы её пропустить,
     * а запуск делаем сами: пилот на месте, есть топливо, нажат подъём — сразу полная мощность.
     * Тяга, вертикаль и толкание по земле при заглушенном двигателе остаются оригинальными.
     */
    @Redirect(method = "updateController", at = @At(value = "INVOKE",
            target = "Limmersive_aircraft/entity/GyrodyneEntity;getEngineTarget()F", ordinal = 0))
    private float zmtweaks$instantStart(GyrodyneEntity self) {
        if (self.getEngineTarget() < 1.0f && self.getControllingPassenger() != null
                && self.getFuelUtilization() > 0.0f && self.pressingInterpolatedY.getValue() > 0.0f) {
            self.setEngineTarget(1.0f);
            if (self.onGround()) {
                // как в оригинале при выходе на полную мощность
                self.setDeltaMovement(self.getDeltaMovement().add(0.0, 0.25, 0.0));
            }
        }
        return 1.0f;
    }

    /** Звук запуска — дефолт EngineVehicle (так звучит алый биплан MMP), вместо WOOSH. */
    @Inject(method = "getEngineStartSound", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$engineStartSound(CallbackInfoReturnable<SoundEvent> cir) {
        cir.setReturnValue(Sounds.ENGINE_START.get());
    }

    /**
     * Звук работы — пропеллер EngineVehicle вдвое тише: zmtweaks:gyrodyne_engine = файл
     * immersive_aircraft:propeller с volume 0.5. IA играет его с громкостью min(1, 0.25 + раскрутка),
     * клиент умножает её на volume из sounds.json, а дистанция затухания max(громкость, 1) × 24
     * не меняется — итог ровно ×0.5.
     */
    @Inject(method = "getEngineSound", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$engineSound(CallbackInfoReturnable<SoundEvent> cir) {
        cir.setReturnValue(ZmSounds.GYRODYNE_ENGINE.get());
    }

    /**
     * Дым из среза выхлопной трубы (модель поднята на 0.25 — полозья на земле), через тик, как у биплана.
     * emitSmokeParticle сам проверяет клиент, дальность частиц и мощность > 0.05.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void zmtweaks$exhaustSmoke(CallbackInfo ci) {
        GyrodyneEntity self = (GyrodyneEntity) (Object) this;
        if (self.level().isClientSide && self.tickCount % 2 == 0 && self.getEnginePower() > 0.0f) {
            self.emitSmokeParticle(0.3125f, 0.625f, -2.25f, 0.0f, 0.05f, -0.2f);
        }
    }
}
