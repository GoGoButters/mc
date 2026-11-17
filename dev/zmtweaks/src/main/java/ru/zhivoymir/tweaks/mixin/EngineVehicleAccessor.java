package ru.zhivoymir.tweaks.mixin;

import immersive_aircraft.entity.EngineVehicle;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Доступ к приватному состоянию топлива EngineVehicle — для повтора его логики в винтокрыле. */
@Mixin(EngineVehicle.class)
public interface EngineVehicleAccessor {
    @Accessor("fuel")
    int[] zmtweaks$getFuel();

    @Accessor("UTILIZATION")
    static EntityDataAccessor<Float> zmtweaks$utilization() {
        throw new AssertionError();
    }

    @Accessor("LOW_ON_FUEL")
    static EntityDataAccessor<Boolean> zmtweaks$lowOnFuel() {
        throw new AssertionError();
    }
}
