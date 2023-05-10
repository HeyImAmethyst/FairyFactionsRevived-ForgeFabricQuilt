package net.heyimamethyst.fairyfactions.fabriclike;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModRegistries;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModWorldGen;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;

public class ModFabricLike {
    public static void init()
    {
        FairyConfig.registerConfigs();
        FairyConfig.passConfigValues();
        FairyFactions.init();
        ModSpawnEggItem.InitSpawnEggs();
        ModRegistries.registerModStuffs();
        ModWorldGen.generateModWorldGen();
    }
}
