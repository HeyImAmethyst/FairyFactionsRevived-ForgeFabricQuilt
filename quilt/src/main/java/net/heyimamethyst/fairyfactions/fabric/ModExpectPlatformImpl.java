package net.heyimamethyst.fairyfactions.fabric;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;
import java.util.*;

public class ModExpectPlatformImpl
{
    /**
     * This is our actual method to {@link ModExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return QuiltLoader.getConfigDir();
    }

    public static Map<EntityType<?>, SpawnEggItem> getSpawnEggMap()
    {
        return null;
    }

    public static Iterator<Item> getItemsOfTag(TagKey<Item> tag)
    {
        Iterable<Holder<Item>> tags = Registry.ITEM.getTagOrEmpty(tag);

        List<Item> items = new ArrayList<>();

        while(tags.iterator().hasNext())
        {
            Item item = tags.iterator().next().value();
            items.add(item);
        }

        return items.iterator();
    }

    public static Potion getStartingPotionFromMix(PotionBrewing.Mix<Potion> potionMix)
    {
        return potionMix.from;
    }

    public static Item getStartingContainerFromMix(PotionBrewing.Mix<Item> potionMix)
    {
        return potionMix.from;
    }
}
