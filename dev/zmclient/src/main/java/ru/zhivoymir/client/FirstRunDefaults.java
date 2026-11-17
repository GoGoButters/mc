package ru.zhivoymir.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Настройки «Живого мира» по умолчанию, один раз на установку: русский язык, без диктора и экрана
 * специальных возможностей. options.txt сборки кладётся только при первой установке,
 * а этот код доводит до того же и тех, у кого игра уже успела записать свои настройки.
 * Клавиши (вторая версия): чат — T, управление рюкзаками — Shift+B
 * (у Backpacked оно стояло на V вместе с меню голосового чата). Трогаем только то, что стоит как у нас было раньше
 * или не назначено, — своё игрок не теряет.
 * Третья версия: голосовой чат — с активацией голосом (Caps Lock теперь «Говорить с жителями»); если «Говорить»
 * голосового чата стоит на Caps Lock — снимаем. Simple Voice Chat читает свой конфиг ещё при загрузке модов,
 * поэтому меняем его объект настроек на ходу: MicThread сверяет режим на каждом кадре и сам перестраивается.
 * Заодно отмечаем знакомство с голосовым чатом пройденным — иначе на его экране можно выбрать «нажми и говори»
 * без назначенной клавиши.
 * Четвёртая версия: клавиши JourneyMap, совпадавшие с другими модами, если стоят по умолчанию: «Создать метку»
 * B (рюкзак) → Z, «Менеджер меток» N (выключить голосовой чат) → ], «Имена существ» G (занята шестью модами) — снята.
 * Шестая версия: у голосового чата сняты «Выключить микрофон» M (полная карта JourneyMap) и «Выключить голосовой чат»
 * N (режим руки Mekanism), если стоят по умолчанию.
 */
@EventBusSubscriber(modid = ZhivoyMirClient.MOD_ID, value = Dist.CLIENT)
public final class FirstRunDefaults {
    private static final Path MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-1.done");
    private static final Path KEYS_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-2.done");
    private static final Path VOICE_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-3.done");
    private static final Path MAP_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-4.done");
    private static final Path MAP_M_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-5.done");
    private static final Path VOICE_KEYS_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-6.done");
    private static final Logger LOG = LogUtils.getLogger();
    private static boolean done = Files.exists(MARKER) && Files.exists(KEYS_MARKER) && Files.exists(VOICE_MARKER)
            && Files.exists(MAP_MARKER) && Files.exists(MAP_M_MARKER) && Files.exists(VOICE_KEYS_MARKER);

    private FirstRunDefaults() {}

    @SubscribeEvent
    static void onOpen(ScreenEvent.Opening event) {
        if (done) return;
        done = true;
        Minecraft mc = Minecraft.getInstance();
        Options o = mc.options;
        boolean langChanged = false;
        if (!Files.exists(MARKER)) {
            o.narrator().set(NarratorStatus.OFF);
            o.narratorHotkey().set(false);
            mc.getNarrator().clear();
            o.onboardAccessibility = false;
            langChanged = !"ru_ru".equals(o.languageCode);
            o.languageCode = "ru_ru";
            mc.getLanguageManager().setSelected("ru_ru");
            mark(MARKER);
        }
        if (!Files.exists(KEYS_MARKER)) {
            InputConstants.Key caps = InputConstants.getKey("key.keyboard.caps.lock");
            if (o.keyChat.getKey().equals(caps)) o.keyChat.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.getKey("key.keyboard.t"));
            for (KeyMapping k : o.keyMappings) {
                if (k.getName().equals("key.backpacked.open_management") && k.getKey().equals(InputConstants.getKey("key.keyboard.v")))
                    k.setKeyModifierAndCode(KeyModifier.SHIFT, InputConstants.getKey("key.keyboard.b"));
            }
            mark(KEYS_MARKER);
        }
        if (!Files.exists(VOICE_MARKER) && voiceActivation()) {
            InputConstants.Key caps = InputConstants.getKey("key.keyboard.caps.lock");
            for (KeyMapping k : o.keyMappings) {
                if (k.getName().equals("key.push_to_talk") && k.getKey().equals(caps))
                    k.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.UNKNOWN);
            }
            mark(VOICE_MARKER);
        }
        if (!Files.exists(MAP_MARKER)) {  // без JourneyMap цикл ничего не найдёт — всё равно отмечаем
            for (KeyMapping k : o.keyMappings) {
                switch (k.getName()) {
                    case "key.journeymap.create_waypoint", "key.journeymap.fullscreen_create_waypoint" -> rebind(k, "b", "key.keyboard.z");
                    case "key.journeymap.fullscreen_waypoints" -> rebind(k, "n", "key.keyboard.right.bracket");
                    case "key.journeymap.toggle_entity_names" -> rebind(k, "g", null);
                    default -> {}
                }
            }
            mark(MAP_MARKER);
        }
        if (!Files.exists(MAP_M_MARKER)) {  // M — полная карта JourneyMap, карта владений Recruits — Shift+M
            for (KeyMapping k : o.keyMappings) {
                if (k.getName().equals("key.recruits.map_screen_key")) rebind(k, "m", KeyModifier.SHIFT, "key.keyboard.m");
            }
            for (KeyMapping k : o.keyMappings) {
                if (k.getName().equals("key.journeymap.map_toggle_alt")) rebind(k, "j", "key.keyboard.m");
            }
            mark(MAP_M_MARKER);
        }
        if (!Files.exists(VOICE_KEYS_MARKER)) {  // без голосового чата цикл ничего не найдёт — всё равно отмечаем
            for (KeyMapping k : o.keyMappings) {
                switch (k.getName()) {
                    case "key.mute_microphone" -> rebind(k, "m", null);
                    case "key.disable_voice_chat" -> rebind(k, "n", null);
                    default -> {}
                }
            }
            mark(VOICE_KEYS_MARKER);
        }
        KeyMapping.resetMapping();
        o.save();
        if (event.getNewScreen() instanceof AccessibilityOnboardingScreen) event.setNewScreen(new TitleScreen(true));
        if (langChanged) mc.reloadResourcePacks();
    }

    /** Переназначает клавишу ({@code to} = null — снять), только если она стоит на {@code from} без модификатора (как по умолчанию). */
    private static void rebind(KeyMapping k, String from, String to) {
        rebind(k, from, KeyModifier.NONE, to);
    }

    private static void rebind(KeyMapping k, String from, KeyModifier mod, String to) {
        if (k.getKeyModifier() == KeyModifier.NONE && k.getKey().equals(InputConstants.getKey("key.keyboard." + from)))
            k.setKeyModifierAndCode(mod, to == null ? InputConstants.UNKNOWN : InputConstants.getKey(to));
    }

    /** Simple Voice Chat: режим микрофона — активация голосом. False — не вышло, попробуем при следующем запуске. */
    private static boolean voiceActivation() {
        if (!ModList.get().isLoaded("voicechat")) return false;
        try {
            Object config = Class.forName("de.maxhenkel.voicechat.VoicechatClient").getField("CLIENT_CONFIG").get(null);
            if (config == null) return false;
            Object entry = config.getClass().getField("microphoneActivationType").get(config);
            Object voice = Class.forName("de.maxhenkel.voicechat.voice.client.MicrophoneActivationType").getField("VOICE").get(null);
            Class<?> configEntry = Class.forName("de.maxhenkel.voicechat.configbuilder.entry.ConfigEntry");
            configEntry.getMethod("set", Object.class).invoke(entry, voice);
            configEntry.getMethod("save").invoke(entry);
            Object onboarding = config.getClass().getField("onboardingFinished").get(config);
            configEntry.getMethod("set", Object.class).invoke(onboarding, Boolean.TRUE);
            configEntry.getMethod("save").invoke(onboarding);
            return true;
        } catch (ReflectiveOperationException | LinkageError | RuntimeException e) {
            LOG.warn("Не удалось включить активацию голосом в Simple Voice Chat", e);
            return false;
        }
    }

    private static void mark(Path marker) {
        try {
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, "Настройки по умолчанию применены; удалите файл, чтобы применить снова.\n");
        } catch (IOException ignored) {
        }
    }
}
