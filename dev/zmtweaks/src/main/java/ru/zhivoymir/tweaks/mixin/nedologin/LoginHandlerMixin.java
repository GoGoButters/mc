package ru.zhivoymir.tweaks.mixin.nedologin;

import ariss.nedologin.common.server.storage.StorageProvider_neoforge;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.zhivoymir.tweaks.auth.NedologinAuth;

/**
 * Пароль на платформу (выделенный сервер): в PlayerLoginHandler.login (главный поток) проверка
 * и регистрация пароля идут через NedologinAuth. Применяется, только если auth.NedologinMixinPlugin
 * нашёл в login оба вызова; иначе — предупреждение в логе и чистый Nedologin.
 */
@Mixin(targets = "ariss.nedologin.common.server.handler.PlayerLoginHandler_neoforge", remap = false)
public abstract class LoginHandlerMixin {
    @WrapOperation(method = "login", require = 0, at = @At(value = "INVOKE",
            target = "Lariss/nedologin/common/server/storage/StorageProvider_neoforge;checkPassword(Ljava/lang/String;Ljava/lang/String;)Z"))
    private boolean zmtweaks$platformCheck(StorageProvider_neoforge storage, String name, String password, Operation<Boolean> original) {
        return NedologinAuth.checkPassword(name, password, digest -> original.call(storage, name, digest));
    }

    @WrapOperation(method = "login", require = 0, at = @At(value = "INVOKE",
            target = "Lariss/nedologin/common/server/storage/StorageProvider_neoforge;register(Ljava/lang/String;Ljava/lang/String;)V"))
    private void zmtweaks$platformRegister(StorageProvider_neoforge storage, String name, String password, Operation<Void> original) {
        original.call(storage, name, NedologinAuth.onRegister(name, password));
    }
}
