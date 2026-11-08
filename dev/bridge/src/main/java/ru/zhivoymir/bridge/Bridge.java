package ru.zhivoymir.bridge;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

/**
 * Мост поселений: связывает деревни MCA и колонии MineColonies.
 * Предупреждает о колонии у самой деревни, переселяет доверяющих жителей в колонию, ведёт общую репутацию
 * (стражи колонии защищают деревню, воры колонии её грабят). Заодно сажает саженец на месте срубленного дерева.
 */
@Mod(Bridge.MOD_ID)
public final class Bridge {
    public static final String MOD_ID = "zmbridge";
    public static final Logger LOG = LogUtils.getLogger();

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue WARN_DISTANCE;
    public static final ModConfigSpec.IntValue INVITE_MIN_REPUTATION;
    public static final ModConfigSpec.IntValue INVITE_MIN_HEARTS;
    public static final ModConfigSpec.IntValue VILLAGE_MIN_RESIDENTS;
    public static final ModConfigSpec.DoubleValue VILLAGE_KEEP_FRACTION;
    public static final ModConfigSpec.DoubleValue INVITE_COOLDOWN_DAYS;
    public static final ModConfigSpec.IntValue GUARD_KILL_REPUTATION;
    public static final ModConfigSpec.IntValue GUARD_KILL_DAILY_CAP;
    public static final ModConfigSpec.IntValue GUARD_KILL_MARGIN;
    public static final ModConfigSpec.IntValue THEFT_PENALTY;
    public static final ModConfigSpec.BooleanValue REPLANT;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        WARN_DISTANCE = b.comment("Warn when a town hall / supply camp / supply ship is closer than this (blocks) to an MCA village border.")
                .defineInRange("warnDistance", 20, 0, 256);
        INVITE_MIN_REPUTATION = b.comment("Relocation: minimal player reputation in the village (sum of hearts of all residents; MCA merchant rank = 400).")
                .defineInRange("inviteMinReputation", 300, 0, 100000);
        INVITE_MIN_HEARTS = b.comment("Relocation: minimal hearts of the invited villager towards the player.")
                .defineInRange("inviteMinHearts", 40, 0, 1000);
        VILLAGE_MIN_RESIDENTS = b.comment("Relocation: the village always keeps at least this many residents.")
                .defineInRange("villageMinResidents", 6, 1, 1000);
        VILLAGE_KEEP_FRACTION = b.comment("Relocation: the village always keeps at least this share of its beds occupied.")
                .defineInRange("villageKeepFraction", 0.6, 0.0, 1.0);
        INVITE_COOLDOWN_DAYS = b.comment("Relocation: at most one villager leaves a village per this many in-game days.")
                .defineInRange("inviteCooldownDays", 2.0, 0.0, 100.0);
        GUARD_KILL_REPUTATION = b.comment("Reputation (hearts in total) the colony owner gets in a village when a colonist kills a monster at that village.")
                .defineInRange("guardKillReputation", 4, 0, 1000);
        GUARD_KILL_DAILY_CAP = b.comment("Daily cap of reputation from colonist kills, per village and player.")
                .defineInRange("guardKillDailyCap", 40, 0, 100000);
        GUARD_KILL_MARGIN = b.comment("How far (blocks) outside the village border a kill still counts as defending the village.")
                .defineInRange("guardKillMargin", 32, 0, 256);
        THEFT_PENALTY = b.comment("Reputation lost by the colony owner when a Colonist Thieves heist on a village is spotted (doubled when the thief is caught).")
                .defineInRange("theftPenalty", 30, 0, 100000);
        REPLANT = b.comment("When a player fells a natural tree from its base (FallingTree), plant a sapling of the same kind in the stump's place (taken from the drops, else from the player's inventory).")
                .define("replantSaplings", true);
        SPEC = b.build();
    }

    public Bridge(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> BridgeCommands.register(e.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post e) -> {
            ProximityWarning.onPlayerTick(e);
            Reputation.onPlayerTick(e);
        });
        NeoForge.EVENT_BUS.addListener(Relocation::onEntityInteract);
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent e) -> Reputation.onDeath(e));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (BlockEvent.BreakEvent e) -> Replant.onBreak(e));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e) -> Replant.onTick(e));
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> {
            if (subscribed) return;
            subscribed = true;
            IMinecoloniesAPI.getInstance().getEventBus().subscribe(ColonyCreatedModEvent.class, ProximityWarning::onColonyCreated);
        });
    }

    /** Подписка на шину MineColonies — один раз за запуск процесса (шина живёт дольше сервера). */
    static boolean subscribed;
}
