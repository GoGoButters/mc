package ru.zhivoymir.livingvoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Запасное сопоставление имён по звучанию, когда английское имя произнесено по-русски:
 * Jalynn — «Джейлин», Kate — «Кейт», Hunter — «Хантер». Сравниваются латинские «звуки» с допуском.
 */
final class Names {
    private static final Map<Character, String> LAT = Map.ofEntries(
            Map.entry('а', "a"), Map.entry('б', "b"), Map.entry('в', "v"), Map.entry('г', "g"), Map.entry('д', "d"),
            Map.entry('е', "e"), Map.entry('ё', "o"), Map.entry('ж', "j"), Map.entry('з', "z"), Map.entry('и', "i"),
            Map.entry('й', "i"), Map.entry('к', "k"), Map.entry('л', "l"), Map.entry('м', "m"), Map.entry('н', "n"),
            Map.entry('о', "o"), Map.entry('п', "p"), Map.entry('р', "r"), Map.entry('с', "s"), Map.entry('т', "t"),
            Map.entry('у', "u"), Map.entry('ф', "f"), Map.entry('х', "h"), Map.entry('ц', "ts"), Map.entry('ч', "ch"),
            Map.entry('ш', "sh"), Map.entry('щ', "sh"), Map.entry('ъ', ""), Map.entry('ы', "i"), Map.entry('ь', ""),
            Map.entry('э', "e"), Map.entry('ю', "iu"), Map.entry('я', "ia"));

    private Names() {
    }

    /** Звучание русского слова латиницей. */
    static String ru(String word) {
        StringBuilder b = new StringBuilder();
        for (char c : word.toLowerCase(Locale.ROOT).toCharArray()) b.append(LAT.getOrDefault(c, String.valueOf(c)));
        return norm(b.toString().replace("dj", "j").replace("dzh", "j"));
    }

    /** Звучание английского имени, как его скажет русский игрок. */
    static String en(String name) {
        String s = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        s = s.replaceAll("a(?=[^aeiou]e$)", "ei")       // Kate -> keit
                .replaceAll("(?<=[^aeiou])e$", "")         // немая e на конце
                .replaceAll("^y(?=[aeiou])", "i")
                .replace("au", "o").replace("aw", "o")          // Aubrie -> obri, Sawyer -> soier
                .replaceAll("u(?=[^aeiouy]{2})", "a")         // Hunter -> hanter
                .replace("ay", "ei").replace("ai", "ei").replace("ey", "ei")
                .replace("ee", "i").replace("ea", "i").replace("oo", "u").replace("ou", "au")
                .replace("ph", "f").replace("th", "t").replace("ck", "k").replace("qu", "kv")
                .replace("sh", "\u0001").replace("ch", "\u0002")
                .replaceAll("c(?=[eiy])", "s").replace('c', 'k').replace("x", "ks").replace('w', 'u')
                .replace("\u0001", "sh").replace("\u0002", "ch");
        return norm(s.replace('y', 'i'));
    }

    private static String norm(String s) {
        return s.replace("h", "").replaceAll("(.)\\1+", "$1");
    }

    /** Индексы самых похожих имён (слова от 4 букв, первая буква совпадает, расстояние до 1–2). */
    static List<Integer> fuzzy(String text, List<String> names) {
        List<Integer> found = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        String[] words = text.toLowerCase(Locale.ROOT).split("[^\\p{L}]+");
        for (int i = 0; i < names.size(); i++) {
            int score = Integer.MAX_VALUE;
            for (String part : names.get(i).split("\\s+")) {
                if (!part.matches("[A-Za-z]{3,}")) continue;
                String target = en(part);
                for (String w : words) {
                    if (w.length() < 4 || !w.matches("\\p{IsCyrillic}+")) continue;
                    for (int cut = 0; cut <= 2 && cut < w.length() - 2; cut++) {  // падежные окончания: «Хантеру»
                        String sound = ru(w.substring(0, w.length() - cut));
                        if (sound.isEmpty() || sound.charAt(0) != target.charAt(0)) continue;
                        int d = distance(sound, target) + cut;
                        if (d - cut <= (target.length() >= 5 ? 2 : 1)) score = Math.min(score, d);
                    }
                }
            }
            if (score < best) {
                best = score;
                found.clear();
            }
            if (score == best && score != Integer.MAX_VALUE) found.add(i);
        }
        return found;
    }

    private static int distance(String a, String b) {
        int[] row = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) row[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int diag = row[0];
            row[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int above = row[j];
                row[j] = Math.min(Math.min(row[j] + 1, row[j - 1] + 1), diag + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
                diag = above;
            }
        }
        return row[b.length()];
    }
}
