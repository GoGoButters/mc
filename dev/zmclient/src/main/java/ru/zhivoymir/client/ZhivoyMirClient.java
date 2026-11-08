package ru.zhivoymir.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/** Клиентские правки «Живого мира». Вся работа — в миксинах. */
@Mod(value = ZhivoyMirClient.MOD_ID, dist = Dist.CLIENT)
public final class ZhivoyMirClient {
    public static final String MOD_ID = "zmclient";
}
