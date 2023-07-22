package net.heyimamethyst.fairyfactions.fabriclike;

import net.heyimamethyst.fairyfactions.FairyConfig;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModRegistries;
import net.heyimamethyst.fairyfactions.fabriclike.registry.ModWorldGen;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.registry.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModFabricLike
{
    public static void init()
    {
        FairyFactions.init();
        ModSpawnEggItem.InitSpawnEggs();
        ModRegistries.registerModStuffs();
        ModWorldGen.generateModWorldGen();

        ModBlocks.BLOCKS.forEach(block ->
        {
            Registry.register(Registry.ITEM, new ResourceLocation(FairyFactions.MOD_ID, block.getRegistryId().toString()), new BlockItem(block.get(), new Item.Properties()
                    .tab(CreativeModeTab.TAB_DECORATIONS)));
        });
    }
}
