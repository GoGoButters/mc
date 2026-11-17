package ru.zhivoymir.tweaks.mixin;

import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.InventoryVehicleEntity;
import immersive_aircraft.screen.VehicleScreenHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.tweaks.aircraft.TankDataSlot;
import ru.zhivoymir.tweaks.aircraft.TankMenu;

/**
 * Уровень бака в меню машины — DataSlot: сервер шлёт изменения в broadcastChanges каждый тик,
 * клиент создаёт то же меню (VehicleScreenRegistry → createMenu) с тем же порядком слотов данных.
 */
@Mixin(VehicleScreenHandler.class)
public abstract class VehicleScreenHandlerMixin extends AbstractContainerMenu implements TankMenu {
    @Unique
    private TankDataSlot zmtweaks$tankSlot;

    protected VehicleScreenHandlerMixin(MenuType<?> type, int id) {
        super(type, id);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void zmtweaks$addTankSlot(int syncId, Inventory playerInventory, InventoryVehicleEntity vehicle, CallbackInfo ci) {
        if (vehicle instanceof EngineVehicle engine && ((EngineVehicleAccessor) engine).zmtweaks$getFuel().length > 0) {
            zmtweaks$tankSlot = new TankDataSlot(engine);
            addDataSlot(zmtweaks$tankSlot);
        }
    }

    @Override
    public int zmtweaks$tankAmount() {
        return zmtweaks$tankSlot == null ? -1 : zmtweaks$tankSlot.get();
    }
}
