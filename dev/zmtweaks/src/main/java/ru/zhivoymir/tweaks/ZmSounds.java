package ru.zhivoymir.tweaks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Звуки мода; файлы и громкость — в assets/zmtweaks/sounds.json. */
public final class ZmSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, ZmTweaks.MOD_ID);

    /** Пропеллер IA (immersive_aircraft:propeller) с громкостью 0.5 — мотор винтокрыла вдвое тише. */
    public static final DeferredHolder<SoundEvent, SoundEvent> GYRODYNE_ENGINE = register("gyrodyne_engine");
    /** «Полёт валькирий» — психологическое оружие винтокрыла. */
    public static final DeferredHolder<SoundEvent, SoundEvent> VALKYRIES = register("valkyries");

    private ZmSounds() {}

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ZmTweaks.MOD_ID, name);
        return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
