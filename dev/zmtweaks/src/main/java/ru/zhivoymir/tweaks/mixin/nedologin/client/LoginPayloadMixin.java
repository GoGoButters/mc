package ru.zhivoymir.tweaks.mixin.nedologin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.zhivoymir.tweaks.auth.PlatformAuth;

/**
 * Клиент Nedologin шлёт серверу двойной SHA-256 пароля, и префикс «zm-android:» сервер не видит.
 * Здесь дайджест такого пароля получает ту же метку; сервер (LoginHandlerMixin) её снимает.
 * Только пакет входа: смена пароля через Nedologin идёт без метки.
 */
@Mixin(targets = "ariss.nedologin.common.network.NLPackets_neoforge", remap = false)
public abstract class LoginPayloadMixin {
    @WrapOperation(method = "loginPayload", require = 0, at = @At(value = "INVOKE",
            target = "Lariss/nedologin/common/network/NLPackets_neoforge;passwordDigest(Ljava/lang/String;)Ljava/lang/String;"))
    private static String zmtweaks$markAndroid(String password, Operation<String> original) {
        String digest = original.call(password);
        return PlatformAuth.platformOf(password).equals(PlatformAuth.ANDROID) ? PlatformAuth.ANDROID_PREFIX + digest : digest;
    }
}
