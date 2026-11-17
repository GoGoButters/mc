package ru.zhivoymir.tweaks.mixin;

import java.util.function.LongSupplier;
import mekanism.common.content.blocktype.Factory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Фабрики Mekanism (все типы и уровни) тратят вдвое больше энергии на каждую линию и имеют вдвое
 * больший буфер. Правится AttributeEnergy, который Factory.setMachineData собирает из атрибута
 * одиночной машины: от него MachineEnergyContainer берёт базовый расход и ёмкость, а апгрейды
 * (MekanismUtils.getEnergyPerTick/getMaxEnergy) умножают базу — итог тоже ×2. GUI фабрики (лента
 * энергии, вкладка расхода по lastUsage) и тултип блока читают те же значения. Атрибут одиночной
 * машины не меняется.
 */
@Mixin(Factory.class)
public abstract class FactoryEnergyMixin {
    private static final String ATTRIBUTE_ENERGY_INIT =
            "Lmekanism/common/block/attribute/AttributeEnergy;<init>(Ljava/util/function/LongSupplier;Ljava/util/function/LongSupplier;)V";

    @ModifyArg(method = "setMachineData", at = @At(value = "INVOKE", target = ATTRIBUTE_ENERGY_INIT), index = 0)
    private LongSupplier zmtweaks$doubleUsage(LongSupplier usage) {
        return () -> zmtweaks$double(usage.getAsLong());
    }

    @ModifyArg(method = "setMachineData", at = @At(value = "INVOKE", target = ATTRIBUTE_ENERGY_INIT), index = 1)
    private LongSupplier zmtweaks$doubleStorage(LongSupplier storage) {
        return () -> zmtweaks$double(storage.getAsLong());
    }

    private static long zmtweaks$double(long value) {
        return value > Long.MAX_VALUE / 2 ? Long.MAX_VALUE : value * 2;
    }
}
