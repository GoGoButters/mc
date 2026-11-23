package ru.zhivoymir.tweaks.auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Проверка логики паролей платформ без JUnit (в моде нет тестовой инфраструктуры). Запуск:
 * java -cp build/classes/java/main:build/classes/java/test:gson.jar ru.zhivoymir.tweaks.auth.PlatformAuthCheck
 */
public final class PlatformAuthCheck {
    private static int failed;

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("zmauth");
        Path file = dir.resolve("store.json");
        PlatformStore store = new PlatformStore(file);
        Predicate<String> main = "pc-main"::equals;  // основной пароль Nedologin (дайджест)
        String android = PlatformAuth.ANDROID_PREFIX + "dg7f3a";

        eq("platformOf android", PlatformAuth.ANDROID, PlatformAuth.platformOf(android));
        eq("platformOf pc", PlatformAuth.PC, PlatformAuth.platformOf("pc-main"));
        eq("strip", "dg7f3a", PlatformAuth.strip(android));
        eq("существующая регистрация = pc", PlatformAuth.PC, store.mainPlatform("Vadim"));

        eq("pc основной", PlatformAuth.Result.MAIN, PlatformAuth.check(store, "Vadim", "pc-main", main));
        eq("pc неверный — отказ", PlatformAuth.Result.DENIED, PlatformAuth.check(store, "Vadim", "typo", main));
        eq("android впервые — регистрация", PlatformAuth.Result.REGISTERED, PlatformAuth.check(store, "Vadim", android, main));
        eq("android снова", PlatformAuth.Result.PLATFORM, PlatformAuth.check(store, "vadim", android, main));
        eq("android чужой — отказ", PlatformAuth.Result.DENIED, PlatformAuth.check(store, "VADIM", PlatformAuth.ANDROID_PREFIX + "x", main));
        eq("pc после android", PlatformAuth.Result.MAIN, PlatformAuth.check(store, "Vadim", "pc-main", main));
        eq("pc неверный после android", PlatformAuth.Result.DENIED, PlatformAuth.check(store, "Vadim", "typo", main));

        String json = Files.readString(file);
        eq("в файле нет дайджеста", false, json.contains("dg7f3a"));
        PlatformStore reloaded = new PlatformStore(file);
        eq("перечитан с диска", PlatformAuth.Result.PLATFORM, PlatformAuth.check(reloaded, "Vadim", android, main));
        eq("нет временных файлов", 1L, Files.list(dir).count());

        eq("reset pc — нечего", 0, store.reset("Vadim", PlatformAuth.PC));
        eq("reset android", 1, store.reset("Vadim", PlatformAuth.ANDROID));
        eq("android после сброса — регистрация", PlatformAuth.Result.REGISTERED,
                PlatformAuth.check(store, "Vadim", PlatformAuth.ANDROID_PREFIX + "new", main));

        // первым был Android: основной пароль Nedologin — android, ПК регистрируется при первом входе
        store.registered("Phone", PlatformAuth.ANDROID);
        Predicate<String> phoneMain = "dg7f3a"::equals;
        eq("android основной", PlatformAuth.Result.MAIN, PlatformAuth.check(store, "Phone", android, phoneMain));
        eq("android неверный — отказ", PlatformAuth.Result.DENIED, PlatformAuth.check(store, "Phone", PlatformAuth.ANDROID_PREFIX + "x", phoneMain));
        eq("pc впервые — регистрация", PlatformAuth.Result.REGISTERED, PlatformAuth.check(store, "Phone", "pc-uuid", phoneMain));
        eq("pc снова", PlatformAuth.Result.PLATFORM, PlatformAuth.check(store, "Phone", "pc-uuid", phoneMain));
        eq("pc чужой — отказ", PlatformAuth.Result.DENIED, PlatformAuth.check(store, "Phone", "pc-other", phoneMain));
        store.registered("Phone", PlatformAuth.PC);
        eq("перерегистрация снимает пароли платформ", 0, store.hashes("Phone").size());

        String h = PasswordHash.hash("secret");
        eq("hash verify", true, PasswordHash.verify("secret", h));
        eq("hash wrong", false, PasswordHash.verify("secret2", h));
        eq("hash соль", false, h.equals(PasswordHash.hash("secret")));
        eq("hash мусор", false, PasswordHash.verify("secret", "bcrypt$1$x"));

        Files.walk(dir).sorted(java.util.Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        System.out.println(failed == 0 ? "OK" : "FAILED: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static void eq(String what, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            failed++;
            System.out.println("FAIL " + what + ": ожидалось " + expected + ", получено " + actual);
        }
    }
}
