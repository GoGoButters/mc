package ru.zhivoymir.tweaks.aircraft;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Винтокрыл с психологическим оружием (GyrodyneMixin). Состояние живёт только на сервере. */
public interface PsyCarrier {
    State zmtweaks$psy();

    final class State {
        /** Музыка включена; пишется в NBT (EngineVehicleTankMixin). */
        public boolean on;
        /** Игроки, которым отправлен start и ещё не отправлен stop. */
        public final Set<UUID> listeners = new HashSet<>();
        /** tickCount последнего переключения — защита от дребезга клавиши. */
        public int lastToggle = -100;
    }
}
