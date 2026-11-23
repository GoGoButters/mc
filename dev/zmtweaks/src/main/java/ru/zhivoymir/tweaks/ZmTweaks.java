package ru.zhivoymir.tweaks;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import ru.zhivoymir.tweaks.aircraft.PassengerDismount;
import ru.zhivoymir.tweaks.aircraft.PsyWeapon;
import ru.zhivoymir.tweaks.auth.AuthCommand;
import ru.zhivoymir.tweaks.auth.NedologinAuth;

/** Правки чужих модов — в миксинах (zmtweaks.mixins.json); здесь звуки, сеть психологического оружия,
 * конфиг сервера и команда /zmauth (пароли платформ, zmtweaks.nedologin.mixins.json). */
@Mod(ZmTweaks.MOD_ID)
public final class ZmTweaks {
    public static final String MOD_ID = "zmtweaks";

    public ZmTweaks(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, ZmConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(AuthCommand::register);
        NeoForge.EVENT_BUS.addListener(NedologinAuth::onServerStopped);
        ZmSounds.REGISTER.register(bus);
        bus.addListener(PsyWeapon::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(PsyWeapon::onEntityLeave);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PassengerDismount::onInteractSpecific);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PassengerDismount::onInteract);
    }
}
