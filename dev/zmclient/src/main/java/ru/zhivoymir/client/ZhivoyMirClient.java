package ru.zhivoymir.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/** Клиентские правки «Живого мира»: миксины, подписчики событий и метки колоний на карте. */
@Mod(value = ZhivoyMirClient.MOD_ID, dist = Dist.CLIENT)
public final class ZhivoyMirClient {
    public static final String MOD_ID = "zmclient";

    public ZhivoyMirClient(IEventBus bus) {
        bus.addListener(ColonyWaypoints::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(ColonyWaypoints::onClientTick);
        NeoForge.EVENT_BUS.addListener(ColonyWaypoints::onLoggingOut);
    }
}
