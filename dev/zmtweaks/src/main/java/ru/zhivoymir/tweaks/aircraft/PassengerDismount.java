package ru.zhivoymir.tweaks.aircraft;

import immersive_aircraft.entity.VehicleEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** ПКМ с присядом по пассажиру-NPC авиации высаживает его (в IA своей логики высадки нет). */
public final class PassengerDismount {
    private PassengerDismount() {}

    public static void onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (dismount(event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        }
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (dismount(event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        }
    }

    private static boolean dismount(Player player, InteractionHand hand, Entity target) {
        if (hand != InteractionHand.MAIN_HAND || !player.isShiftKeyDown()
                || target instanceof Player || !(target.getVehicle() instanceof VehicleEntity)) {
            return false;
        }
        // stopRiding ставит пассажира в VehicleEntity.getDismountLocationForPassenger
        if (!player.level().isClientSide) {
            target.stopRiding();
        }
        return true;
    }
}
