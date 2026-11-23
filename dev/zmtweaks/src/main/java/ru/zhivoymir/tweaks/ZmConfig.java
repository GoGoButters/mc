package ru.zhivoymir.tweaks;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Серверный конфиг: config/zmtweaks-server.toml (NeoForge 21.1 кладёт серверные конфиги в общий config/). */
public final class ZmConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue PLATFORM_PASSWORDS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        PLATFORM_PASSWORDS = b
                .comment("Свой пароль Nedologin на каждую платформу (ПК, Android): первый вход с новой платформы регистрирует её пароль.",
                        "Выключено — только основной пароль Nedologin.")
                .define("auth.platformPasswords", true);
        SPEC = b.build();
    }

    private ZmConfig() {}

    public static boolean platformPasswords() {
        return !SPEC.isLoaded() || PLATFORM_PASSWORDS.getAsBoolean();
    }
}
