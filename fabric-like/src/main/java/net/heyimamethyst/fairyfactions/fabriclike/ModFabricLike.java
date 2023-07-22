package net.heyimamethyst.fairyfactions.fabriclike;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModRegistries;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModWorldGen;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

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

        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_WHITE_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_ORANGE_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_MAGENTA_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_LIGHT_BLUE_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_YELLOW_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_LIME_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_PINK_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_GRAY_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_LIGHT_GRAY_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_CYAN_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_PURPLE_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_BLUE_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_BROWN_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_GREEN_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_RED_FAIRY_BED.get());
        addToItemGroup(CreativeModeTabs.COLORED_BLOCKS, ModItems.OAK_BLACK_FAIRY_BED.get());
    }

    public static void addToItemGroup(ResourceKey<CreativeModeTab> group, Item item)
    {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addBefore(Items.FOX_SPAWN_EGG, item));
    }
}
