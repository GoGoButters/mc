package ru.zhivoymir.tweaks;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import ru.zhivoymir.tweaks.aircraft.PassengerDismount;
import ru.zhivoymir.tweaks.aircraft.PsyWeapon;

/** Правки чужих модов — в миксинах (zmtweaks.mixins.json); здесь звуки и сеть психологического оружия. */
@Mod(ZmTweaks.MOD_ID)
public final class ZmTweaks {
    public static final String MOD_ID = "zmtweaks";

    public ZmTweaks(IEventBus bus) {
        ZmSounds.REGISTER.register(bus);
        bus.addListener(PsyWeapon::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(PsyWeapon::onEntityLeave);
        NeoForge.EVENT_BUS.addListener(PassengerDismount::onInteractSpecific);
        NeoForge.EVENT_BUS.addListener(PassengerDismount::onInteract);
    }
}
