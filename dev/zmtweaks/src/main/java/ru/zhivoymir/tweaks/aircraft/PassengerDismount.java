package ru.zhivoymir.tweaks.aircraft;

import immersive_aircraft.entity.VehicleEntity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * ПКМ с присядом высаживает NPC из авиации: по самому пассажиру — его, по транспорту снаружи — всех не-игроков
 * (IA сама высаживает только место 0). Пакет взаимодействия клиент шлёт до события, так что отмена на клиенте
 * не мешает серверу получить своё событие.
 */
public final class PassengerDismount {
    private PassengerDismount() {}

    public static void onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (handle(event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        }
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (handle(event.getEntity(), event.getHand(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        }
    }

    private static boolean handle(Player player, InteractionHand hand, Entity target) {
        if (hand != InteractionHand.MAIN_HAND || !player.isShiftKeyDown() || target instanceof Player) {
            return false;
        }
        boolean server = !player.level().isClientSide;
        if (target instanceof VehicleEntity vehicle) {
            if (player.getVehicle() == vehicle) {
                return false;
            }
            List<Entity> npcs = new ArrayList<>();
            for (Entity passenger : vehicle.getPassengers()) {
                if (!(passenger instanceof Player)) {
                    npcs.add(passenger);
                }
            }
            if (npcs.isEmpty()) {
                return false;
            }
            if (server) {
                npcs.forEach(PassengerDismount::dismount);
            }
            return true;
        }
        if (!(target.getVehicle() instanceof VehicleEntity)) {
            return false;
        }
        if (server) {
            dismount(target);
        }
        return true;
    }

    // stopRiding ставит пассажира в VehicleEntity.getDismountLocationForPassenger
    private static void dismount(Entity passenger) {
        Recruits.clearMount(passenger);
        passenger.stopRiding();
    }

    /** Рекрут с приказом посадки (RecruitMountEntityGoal) сядет обратно — снимаем приказ, как кнопка «Спешиться». */
    private static final class Recruits {
        private static final Class<?> RECRUIT;
        private static final Method SHOULD_MOUNT;
        private static final Field DISMOUNT;

        static {
            Class<?> cls = null;
            Method shouldMount = null;
            Field dismount = null;
            try {
                cls = Class.forName("com.talhanation.recruits.entities.AbstractRecruitEntity");
                shouldMount = cls.getMethod("shouldMount", boolean.class, UUID.class);
                dismount = cls.getField("dismount");
            } catch (ReflectiveOperationException | LinkageError e) {
                cls = null;
            }
            RECRUIT = cls;
            SHOULD_MOUNT = shouldMount;
            DISMOUNT = dismount;
        }

        static void clearMount(Entity entity) {
            if (RECRUIT == null || !RECRUIT.isInstance(entity)) {
                return;
            }
            try {
                SHOULD_MOUNT.invoke(entity, false, null);
                DISMOUNT.setInt(entity, 180);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                // другая версия recruits — просто высаживаем
            }
        }
    }
}
