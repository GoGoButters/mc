package ru.zhivoymir.livingvoice;

import net.conczin.mca.Config;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.ai.chatAI.ChatAI;
import net.conczin.mca.server.world.data.PlayerSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;

import java.util.concurrent.CompletableFuture;

/** Жители MCA: фраза идёт в их чат на ИИ, как если бы игрок написал её в чат рядом с жителем. Загружается только при MCA. */
final class McaBridge {
    private McaBridge() {
    }

    static boolean isVillager(Entity e) {
        return e instanceof VillagerEntityMCA;
    }

    /** Профессии MCA и ванильные: как жителя могут позвать по-русски и по-английски. */
    private static final Map<String, List<String>> JOBS = Map.ofEntries(
            Map.entry("farmer", List.of("farmer", "фермер", "фермерша", "крестьянин", "крестьянка", "земледелец")),
            Map.entry("fisherman", List.of("fisherman", "рыбак", "рыбачка", "рыболов")),
            Map.entry("shepherd", List.of("shepherd", "пастух", "пастушка", "овчар")),
            Map.entry("fletcher", List.of("fletcher", "лучник", "стрельник", "мастер стрел")),
            Map.entry("librarian", List.of("librarian", "библиотекарь", "библиотекарша", "книжник")),
            Map.entry("cartographer", List.of("cartographer", "картограф")),
            Map.entry("cleric", List.of("cleric", "priest", "священник", "жрец", "жрица", "монах", "батюшка")),
            Map.entry("armorer", List.of("armorer", "бронник", "бронница", "кузнец", "оружейник")),
            Map.entry("weaponsmith", List.of("weaponsmith", "оружейник", "оружейница", "кузнец")),
            Map.entry("toolsmith", List.of("toolsmith", "инструментальщик", "кузнец")),
            Map.entry("butcher", List.of("butcher", "мясник", "мясница")),
            Map.entry("leatherworker", List.of("leatherworker", "кожевник", "кожевница", "скорняк")),
            Map.entry("mason", List.of("mason", "каменщик", "каменщица", "камнетёс", "камнетес")),
            Map.entry("nitwit", List.of("nitwit", "бездельник", "бездельница", "дурачок")),
            Map.entry("outlaw", List.of("outlaw", "разбойник", "разбойница", "бандит")),
            Map.entry("miner", List.of("miner", "шахтёр", "шахтер", "горняк")),
            Map.entry("baker", List.of("baker", "пекарь", "булочник", "булочница")),
            Map.entry("guard", List.of("guard", "стражник", "стражница", "страж", "охранник", "стража")),
            Map.entry("warrior", List.of("warrior", "воин", "воительница", "боец")),
            Map.entry("archer", List.of("archer", "лучник", "лучница", "стрелок")),
            Map.entry("adventurer", List.of("adventurer", "путешественник", "путешественница", "странник", "искатель")),
            Map.entry("mercenary", List.of("mercenary", "наёмник", "наемник", "наёмница")),
            Map.entry("cultist", List.of("cultist", "культист", "сектант", "сектантка")));

    static String name(ServerPlayer player, Entity e) {
        VillagerEntityMCA v = (VillagerEntityMCA) e;
        String nick = v.getNickname(player.getUUID());
        return nick == null || nick.isEmpty() ? v.getName().getString() : nick;
    }

    static List<String> jobNames(Entity e) {
        String path = BuiltInRegistries.VILLAGER_PROFESSION.getKey(((VillagerEntityMCA) e).getVillagerData().getProfession()).getPath();
        return JOBS.getOrDefault(path, List.of(path));
    }

    static void say(ServerPlayer player, Entity target, String text) {
        VillagerEntityMCA villager = (VillagerEntityMCA) target;
        if (!Config.getInstance().enableVillagerChatAI) {
            player.displayClientMessage(Component.literal("Разговоры с жителями MCA выключены на сервере").withStyle(ChatFormatting.GRAY), true);
            return;
        }
        player.sendSystemMessage(Component.literal("<").append(player.getDisplayName()).append(" → ")
                .append(villager.getName()).append("> " + text).withStyle(ChatFormatting.GRAY));
        // данные игрока MCA создаёт со случайными значениями — только в серверном потоке (иначе C2ME падает)
        PlayerSaveData.get(player);
        // как в MCA (MixinServerGamePacketListenerImpl): ответ ИИ — асинхронно, затем реплика жителя
        long t0 = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> ChatAI.answer(player, villager, text).ifPresent(a -> {
            LivingVoice.LOG.info("[livingvoice] {} answered in {} ms: {}", villager.getName().getString(), System.currentTimeMillis() - t0, a);
            villager.conversationManager.addMessage(player, Component.literal(a));
        })).exceptionally(err -> {
            LivingVoice.LOG.warn("[livingvoice] MCA answer failed", err);
            return null;
        });
    }
}
