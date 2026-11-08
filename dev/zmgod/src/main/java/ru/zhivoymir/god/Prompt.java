package ru.zhivoymir.god;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;
import java.util.UUID;

/** Характер бога (системный промпт) и сводка мира для очередного решения. */
final class Prompt {
    private Prompt() {}

    static final String SYSTEM = """
            Ты — бог мира «Живой мир» (Minecraft): древний, немногословный, справедливый и немного загадочный.
            Ты любишь живой мир: деревни, колонии, их жителей и тех, кто трудится и бережёт других.
            Ты не слуга игроков: не исполняешь каждую просьбу, иногда отвечаешь загадкой, знамением или молчанием.
            Ты мягок: наказываешь редко и только за дурные дела (убийство жителей, жестокость). Сначала предупреждаешь
            словом или знамением, потом насылаешь проклятие, а нашествие — только на упорствующего грешника.
            Награждаешь за веру, подношения, труд, рост колоний, победы над злом.
            Говоришь по-русски, торжественно и коротко: одно-два предложения, не больше 30 слов, обращаешься на «ты».
            Не называй игрока по нику (его читает голос) — обращайся «смертный», «дитя моё» или без обращения. Без смайликов и разметки. Не называй механику игры: эффекты, числа, предметы по ID, «веру» как число, «кулдаун».
            При плановом обходе чаще всего ничего не делай или ограничься знамением; вмешивайся, когда в событиях есть повод,
            и не повторяй свои недавние вмешательства. На молитву отвечай по сути просьбы; можно отказать или промолчать.

            Ответ — только один JSON-объект, без пояснений вокруг:
            {"thought":"зачем так решил, одна фраза для летописи",
             "speech":"твои слова или пустая строка",
             "to":"имя игрока, которому говоришь, или all",
             "actions":[не больше двух действий],
             "faith":{"имя игрока": изменение веры от -10 до 10},
             "note":{"имя игрока":"что запомнить о нём, до 200 символов"}}
            Поля faith и note необязательны. Действия:
            - {"type":"weather","kind":"clear|rain|thunder","minutes":5-30}
            - {"type":"omen","target":"имя","kind":"lightning|thunder|bell|stars|cave"} — знамение рядом с игроком, без вреда
            - {"type":"bless","target":"имя","effect":"regeneration|speed|haste|luck|resistance|saturation|night_vision|fire_resistance|absorption|water_breathing|hero","minutes":1-10,"level":1-2}
            - {"type":"heal","target":"имя"} — исцеление и насыщение
            - {"type":"gift","target":"имя","item":"предмет","count":число} — предметы и наибольшее количество: %s
            - {"type":"curse","target":"имя","effect":"slowness|weakness|mining_fatigue|hunger|unluck|darkness","minutes":1-5} — только грешнику
            - {"type":"invasion","target":"имя","mob":"zombie|skeleton|spider|husk|pillager","count":2-5} — только упорствующему грешнику
            Действия, помеченные в сводке как недоступные, не выбирай.
            """.formatted(Actions.giftList());

    static String summary(MinecraftServer server, Director.Trigger t) {
        GodData data = GodData.get(server);
        ServerLevel overworld = server.overworld();
        long now = GodData.now(server);
        StringBuilder sb = new StringBuilder();
        long dayTime = overworld.getDayTime() % 24000;
        sb.append("Сейчас: день ").append(overworld.getDayTime() / 24000 + 1).append(", ").append(partOfDay(dayTime))
                .append(", погода: ").append(overworld.isThundering() ? "гроза" : overworld.isRaining() ? "дождь" : "ясно").append(".\n");
        sb.append("Повод: ").append(t.reason()).append(".\n");

        sb.append("\nИгроки на сервере:\n");
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            UUID id = p.getUUID();
            int f = data.faith(id);
            sb.append("- ").append(Chronicle.name(p)).append(": вера ").append(f).append(" (").append(faithWord(f)).append(")")
                    .append(", здоровье ").append(Math.round(p.getHealth())).append("/").append(Math.round(p.getMaxHealth()))
                    .append(", сытость ").append(p.getFoodData().getFoodLevel()).append("/20")
                    .append(", мир ").append(p.level().dimension().location().getPath())
                    .append(", высота ").append(p.getBlockY());
            if (p.level().canSeeSky(p.blockPosition())) sb.append(", под открытым небом");
            if (Prayers.nearAltar(p)) sb.append(", у алтаря");
            String where = colonyNear(p);
            if (where != null) sb.append(", ").append(where);
            String note = data.notes.get(id);
            if (note != null) sb.append("\n  твоя заметка о нём: ").append(note);
            String blocked = Actions.unavailable(server, p);
            if (!blocked.isEmpty()) sb.append("\n  недоступно сейчас: ").append(blocked);
            sb.append("\n");
        }
        if (data.mark("weather") > now) sb.append("Погоду менять сейчас нельзя.\n");

        var colonies = IMinecoloniesAPI.getInstance().getColonyManager().getAllColonies();
        if (!colonies.isEmpty()) {
            sb.append("\nКолонии:\n");
            for (IColony c : colonies) {
                sb.append("- «").append(c.getName()).append("», владелец ").append(c.getPermissions().getOwnerName())
                        .append(": ").append(c.getCitizenManager().getCurrentCitizenCount()).append(" колонистов, счастье ")
                        .append(String.format(Locale.ROOT, "%.1f", c.getOverallHappiness())).append("/10");
                if (c.getRaiderManager().isRaided()) sb.append(", сейчас на неё нападают");
                sb.append("\n");
            }
        }

        sb.append("\nСобытия мира (новые внизу):\n");
        if (data.events.isEmpty()) sb.append("- ничего примечательного\n");
        for (GodData.Entry e : data.events) sb.append("- ").append(ago(now - e.time())).append(": ").append(e.text()).append("\n");

        sb.append("\nТвои недавние вмешательства (новые внизу):\n");
        if (data.history.isEmpty()) sb.append("- пока не было\n");
        for (GodData.Entry e : data.history) sb.append("- ").append(ago(now - e.time())).append(": ").append(e.text()).append("\n");
        return sb.toString();
    }

    private static String colonyNear(ServerPlayer p) {
        IColony c = IMinecoloniesAPI.getInstance().getColonyManager().getClosestIColony(p.level(), p.blockPosition());
        if (c == null) return null;
        double d = Math.sqrt(c.getCenter().distSqr(p.blockPosition()));
        if (d > 160) return null;
        boolean own = p.getUUID().equals(c.getPermissions().getOwner());
        return (d < 64 ? "в колонии «" : "возле колонии «") + c.getName() + "»" + (own ? " (своей)" : "");
    }

    static String faithWord(int f) {
        if (f <= -30) return "отступник";
        if (f < 0) return "грешит";
        if (f < 10) return "равнодушен";
        if (f < 40) return "верит";
        return "истово верует";
    }

    private static String partOfDay(long t) {
        if (t < 1000) return "рассвет";
        if (t < 6000) return "утро";
        if (t < 11000) return "день";
        if (t < 13000) return "закат";
        if (t < 23000) return "ночь";
        return "рассвет";
    }

    /** Тики (20 в секунду) — в «N мин назад». */
    static String ago(long ticks) {
        long min = ticks / 1200;
        if (min < 1) return "только что";
        if (min < 60) return min + " мин назад";
        long h = min / 60;
        return h < 48 ? h + " ч назад" : h / 24 + " дн назад";
    }

    static String minutes(long ticks) {
        return Math.max(1, (ticks + 1199) / 1200) + " мин";
    }
}
