package net.heyimamethyst.fairyfactions.fabriclike;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModRegistries;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModWorldGen;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModFabricLike {
    public static void init()
    {
        FairyConfig.registerConfigs();
        FairyConfig.passConfigValues();
        FairyFactions.init();
        addItemsToItemGroups();
        ModRegistries.registerModStuffs();
        ModWorldGen.generateModWorldGen();
    }

    public static void addItemsToItemGroups()
    {
        addToItemGroup(CreativeModeTabs.SPAWN_EGGS, ModItems.FAIRY_SPAWN_EGG.get());
    }

    public static void addToItemGroup(CreativeModeTab group, Item item)
    {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(Items.FOX_SPAWN_EGG, item));
    }
}
