package ru.zhivoymir.client;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.resources.language.I18n;

/**
 * Русские названия стилей, разделов и чертежей Structurize/MineColonies. Окна строителя выводят их
 * через Component.literal мимо языковых файлов; миксины подменяют только текст надписи.
 * Словарь — kubejs/assets/zmclient/lang/ru_ru.json (tools/bp-names.py); без ключа остаётся оригинал.
 * Нормализация ключа совпадает с bp-names.py: key().
 */
public final class BlueprintNames {
    private static final Pattern LEVEL = Pattern.compile("^(.*?)(\\d+)$");

    private BlueprintNames() {}

    static String key(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "_").replaceAll("^_+|_+$", "");
    }

    private static String tr(String key) {
        return I18n.exists(key) ? I18n.get(key) : null;
    }

    /** Название стиля (StructurePackMeta.getName()). */
    public static String pack(String name) {
        if (name == null) return null;
        String t = tr("zmclient.bp.pack." + key(name));
        return t != null ? t : name;
    }

    /** Описание стиля по его названию. */
    public static String desc(String packName, String desc) {
        if (packName == null) return desc;
        String t = tr("zmclient.bp.desc." + key(packName));
        return t != null ? t : desc;
    }

    /** Раздел (папка); в окне — с заглавной буквы. */
    public static String folder(String name) {
        if (name == null) return null;
        if (name.equals("back")) {
            String t = tr("zmclient.bp.back");
            return t != null ? t : name;
        }
        String t = tr("zmclient.bp.folder." + key(name));
        return t != null ? t : name;
    }

    /** Чертёж: имя файла; цифры уровня в конце сохраняются («Лестница 3»). */
    public static String name(String name) {
        if (name == null) return null;
        if (name.equals("back")) return folder(name);
        Matcher m = LEVEL.matcher(name);
        String base = m.matches() ? m.group(1) : name;
        String t = tr("zmclient.bp.name." + key(base));
        if (t == null) return name;
        return m.matches() ? t + " " + m.group(2) : t;
    }

    /** «Level: N» на кнопках уровней. */
    public static String level(String s) {
        if (s == null || !s.startsWith("Level: ")) return s;
        return I18n.exists("zmclient.bp.level") ? I18n.get("zmclient.bp.level", s.substring(7)) : s;
    }

    /** Строка «стиль/раздел/…/чертёж» над списками инструмента. */
    public static String tree(String s) {
        if (s == null) return null;
        String[] seg = s.split("/", -1);
        seg[0] = pack(seg[0]);
        for (int i = 1; i < seg.length; i++) seg[i] = segment(seg[i]);
        return String.join("/", seg);
    }

    /** Путь чертежа без стиля; sep — разделитель («/» или перевод строки). */
    public static String path(String s, String sep) {
        if (s == null) return null;
        String[] seg = s.split(Pattern.quote(sep), -1);
        for (int i = 0; i < seg.length; i++) seg[i] = segment(seg[i]);
        return String.join(sep, seg);
    }

    private static String segment(String seg) {
        if (seg.isEmpty()) return seg;
        String t = tr("zmclient.bp.folder." + key(seg));
        return t != null ? t : name(seg);
    }
}
