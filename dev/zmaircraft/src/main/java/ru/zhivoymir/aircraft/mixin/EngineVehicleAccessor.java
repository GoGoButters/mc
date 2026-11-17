package ru.zhivoymir.aircraft.mixin;

import immersive_aircraft.entity.EngineVehicle;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Доступ к приватному состоянию топлива EngineVehicle — для повтора его логики в винтокрыле. */
@Mixin(EngineVehicle.class)
public interface EngineVehicleAccessor {
    @Accessor("fuel")
    int[] zmaircraft$getFuel();

    @Accessor("UTILIZATION")
    static EntityDataAccessor<Float> zmaircraft$utilization() {
        throw new AssertionError();
    }

    @Accessor("LOW_ON_FUEL")
    static EntityDataAccessor<Boolean> zmaircraft$lowOnFuel() {
        throw new AssertionError();
    }
}
