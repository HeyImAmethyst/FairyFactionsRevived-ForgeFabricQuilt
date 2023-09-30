package net.heyimamethyst.fairyfactions.fabric;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.spongepowered.tools.obfuscation.ObfuscationDataProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModExpectPlatformImpl
{
    /**
     * This is our actual method to {@link ModExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Map<EntityType<?>, SpawnEggItem> getSpawnEggMap()
    {
        return null;
    }

    public static Iterator<Item> getItemsOfTag(TagKey<Item> tag)
    {
        List<Item> items = new ArrayList<>();

        for (Holder<Item> item : BuiltInRegistries.ITEM.getTagOrEmpty(tag))
        {
            items.add(item.value());
        }

        return items.iterator();
    }

//    public static Potion getStartingPotionFromMix(PotionBrewing.Mix<Potion> potionMix)
//    {
//        return potionMix.from;
//    }
//
//    public static Item getStartingContainerFromMix(PotionBrewing.Mix<Item> potionMix)
//    {
//        return potionMix.from;
//    }
}
