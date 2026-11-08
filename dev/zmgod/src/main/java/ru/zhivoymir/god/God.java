package ru.zhivoymir.god;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import com.minecolonies.api.eventbus.events.colony.buildings.BuildingConstructionModEvent;
import com.minecolonies.api.eventbus.events.colony.citizens.CitizenDiedModEvent;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

/**
 * ИИ-бог «Живого мира»: ведёт летопись событий и веру игроков, слышит молитвы и время от времени вмешивается.
 * Решение принимает LLM (через брокер), исполняются только действия из белого списка {@link Actions}.
 */
@Mod(God.MOD_ID)
public final class God {
    public static final String MOD_ID = "zmgod";
    public static final Logger LOG = LogUtils.getLogger();
    static final boolean LIVING_VOICE = ModList.get().isLoaded("livingvoice");

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue DRY_RUN;
    public static final ModConfigSpec.ConfigValue<String> BROKER_URL;
    public static final ModConfigSpec.ConfigValue<String> API_KEY;
    public static final ModConfigSpec.ConfigValue<String> MODEL;
    public static final ModConfigSpec.ConfigValue<String> PRAYER_MODEL;
    public static final ModConfigSpec.IntValue INTERVAL_MIN;
    public static final ModConfigSpec.IntValue INTERVAL_MAX;
    public static final ModConfigSpec.IntValue PRAYER_COOLDOWN;
    public static final ModConfigSpec.ConfigValue<String> VOICE;
    public static final ModConfigSpec.DoubleValue VOICE_PITCH;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        ENABLED = b.comment("The god watches the world and intervenes on his own (prayers are answered regardless).")
                .define("enabled", true);
        DRY_RUN = b.comment("Only log decisions, never act or speak.")
                .define("dryRun", false);
        BROKER_URL = b.comment("OpenAI-compatible chat completions endpoint (LLM broker).")
                .define("brokerUrl", "http://192.168.1.161:8080/v1/chat/completions");
        API_KEY = b.comment("Broker token. Empty = environment variable ZMGOD_API_KEY, then LIVINGCOLONISTS_API_KEY.")
                .define("apiKey", "");
        MODEL = b.comment("Model or broker role for the god's own visits.")
                .define("model", "director");
        PRAYER_MODEL = b.comment("Model or broker role for answering prayers (should be fast).")
                .define("prayerModel", "director");
        INTERVAL_MIN = b.comment("The god looks at the world on his own once per this many real minutes (random between min and max).")
                .defineInRange("intervalMinMinutes", 60, 5, 1440);
        INTERVAL_MAX = b.defineInRange("intervalMaxMinutes", 120, 5, 1440);
        PRAYER_COOLDOWN = b.comment("A player may pray at most once per this many seconds.")
                .defineInRange("prayerCooldownSeconds", 60, 0, 3600);
        VOICE = b.comment("TTS voice of the god (Living Voice).")
                .define("voice", "vosk:prometey");
        VOICE_PITCH = b.comment("Below 1 = lower and slower voice.")
                .defineInRange("voicePitch", 0.88, 0.5, 1.5);
        SPEC = b.build();
    }

    public God(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> GodCommands.register(e.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e) -> Director.onServerTick(e.getServer()));
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent e) -> Chronicle.onDeath(e));
        NeoForge.EVENT_BUS.addListener((AdvancementEvent.AdvancementEarnEvent e) -> Chronicle.onAdvancement(e));
        NeoForge.EVENT_BUS.addListener((ServerChatEvent e) -> Prayers.onChat(e));
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> Prayers.onRightClickBlock(e));
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> {
            if (LIVING_VOICE) LvBridge.hook();
            if (subscribed) return;
            subscribed = true;
            var mc = IMinecoloniesAPI.getInstance().getEventBus();
            mc.subscribe(CitizenDiedModEvent.class, Chronicle::onCitizenDied);
            mc.subscribe(ColonyCreatedModEvent.class, Chronicle::onColonyCreated);
            mc.subscribe(BuildingConstructionModEvent.class, Chronicle::onBuilt);
        });
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> {
            if (LIVING_VOICE) LvBridge.unhook();
            Director.reset();
        });
    }

    /** Подписка на шину MineColonies — один раз за запуск процесса (шина живёт дольше сервера). */
    private static boolean subscribed;

    static String apiKey() {
        String k = API_KEY.get();
        if (k.isEmpty()) k = System.getenv().getOrDefault("ZMGOD_API_KEY", "");
        if (k.isEmpty()) k = System.getenv().getOrDefault("LIVINGCOLONISTS_API_KEY", "");
        return k;
    }
}
