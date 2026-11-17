package ru.zhivoymir.tweaks.aircraft;

import immersive_aircraft.entity.EngineVehicle;
import net.minecraft.world.inventory.DataSlot;

/**
 * Уровень бака в меню машины (VehicleScreenHandler): сервер читает бак сущности, клиент хранит
 * присланное значение. Пакет данных меню — short, 10 000 влезает.
 */
public final class TankDataSlot extends DataSlot {
    private final EngineVehicle vehicle;
    private int clientValue;

    public TankDataSlot(EngineVehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public int get() {
        return vehicle.level().isClientSide ? clientValue : ((FuelTank) vehicle).zmtweaks$tankAmount();
    }

    @Override
    public void set(int value) {
        clientValue = value;
    }
}
