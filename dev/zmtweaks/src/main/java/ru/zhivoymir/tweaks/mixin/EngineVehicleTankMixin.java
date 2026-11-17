package ru.zhivoymir.tweaks.mixin;

import immersive_aircraft.AircraftStats;
import immersive_aircraft.cobalt.registration.CobaltFuelRegistry;
import immersive_aircraft.entity.EngineVehicle;
import immersive_aircraft.entity.inventory.VehicleInventoryDescription;
import immersive_aircraft.entity.inventory.slots.SlotDescription;
import immersive_aircraft.util.Utils;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.zhivoymir.tweaks.aircraft.FuelTank;
import ru.zhivoymir.tweaks.aircraft.PsyCarrier;

/**
 * Внутренний бак 10 000 мБ у всей авиации (EngineVehicle, в т.ч. Man of Many Planes).
 * Каждый серверный тик бак доливается из ёмкостей в слотах котла: сколько влезет, ёмкость с
 * остатком остаётся в слоте, пустая заменяется на getContainer() (ведро сливается только целиком —
 * так устроен его обработчик). Котлы (fuel[i]) питаются из бака порциями по 10 мБ, пока fuel[i] ≤ 1000;
 * путь твёрдого топлива IA 1.7.0 сохранён как был. Заодно здесь NBT психологического оружия винтокрыла.
 */
@Mixin(EngineVehicle.class)
public abstract class EngineVehicleTankMixin implements FuelTank {
    @Shadow
    @Final
    private int[] fuel;

    @Unique
    private int zmtweaks$tank;
    @Unique
    private Fluid zmtweaks$tankFluid = Fluids.EMPTY;

    @Override
    public int zmtweaks$tankAmount() {
        return zmtweaks$tank;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void zmtweaks$fillTank(CallbackInfo ci) {
        EngineVehicle self = (EngineVehicle) (Object) this;
        if (self.level().isClientSide || fuel.length == 0) return;
        Container inventory = self.getInventory();
        for (SlotDescription slot : self.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER)) {
            if (zmtweaks$tank >= CAPACITY) return;
            ItemStack stack = inventory.getItem(slot.index());
            if (stack.isEmpty() || Utils.getItemFuelTime(stack) > 0) continue;
            // как CobaltFuelRegistryImpl.refuelFluid: работаем с копией, в слот кладём getContainer()
            IFluidHandlerItem handler = stack.copy().getCapability(Capabilities.FluidHandler.ITEM);
            if (handler == null) continue;
            for (int t = 0; t < handler.getTanks(); ++t) {
                FluidStack inside = handler.getFluidInTank(t);
                if (inside.isEmpty() || Utils.getFluidFuelTime(inside.getFluid()) <= 0) continue;
                if (zmtweaks$tank > 0 && !inside.is(zmtweaks$tankFluid)) continue;  // в баке другое топливо
                FluidStack request = inside.copyWithAmount(Math.min(inside.getAmount(), CAPACITY - zmtweaks$tank));
                FluidStack drained = handler.drain(request, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty() || !drained.is(inside.getFluid())) continue;
                zmtweaks$tank += drained.getAmount();
                zmtweaks$tankFluid = inside.getFluid();
                inventory.setItem(slot.index(), handler.getContainer());
                break;
            }
        }
    }

    /** Замена приватного EngineVehicle.refuel(int): твёрдое топливо — как в IA, жидкое — из бака. */
    @Inject(method = "refuel(I)V", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$refuelFromTank(int i, CallbackInfo ci) {
        ci.cancel();
        EngineVehicle self = (EngineVehicle) (Object) this;
        List<SlotDescription> slots = self.getInventoryDescription().getSlots(VehicleInventoryDescription.BOILER);
        if (i >= slots.size()) return;
        int slot = slots.get(i).index();
        while (fuel[i] <= EngineVehicle.TARGET_FUEL) {
            ItemStack stack = self.getInventory().getItem(slot);
            int time = Utils.getItemFuelTime(stack);
            if (time > 0) {
                ItemStack remainingItem = CobaltFuelRegistry.INSTANCE.getCraftingRemainingItem(stack);
                stack.shrink(1);
                if (stack.isEmpty()) self.getInventory().setItem(slot, remainingItem);
            } else {
                time = zmtweaks$drainTank();
            }
            if (time <= 0) break;
            fuel[i] += time;
            if (self.getControllingPassenger() instanceof ServerPlayer player) {
                player.awardStat(AircraftStats.FUEL_BURNED, time);
            }
        }
    }

    /** Порция до 10 мБ из бака → единицы горения пропорционально курсу жидкости (керосин: 10 мБ = 264). */
    @Unique
    private int zmtweaks$drainTank() {
        if (zmtweaks$tank <= 0) return 0;
        int perBucket = Utils.getFluidFuelTime(zmtweaks$tankFluid);
        if (perBucket <= 0) return 0;  // жидкость убрали из конфига — бак стоит
        int amount = Math.min(PORTION, zmtweaks$tank);
        zmtweaks$tank -= amount;
        if (zmtweaks$tank == 0) zmtweaks$tankFluid = Fluids.EMPTY;
        return Math.max(1, Math.round(perBucket * (amount / 1000.0f)));
    }

    @Inject(method = "isFuelLow", at = @At("HEAD"), cancellable = true)
    private void zmtweaks$isFuelLow(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(FuelTank.isFuelLow((EngineVehicle) (Object) this));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void zmtweaks$save(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("ZmTank", zmtweaks$tank);
        tag.putString("ZmTankFluid", BuiltInRegistries.FLUID.getKey(zmtweaks$tankFluid).toString());
        if (this instanceof PsyCarrier psy) tag.putBoolean("ZmPsyWeapon", psy.zmtweaks$psy().on);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void zmtweaks$load(CompoundTag tag, CallbackInfo ci) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("ZmTankFluid"));
        zmtweaks$tankFluid = id == null ? Fluids.EMPTY : BuiltInRegistries.FLUID.get(id);
        zmtweaks$tank = zmtweaks$tankFluid == Fluids.EMPTY ? 0 : Math.clamp(tag.getInt("ZmTank"), 0, CAPACITY);
        if (this instanceof PsyCarrier psy) psy.zmtweaks$psy().on = tag.getBoolean("ZmPsyWeapon");
    }
}
