package net.heyimamethyst.fairyfactions.fabriclike;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heyimamethyst.fairyfactions.FairyConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModRegistries;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModWorldGen;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

public class ModFabricLike
{
    public static void init()
    {
        FairyFactions.init();
        ModSpawnEggItem.InitSpawnEggs();
        addItemsToItemGroups();
        ModRegistries.registerModStuffs();
        ModWorldGen.generateModWorldGen();
    }

    public static void addItemsToItemGroups()
    {
        addToItemGroup(CreativeModeTabs.SPAWN_EGGS, ModItems.FAIRY_SPAWN_EGG.get());

        addToItemGroup(CreativeModeTabs.TOOLS_AND_UTILITIES, ModItems.FAIRY_WAND.get());

        for (RegistrySupplier<FairyBedItem> fairyBedItem: ModItems.FAIRY_BED_ITEMS.values())
        {
            addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, fairyBedItem.get());
        }
    }

    public static void addToItemGroup(CreativeModeTab group, Item item)
    {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(Items.FOX_SPAWN_EGG, item));
    }
}
