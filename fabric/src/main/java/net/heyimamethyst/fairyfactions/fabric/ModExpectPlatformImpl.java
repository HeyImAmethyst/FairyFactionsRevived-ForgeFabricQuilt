package net.heyimamethyst.fairyfactions.fabric;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

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
        Iterable<Holder<Item>> tags = Registry.ITEM.getTagOrEmpty(tag);

        List<Item> items = new ArrayList<>();

        while(tags.iterator().hasNext())
        {
            Item item = tags.iterator().next().value();
            items.add(item);
        }

        return items.iterator();
    }
}
