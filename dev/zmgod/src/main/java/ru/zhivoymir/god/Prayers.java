package ru.zhivoymir.god;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/** Молитвы (вслух, в чате, у алтаря) и подношения на алтарь. */
final class Prayers {
    static final TagKey<Block> ALTARS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(God.MOD_ID, "altars"));
    private static final Pattern PRAYER = Pattern.compile(
            "^\\s*(?:о\\s+)?(?:боже|боженька|господи|господь|создатель|отче|всевышний)(?:$|[\\s,!.:?…])",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final int ALTAR_RADIUS = 6;
    /** Вера за молитву и её дневной потолок (игровые сутки). */
    private static final int PRAYER_FAITH = 1, PRAYER_FAITH_DAILY = 3;
    private static final int OFFERING_FAITH_DAILY = 15;
    /** Подношения: предмет → вера. */
    private static final Map<net.minecraft.world.item.Item, Integer> OFFERINGS = Map.ofEntries(
            Map.entry(Items.BREAD, 1), Map.entry(Items.WHEAT, 1), Map.entry(Items.APPLE, 1), Map.entry(Items.CAKE, 2),
            Map.entry(Items.GOLDEN_CARROT, 1), Map.entry(Items.GOLD_INGOT, 2), Map.entry(Items.EMERALD, 3),
            Map.entry(Items.GOLDEN_APPLE, 4), Map.entry(Items.DIAMOND, 5), Map.entry(Items.GOLD_BLOCK, 10),
            Map.entry(Items.EMERALD_BLOCK, 15), Map.entry(Items.ENCHANTED_GOLDEN_APPLE, 15));

    private Prayers() {}

    static boolean isPrayer(String text) {
        return PRAYER.matcher(text).find();
    }

    static void onChat(ServerChatEvent e) {
        String text = e.getRawText();
        if (!isPrayer(text)) return;
        e.setCanceled(true);  // молитва — между игроком и богом, в общий чат не идёт
        ServerPlayer p = e.getPlayer();
        p.sendSystemMessage(Component.literal("Ты молишься: «" + text.trim() + "»").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        pray(p, text.trim(), "в чате", false);
    }

    /** Фраза, сказанная вслух (Living Voice). true — это молитва. */
    static boolean onVoice(ServerPlayer p, String text) {
        if (!isPrayer(text)) return false;
        p.displayClientMessage(Component.literal("Ты молишься: «" + text.trim() + "»").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
        pray(p, text.trim(), "вслух", false);
        return true;
    }

    /** force — для команды отладки: без кулдауна и без шанса промолчать. */
    static void pray(ServerPlayer p, String text, String how, boolean force) {
        MinecraftServer server = p.server;
        GodData data = GodData.get(server);
        UUID id = p.getUUID();
        long now = GodData.now(server);
        String name = Chronicle.name(p);
        if (!force && data.mark("pray|" + id) > now) {
            p.displayClientMessage(Component.literal("Небеса ещё не готовы слушать тебя снова").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
            return;
        }
        data.mark("pray|" + id, now + 20L * God.PRAYER_COOLDOWN.get());
        boolean altar = nearAltar(p);
        data.event(server, name + " молится (" + how + (altar ? ", у алтаря" : "") + "): «" + text + "»");
        if (daily(data, "pf|" + id, now, PRAYER_FAITH, PRAYER_FAITH_DAILY)) data.addFaith(id, PRAYER_FAITH);
        double chance = Math.max(0.25, Math.min(0.97, 0.55 + data.faith(id) / 200.0 + (altar ? 0.3 : 0)));
        if (!force && ThreadLocalRandom.current().nextDouble() > chance) {
            God.LOG.info("[zmgod] prayer of {} unanswered (chance {})", name, chance);
            Director.later(server, 60, () -> Director.silence(server, id));
            return;
        }
        Director.request(server, new Director.Trigger("молитва игрока " + name + (altar ? " у алтаря" : "") + " (" + how
                + "): «" + text + "». Ответь ему (to = " + name + ")", id, false, null));
    }

    static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getEntity() instanceof ServerPlayer p) || !(e.getLevel() instanceof ServerLevel level)) return;
        if (!level.getBlockState(e.getPos()).is(ALTARS)) return;
        ItemStack stack = e.getItemStack();
        Integer value = OFFERINGS.get(stack.getItem());
        if (value == null) return;
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
        GodData data = GodData.get(p.server);
        long now = GodData.now(p.server);
        String item = stack.getHoverName().getString();
        if (!p.getAbilities().instabuild) stack.shrink(1);
        BlockPos pos = e.getPos();
        level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 25, 0.25, 0.2, 0.25, 0.01);
        level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.7f, 1.3f);
        boolean counted = daily(data, "of|" + p.getUUID(), now, value, OFFERING_FAITH_DAILY);
        if (counted) data.addFaith(p.getUUID(), value);
        data.event(p.server, Chronicle.name(p) + " возложил на алтарь дар: " + item
                + (counted ? "" : " (сегодня даров уже довольно)"));
        p.displayClientMessage(Component.literal(counted ? "Дар принят" : "Дар принят, но небеса уже насыщены дарами сегодня")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC), true);
    }

    /** Дневной потолок (игровые сутки): можно ли добавить amount к счётчику key. */
    private static boolean daily(GodData data, String key, long now, int amount, int cap) {
        long day = now / 24000;
        long packed = data.mark(key);
        long sum = packed >> 32 == day ? packed & 0xffffffffL : 0;
        if (sum + amount > cap) return false;
        data.mark(key, (day << 32) | (sum + amount));
        return true;
    }

    static boolean nearAltar(ServerPlayer p) {
        ServerLevel level = p.serverLevel();
        BlockPos c = p.blockPosition();
        return BlockPos.betweenClosedStream(c.offset(-ALTAR_RADIUS, -3, -ALTAR_RADIUS), c.offset(ALTAR_RADIUS, 3, ALTAR_RADIUS))
                .anyMatch(pos -> level.isLoaded(pos) && level.getBlockState(pos).is(ALTARS));
    }
}
