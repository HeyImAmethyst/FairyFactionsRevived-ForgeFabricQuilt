package net.heyimamethyst.fairyfactions.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(FairyFactions.MOD_ID, Registries.SOUND_EVENT);

    public static void Init()
    {
        SOUND_EVENTS.register();
    }

    public static RegistrySupplier<SoundEvent> FAIRY_IDLE = registerSoundEvent("fairy_idle");
    public static RegistrySupplier<SoundEvent> FAIRY_QUEEN_IDLE = registerSoundEvent("fairy_queen_idle");

    public static RegistrySupplier<SoundEvent> FAIRY_HURT = registerSoundEvent("fairy_hurt");
    public static RegistrySupplier<SoundEvent> FAIRY_QUEEN_HURT = registerSoundEvent("fairy_queen_hurt");

    public static RegistrySupplier<SoundEvent> FAIRY_DEATH = registerSoundEvent("fairy_death");
    public static RegistrySupplier<SoundEvent> FAIRY_QUEEN_DEATH = registerSoundEvent("fairy_queen_death");

    public static RegistrySupplier<SoundEvent> FAIRY_ANGRY = registerSoundEvent("fairy_angry");
    public static RegistrySupplier<SoundEvent> FAIRY_QUEEN_ANGRY = registerSoundEvent("fairy_queen_angry");

    public static RegistrySupplier<SoundEvent> FAIRY_FLAP = registerSoundEvent("fairy_flap");


    private static RegistrySupplier<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FairyFactions.MOD_ID, name)));
    }
}
