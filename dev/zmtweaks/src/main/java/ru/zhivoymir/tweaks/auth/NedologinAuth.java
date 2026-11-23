package ru.zhivoymir.tweaks.auth;

import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import ru.zhivoymir.tweaks.ZmConfig;

/** Связка LoginHandlerMixin (выделенный сервер) с PlatformAuth: хранилище в папке мира, лог, сообщения опам. */
public final class NedologinAuth {
    public static final Logger LOG = LogUtils.getLogger();
    private static final String FILE = "zmtweaks-platform-passwords.json";
    private static PlatformStore store;

    private NedologinAuth() {}

    public static synchronized PlatformStore store(MinecraftServer server) {
        Path file = server.getWorldPath(LevelResource.ROOT).resolve(FILE);
        if (store == null || !store.file().equals(file)) store = new PlatformStore(file);
        return store;
    }

    public static synchronized void onServerStopped(ServerStoppedEvent event) {
        store = null;
    }

    /** Вместо StorageProvider.checkPassword в PlayerLoginHandler.login. */
    public static boolean checkPassword(String name, String sent, Predicate<String> mainCheck) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !ZmConfig.platformPasswords()) return mainCheck.test(PlatformAuth.strip(sent));
        PlatformAuth.Result result;
        try {
            result = PlatformAuth.check(store(server), name, sent, mainCheck);
        } catch (RuntimeException e) {
            LOG.error("[zmtweaks] Пароли платформ недоступны, проверяется только основной пароль Nedologin", e);
            return mainCheck.test(PlatformAuth.strip(sent));
        }
        String platform = PlatformAuth.platformOf(sent);
        if (result == PlatformAuth.Result.REGISTERED) {
            LOG.info("[zmtweaks] Игрок {} вошёл с новой платформы {}: её пароль зарегистрирован", name, platform);
            Component msg = Component.literal("[zmtweaks] Игрок " + name + " зарегистрировал пароль платформы " + platform
                    + " (сброс: /zmauth reset " + name + " " + platform + ")").withStyle(ChatFormatting.YELLOW);
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (server.getPlayerList().isOp(p.getGameProfile())) p.sendSystemMessage(msg);
            }
        } else if (result == PlatformAuth.Result.DENIED) {
            LOG.info("[zmtweaks] Игрок {}: неверный пароль (платформа {})", name, platform);
        }
        return result.allowed();
    }

    /** Вместо StorageProvider.register: Nedologin хранит чистый дайджест, мы — платформу основного пароля. */
    public static String onRegister(String name, String sent) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            try {
                store(server).registered(name, PlatformAuth.platformOf(sent));
            } catch (RuntimeException e) {
                LOG.error("[zmtweaks] Не записана платформа основного пароля {}", name, e);
            }
        }
        return PlatformAuth.strip(sent);
    }
}
