package net.heyimamethyst.fairyfactions.forge;

import dev.architectury.platform.forge.EventBuses;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.forge.events.mod.ModEvents;
import net.heyimamethyst.fairyfactions.forge.registry.ModSpawns;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FairyFactions.MOD_ID)
public class ModForge
{
    public ModForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FairyFactions.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        FairyFactions.init();
    }

    private void setup(final FMLCommonSetupEvent event)
    {

        event.enqueueWork(() ->
        {
            //ModSpawns.registerSpawns();
        });

        ModSpawnEggItem.InitSpawnEggs();
    }
}
