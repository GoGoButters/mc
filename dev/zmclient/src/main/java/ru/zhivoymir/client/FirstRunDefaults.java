package ru.zhivoymir.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Настройки «Живого мира» по умолчанию, один раз на установку: русский язык, без диктора и экрана
 * специальных возможностей. options.txt сборки кладётся только при первой установке,
 * а этот код доводит до того же и тех, у кого игра уже успела записать свои настройки.
 * Клавиши (вторая версия): чат — T, голосовой чат «Говорить» — Caps Lock, управление рюкзаками — Shift+B
 * (у Backpacked оно стояло на V вместе с меню голосового чата). Трогаем только то, что стоит как у нас было раньше
 * или не назначено, — своё игрок не теряет.
 */
@EventBusSubscriber(modid = ZhivoyMirClient.MOD_ID, value = Dist.CLIENT)
public final class FirstRunDefaults {
    private static final Path MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-1.done");
    private static final Path KEYS_MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-2.done");
    private static boolean done = Files.exists(MARKER) && Files.exists(KEYS_MARKER);

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
                if (k.getName().equals("key.push_to_talk") && k.isUnbound()) k.setKeyModifierAndCode(KeyModifier.NONE, caps);
                if (k.getName().equals("key.backpacked.open_management") && k.getKey().equals(InputConstants.getKey("key.keyboard.v")))
                    k.setKeyModifierAndCode(KeyModifier.SHIFT, InputConstants.getKey("key.keyboard.b"));
            }
            mark(KEYS_MARKER);
        }
        KeyMapping.resetMapping();
        o.save();
        if (event.getNewScreen() instanceof AccessibilityOnboardingScreen) event.setNewScreen(new TitleScreen(true));
        if (langChanged) mc.reloadResourcePacks();
    }

    private static void mark(Path marker) {
        try {
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, "Настройки по умолчанию применены; удалите файл, чтобы применить снова.\n");
        } catch (IOException ignored) {
        }
    }
}
