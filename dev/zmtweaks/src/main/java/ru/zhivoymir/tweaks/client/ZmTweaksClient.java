package ru.zhivoymir.tweaks.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import ru.zhivoymir.tweaks.ZmTweaks;

/** Клиентская часть: клавиша психологического оружия и музыка. */
@Mod(value = ZmTweaks.MOD_ID, dist = Dist.CLIENT)
public final class ZmTweaksClient {
    public ZmTweaksClient(IEventBus bus) {
        bus.addListener(PsyClient::onRegisterKeys);
        NeoForge.EVENT_BUS.addListener(PsyClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(PsyClient::onLoggingOut);
    }
}
