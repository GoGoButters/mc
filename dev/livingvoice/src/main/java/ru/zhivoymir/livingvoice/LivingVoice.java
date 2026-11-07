package ru.zhivoymir.livingvoice;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

/**
 * Голосовые разговоры с жителями: речь из Simple Voice Chat (пока игрок держит кнопку) распознаётся
 * сервисом STT и уходит тому жителю MCA или колонисту, на которого игрок смотрит. Колонисты отвечают голосом.
 */
@Mod(LivingVoice.MOD_ID)
public final class LivingVoice {
    public static final String MOD_ID = "livingvoice";
    public static final Logger LOG = LogUtils.getLogger();

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> STT_URL;
    public static final ModConfigSpec.ConfigValue<String> TTS_URL;
    public static final ModConfigSpec.DoubleValue RANGE;
    public static final ModConfigSpec.DoubleValue CALL_RANGE;
    public static final ModConfigSpec.IntValue SILENCE_MS;
    public static final ModConfigSpec.BooleanValue COLONIST_VOICES;
    public static final ModConfigSpec.DoubleValue VOICE_DISTANCE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        STT_URL = b.comment("Speech recognition: POST raw PCM s16le mono, ?rate=16000, answer {\"text\": ...}. Empty = off.")
                .define("sttUrl", "");
        TTS_URL = b.comment("TTS base for colonist voices (MCA realtime protocol): {base}v1/tts/piper/voices and {base}v1/tts/piper/speak. Empty = off.")
                .define("ttsUrl", "");
        RANGE = b.comment("How far (blocks) the player may look at a villager or colonist to talk to it.")
                .defineInRange("range", 12.0, 2.0, 32.0);
        CALL_RANGE = b.comment("How far (blocks) a villager or colonist hears being called by name or job (\"hey, builder!\").")
                .defineInRange("callRange", 16.0, 2.0, 48.0);
        SILENCE_MS = b.comment("A phrase ends when no voice packets arrive for this long (ms).")
                .defineInRange("silenceMs", 450, 150, 3000);
        COLONIST_VOICES = b.comment("Colonists (Living Colonists) speak their lines aloud via voice chat.")
                .define("colonistVoices", true);
        VOICE_DISTANCE = b.comment("How far (blocks) colonist voices are heard.")
                .defineInRange("voiceDistance", 24.0, 4.0, 64.0);
        SPEC = b.build();
    }

    public LivingVoice(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> DebugCommands.register(e.getDispatcher()));
    }
}
