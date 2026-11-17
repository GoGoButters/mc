package ru.zhivoymir.tweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import immersive_aircraft.entity.GyrodyneEntity;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import ru.zhivoymir.tweaks.aircraft.PsyWeapon;

/**
 * Клиент психологического оружия: клавиша (левый Ctrl — IA его не использует, у ванили это бег,
 * в машине он не нужен) и музыка по пакетам сервера, по одной на винтокрыл.
 */
public final class PsyClient {
    static final KeyMapping KEY = new KeyMapping("key.zmtweaks.psy_weapon", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.zmtweaks");

    private static final Map<Integer, ValkyriesSound> PLAYING = new HashMap<>();

    private PsyClient() {}

    static void onRegisterKeys(RegisterKeyMappingsEvent e) {
        e.register(KEY);
    }

    static void onClientTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        while (KEY.consumeClick()) {
            if (mc.player != null && mc.player.getVehicle() instanceof GyrodyneEntity) {
                PacketDistributor.sendToServer(new PsyWeapon.Toggle());
            }
        }
    }

    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut e) {
        PLAYING.values().forEach(ValkyriesSound::finish);
        PLAYING.clear();
    }

    /**
     * start и pos: если музыка этого винтокрыла не звучит (не начата, оборвана сменой измерения
     * или перезагрузкой звука) — начать, иначе обновить позицию из пакета.
     */
    public static void onStart(int entityId, double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        ValkyriesSound sound = PLAYING.get(entityId);
        if (sound != null && !sound.isStopped() && mc.getSoundManager().isActive(sound)) {
            sound.setTarget(x, y, z);
            return;
        }
        if (sound != null) sound.finish();
        sound = new ValkyriesSound(entityId, x, y, z);
        PLAYING.put(entityId, sound);
        mc.getSoundManager().play(sound);
    }

    public static void onStop(int entityId) {
        ValkyriesSound sound = PLAYING.remove(entityId);
        if (sound != null) sound.finish();
    }
}
