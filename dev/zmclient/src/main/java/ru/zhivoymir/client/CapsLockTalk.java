package ru.zhivoymir.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sun.jna.Library;
import com.sun.jna.Native;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Говорят в голосовом чате, зажав Caps Lock, но Windows при каждом нажатии переключает заглавные буквы.
 * Если при открытии любого окна (чат, табличка, наковальня) Caps Lock горит — нажимаем его ещё раз,
 * чтобы игрок писал строчными. Работает, только пока на Caps Lock стоит «Говорить» или чат.
 */
@EventBusSubscriber(modid = ZhivoyMirClient.MOD_ID, value = Dist.CLIENT)
public final class CapsLockTalk {
    private static final byte VK_CAPITAL = 0x14;
    private static final int KEYEVENTF_KEYUP = 0x0002;

    private CapsLockTalk() {}

    /** Две функции user32.dll; JNA входит в сам Minecraft. */
    private interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);

        short GetKeyState(int vKey);

        void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    }

    @SubscribeEvent
    static void onOpen(ScreenEvent.Opening event) {
        if (event.getNewScreen() == null) return;
        if (Util.getPlatform() != Util.OS.WINDOWS) return;
        if (!onCapsLock()) return;
        try {
            if ((User32.INSTANCE.GetKeyState(VK_CAPITAL) & 1) == 0) return;
            User32.INSTANCE.keybd_event(VK_CAPITAL, (byte) 0x45, 0, 0);
            User32.INSTANCE.keybd_event(VK_CAPITAL, (byte) 0x45, KEYEVENTF_KEYUP, 0);
        } catch (Throwable ignored) {
            // нет JNA или чужая ОС — остаётся как есть
        }
    }

    private static boolean onCapsLock() {
        for (KeyMapping k : Minecraft.getInstance().options.keyMappings) {
            if (!k.getName().equals("key.push_to_talk") && !k.getName().equals("key.chat")) continue;
            InputConstants.Key key = k.getKey();
            if (key.getType() == InputConstants.Type.KEYSYM && key.getValue() == GLFW.GLFW_KEY_CAPS_LOCK) return true;
        }
        return false;
    }
}
