package ru.zhivoymir.livingvoice;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import me.bytefox.livingcolonists.talk.Addressing;
import me.bytefox.livingcolonists.talk.Conversations;
import me.bytefox.livingcolonists.talk.JobNames;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Колонисты Living Colonists: разговор закрепляется за колонистом, на которого смотрит игрок. Загружается только при LC. */
public final class ColonistBridge {
    /** Экземпляр разговоров LC (его ловит ConversationsMixin при создании). */
    public static volatile Conversations conversations;

    private ColonistBridge() {
    }

    static boolean isColonist(Entity e) {
        return e instanceof AbstractEntityCitizen c && c.getCitizenData() != null;
    }

    static String name(Entity e) {
        return ((AbstractEntityCitizen) e).getCitizenData().getName();
    }

    /** Названия профессии колониста (en, ru и язык игрока) и разговорные синонимы — из LC. */
    static Set<String> jobNames(Entity e, String locale) {
        return new JobNames().of(((AbstractEntityCitizen) e).getCitizenData(), locale);
    }

    /** Сопоставление обращений LC: падежи («Ивану»), латиница/кириллица («John» — «Джон»). */
    static List<Integer> mentioned(String text, List<String> names) {
        return Addressing.allMentioned(text, names);
    }

    static List<Integer> byJob(String text, List<? extends Collection<String>> jobs) {
        return Addressing.byJob(text, jobs);
    }

    static void say(ServerPlayer player, Entity target, String text) {
        Conversations conv = conversations;
        if (conv == null) {
            player.displayClientMessage(Component.literal("Разговоры с колонистами недоступны").withStyle(ChatFormatting.GRAY), true);
            return;
        }
        // lock — как клавиша разговора LC при взгляде на колониста; onChat — как реплика в чате
        conv.lock(player, ((AbstractEntityCitizen) target).getCitizenData(), false);
        if (conv.wantsChat(player)) conv.onChat(player, text);
    }
}
