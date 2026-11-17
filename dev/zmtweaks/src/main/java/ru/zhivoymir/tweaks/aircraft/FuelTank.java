package ru.zhivoymir.tweaks.aircraft;

import immersive_aircraft.config.Config;
import immersive_aircraft.entity.EngineVehicle;
import ru.zhivoymir.tweaks.mixin.EngineVehicleAccessor;

/**
 * Внутренний бак авиации (реализует EngineVehicleTankMixin на всех EngineVehicle): жидкое топливо
 * из ёмкостей в слотах котла сначала переливается в бак, котлы питаются из бака порциями.
 */
public interface FuelTank {
    int CAPACITY = 10_000;
    /** Порция, которую котёл забирает из бака за раз, мБ. */
    int PORTION = 10;
    /** Бак ниже этого уровня (и котлы почти пусты) — «мало топлива». */
    int LOW = 1_000;

    int zmtweaks$tankAmount();

    /**
     * isFuelLow для EngineVehicle и винтокрыла: как в IA 1.7.0 (все котлы ≤ 900), но только когда
     * и в баке меньше 1000 мБ. Клиент читает синхронизированный флаг.
     */
    static boolean isFuelLow(EngineVehicle vehicle) {
        if (!Config.getInstance().burnFuelInCreative && vehicle.isPilotCreative()) {
            return false;
        }
        if (vehicle.level().isClientSide) {
            return vehicle.getEntityData().get(EngineVehicleAccessor.zmtweaks$lowOnFuel());
        }
        boolean low = ((FuelTank) vehicle).zmtweaks$tankAmount() < LOW;
        if (low) {
            for (int i : ((EngineVehicleAccessor) vehicle).zmtweaks$getFuel()) {
                if (i <= EngineVehicle.LOW_FUEL) continue;
                low = false;
                break;
            }
        }
        vehicle.getEntityData().set(EngineVehicleAccessor.zmtweaks$lowOnFuel(), low);
        return low;
    }
}
