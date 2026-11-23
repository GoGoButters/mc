package ru.zhivoymir.tweaks.auth;

import java.util.function.Predicate;

/**
 * Пароль на платформу поверх Nedologin. Клиент Nedologin шлёт не пароль, а его двойной SHA-256,
 * поэтому клиентская часть zmtweaks помечает дайджест пароля «zm-android:…» тем же префиксом
 * (LoginPayloadMixin), а здесь метка снимается: дальше везде идёт чистый дайджест, как у Nedologin.
 */
public final class PlatformAuth {
    public static final String PC = "pc";
    public static final String ANDROID = "android";
    public static final String ANDROID_PREFIX = "zm-android:";

    public enum Result {
        /** Совпал основной пароль Nedologin. */
        MAIN,
        /** Совпал сохранённый пароль платформы. */
        PLATFORM,
        /** Первый вход с новой платформы: пароль платформы зарегистрирован. */
        REGISTERED,
        DENIED;

        public boolean allowed() {
            return this != DENIED;
        }
    }

    private PlatformAuth() {}

    public static String platformOf(String sent) {
        return sent != null && sent.startsWith(ANDROID_PREFIX) ? ANDROID : PC;
    }

    public static String strip(String sent) {
        return sent != null && sent.startsWith(ANDROID_PREFIX) ? sent.substring(ANDROID_PREFIX.length()) : sent;
    }

    /**
     * Проверка пароля зарегистрированного ника. mainCheck — проверка основного пароля Nedologin
     * (получает дайджест без метки).
     */
    public static Result check(PlatformStore store, String name, String sent, Predicate<String> mainCheck) {
        String platform = platformOf(sent);
        String digest = strip(sent);
        if (mainCheck.test(digest)) return Result.MAIN;
        String stored = store.hash(name, platform);
        if (stored != null) return PasswordHash.verify(digest, stored) ? Result.PLATFORM : Result.DENIED;
        if (!platform.equals(store.mainPlatform(name))) {
            store.putHash(name, platform, PasswordHash.hash(digest));
            return Result.REGISTERED;
        }
        return Result.DENIED;
    }
}
