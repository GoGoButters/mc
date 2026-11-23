package ru.zhivoymir.tweaks.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Пароли платформ: JSON {ник в нижнем регистре → {main: платформа основного пароля Nedologin,
 * hashes: {платформа → PBKDF2}}}. Нет записи — основной пароль ПК. Все методы синхронизированы,
 * файл пишется через временный и атомарную замену.
 */
public final class PlatformStore {
    public static final class Entry {
        public String main = PlatformAuth.PC;
        public Map<String, String> hashes = new TreeMap<>();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;
    private final Map<String, Entry> entries;

    public PlatformStore(Path file) {
        this.file = file;
        this.entries = load(file);
    }

    public Path file() {
        return file;
    }

    public static String key(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public synchronized String mainPlatform(String name) {
        Entry e = entries.get(key(name));
        return e == null || e.main == null ? PlatformAuth.PC : e.main;
    }

    public synchronized String hash(String name, String platform) {
        Entry e = entries.get(key(name));
        return e == null ? null : e.hashes.get(platform);
    }

    public synchronized Map<String, String> hashes(String name) {
        Entry e = entries.get(key(name));
        return e == null ? Map.of() : Map.copyOf(e.hashes);
    }

    /** Nedologin зарегистрировал ник заново: старые пароли платформ недействительны. */
    public synchronized void registered(String name, String platform) {
        Entry e = new Entry();
        e.main = platform;
        entries.put(key(name), e);
        save();
    }

    public synchronized void putHash(String name, String platform, String hash) {
        entries.computeIfAbsent(key(name), k -> new Entry()).hashes.put(platform, hash);
        save();
    }

    /** Снять пароли платформ (null — все); основной пароль Nedologin не трогается. Возвращает, что снято. */
    public synchronized int reset(String name, String platform) {
        Entry e = entries.get(key(name));
        if (e == null) return 0;
        int before = e.hashes.size();
        if (platform == null) e.hashes.clear();
        else e.hashes.remove(platform);
        int removed = before - e.hashes.size();
        if (removed > 0) save();
        return removed;
    }

    private static Map<String, Entry> load(Path file) {
        if (!Files.exists(file)) return new TreeMap<>();
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<String, Entry> m = GSON.fromJson(r, new TypeToken<TreeMap<String, Entry>>() {}.getType());
            if (m == null) return new TreeMap<>();
            m.values().forEach(e -> {
                if (e.main == null) e.main = PlatformAuth.PC;
                if (e.hashes == null) e.hashes = new TreeMap<>();
            });
            return m;
        } catch (IOException e) {
            throw new UncheckedIOException("Не читается " + file, e);
        }
    }

    private void save() {
        try {
            Path dir = file.toAbsolutePath().getParent();
            Files.createDirectories(dir);
            Path tmp = Files.createTempFile(dir, file.getFileName().toString(), ".tmp");
            try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(entries, w);
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Не записывается " + file, e);
        }
    }
}
