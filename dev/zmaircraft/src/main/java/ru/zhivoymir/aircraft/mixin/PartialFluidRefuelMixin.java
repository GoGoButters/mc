package ru.zhivoymir.aircraft.mixin;

import immersive_aircraft.neoforge.cobalt.registration.CobaltFuelRegistryImpl;
import immersive_aircraft.util.Utils;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Жидкое топливо: вместо «ровно 1000 мБ или ничего» сливается min(остаток, 1000) мБ,
 * время горения — пропорционально слитому. Работают неполные ёмкости и ёмкости меньше ведра
 * (в т.ч. проверка FuelSlot.mayPlace через getFluidFuelTime, simulate=true).
 */
@Mixin(CobaltFuelRegistryImpl.class)
public abstract class PartialFluidRefuelMixin {
    @Inject(method = "refuelFluid(Lnet/neoforged/neoforge/fluids/capability/IFluidHandlerItem;Z)I",
            at = @At("HEAD"), cancellable = true)
    private void zmaircraft$partialRefuel(IFluidHandlerItem handler, boolean simulate, CallbackInfoReturnable<Integer> cir) {
        if (handler == null) {
            cir.setReturnValue(0);
            return;
        }
        for (int tank = 0; tank < handler.getTanks(); ++tank) {
            FluidStack fluid = handler.getFluidInTank(tank);
            int time = fluid.isEmpty() ? 0 : Utils.getFluidFuelTime(fluid.getFluid());
            if (time <= 0) continue;
            FluidStack request = fluid.copyWithAmount(Math.min(fluid.getAmount(), 1000));
            FluidStack drained = handler.drain(request, IFluidHandler.FluidAction.SIMULATE);
            if (drained.isEmpty() || !FluidStack.isSameFluidSameComponents(drained, request)) continue;
            int amount = drained.getAmount();
            if (!simulate) {
                drained = handler.drain(request.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty() || !FluidStack.isSameFluidSameComponents(drained, request)) {
                    cir.setReturnValue(0);
                    return;
                }
                amount = drained.getAmount();
            }
            cir.setReturnValue(Math.max(1, Math.round(time * (amount / 1000.0f))));
            return;
        }
        cir.setReturnValue(0);
    }
}
