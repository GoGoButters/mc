package ru.zhivoymir.tweaks.mixin.client;

import immersive_aircraft.client.gui.VehicleScreen;
import immersive_aircraft.screen.VehicleScreenHandler;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.zhivoymir.tweaks.aircraft.FuelTank;
import ru.zhivoymir.tweaks.aircraft.TankMenu;
import ru.zhivoymir.tweaks.client.TankBar;

/** Индикатор бака у первого котла (TankBar) и подсказка «Керосин: N / 10 000 мБ» при наведении. */
@Mixin(VehicleScreen.class)
public abstract class VehicleScreenMixin extends AbstractContainerScreen<VehicleScreenHandler> {
    @Unique
    private TankBar zmtweaks$bar;

    protected VehicleScreenMixin(VehicleScreenHandler menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Unique
    private TankBar zmtweaks$bar() {
        if (zmtweaks$bar == null && ((TankMenu) menu).zmtweaks$tankAmount() >= 0) {
            zmtweaks$bar = TankBar.of(menu.getVehicle().getInventoryDescription());
        }
        return zmtweaks$bar;
    }

    /** После спрайтов слотов — полоса поверх фона окна. */
    @Inject(method = "renderBg", at = @At("TAIL"))
    private void zmtweaks$renderTank(GuiGraphics g, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        TankBar bar = zmtweaks$bar();
        if (bar != null) {
            bar.render(g, leftPos, topPos, ((TankMenu) menu).zmtweaks$tankAmount(), FuelTank.CAPACITY, FuelTank.LOW);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void zmtweaks$tankTooltip(GuiGraphics g, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TankBar bar = zmtweaks$bar();
        if (bar != null && menu.getCarried().isEmpty() && bar.isHovered(leftPos, topPos, mouseX, mouseY)) {
            g.renderTooltip(font, Component.translatable("gui.zmtweaks.tank",
                    zmtweaks$format(((TankMenu) menu).zmtweaks$tankAmount()), zmtweaks$format(FuelTank.CAPACITY)), mouseX, mouseY);
        }
    }

    /** 10000 → «10 000». */
    @Unique
    private static String zmtweaks$format(int value) {
        return String.format(Locale.ROOT, "%,d", value).replace(',', ' ');
    }
}
