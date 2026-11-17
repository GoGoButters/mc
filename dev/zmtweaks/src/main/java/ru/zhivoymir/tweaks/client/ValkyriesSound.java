package ru.zhivoymir.tweaks.client;

import immersive_aircraft.entity.GyrodyneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import ru.zhivoymir.tweaks.ZmSounds;
import ru.zhivoymir.tweaks.aircraft.PsyWeapon;

/**
 * «Полёт валькирий» одного винтокрыла. Затухание движка выключено (NONE), громкость считается
 * сама: линейно 1 → 0 от винтокрыла до 150 блоков от камеры. Позиция — от сущности, если она
 * загружена у клиента, иначе последняя присланная сервером. Категория RECORDS («Музыкальные блоки»).
 */
final class ValkyriesSound extends AbstractTickableSoundInstance {
    private final int entityId;
    private double targetX, targetY, targetZ;

    ValkyriesSound(int entityId, double x, double y, double z) {
        super(ZmSounds.VALKYRIES.get(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.entityId = entityId;
        this.looping = true;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.relative = false;
        setTarget(x, y, z);
        tick();
    }

    void setTarget(double x, double y, double z) {
        targetX = x;
        targetY = y;
        targetZ = z;
    }

    void finish() {
        stop();
    }

    /** На краю радиуса громкость 0 — звук всё равно должен стартовать и потом нарастать. */
    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.level == null ? null : mc.level.getEntity(entityId);
        if (entity instanceof GyrodyneEntity && !entity.isRemoved()) {
            x = entity.getX();
            y = entity.getY() + 1.0;
            z = entity.getZ();
        } else {
            x = targetX;
            y = targetY;
            z = targetZ;
        }
        Vec3 listener = mc.gameRenderer.getMainCamera().getPosition();
        double distance = listener.distanceTo(new Vec3(x, y, z));
        volume = (float) Mth.clamp(1.0 - distance / PsyWeapon.RANGE, 0.0, 1.0);
    }
}
