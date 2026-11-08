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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Настройки «Живого мира» по умолчанию, один раз на установку: русский язык, без диктора и экрана
 * специальных возможностей, чат на Caps Lock. options.txt сборки кладётся только при первой установке,
 * а этот код доводит до того же и тех, у кого игра уже успела записать свои настройки.
 */
@EventBusSubscriber(modid = ZhivoyMirClient.MOD_ID, value = Dist.CLIENT)
public final class FirstRunDefaults {
    private static final Path MARKER = FMLPaths.CONFIGDIR.get().resolve("zmclient-defaults-1.done");
    private static boolean done = Files.exists(MARKER);

    private FirstRunDefaults() {}

    @SubscribeEvent
    static void onOpen(ScreenEvent.Opening event) {
        if (done) return;
        done = true;
        Minecraft mc = Minecraft.getInstance();
        Options o = mc.options;
        o.narrator().set(NarratorStatus.OFF);
        o.narratorHotkey().set(false);
        mc.getNarrator().clear();
        o.onboardAccessibility = false;
        o.keyChat.setKey(InputConstants.getKey("key.keyboard.caps.lock"));
        KeyMapping.resetMapping();
        boolean langChanged = !"ru_ru".equals(o.languageCode);
        o.languageCode = "ru_ru";
        mc.getLanguageManager().setSelected("ru_ru");
        o.save();
        if (event.getNewScreen() instanceof AccessibilityOnboardingScreen) event.setNewScreen(new TitleScreen(true));
        if (langChanged) mc.reloadResourcePacks();
        try {
            Files.createDirectories(MARKER.getParent());
            Files.writeString(MARKER, "Настройки по умолчанию применены; удалите файл, чтобы применить снова.\n");
        } catch (IOException ignored) {
        }
    }
}
