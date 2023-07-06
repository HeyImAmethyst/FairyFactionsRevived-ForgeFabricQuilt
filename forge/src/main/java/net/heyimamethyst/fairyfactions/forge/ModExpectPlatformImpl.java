package net.heyimamethyst.fairyfactions.forge;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.*;

public class ModExpectPlatformImpl {
    /**
     * This is our actual method to {@link ModExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Map<EntityType<?>, SpawnEggItem> getSpawnEggMap()
    {
        return ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "f_43201_");
    }

    public static Iterator<Item> getItemsOfTag(TagKey<Item> tag)
    {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag).iterator();

//        Iterable<Holder<Item>> tags = Registry.ITEM.getTagOrEmpty(tag);
//
//        List<Item> items = new ArrayList<>();
//
//        while(tags.iterator().hasNext())
//        {
//            Item item = tags.iterator().next().value();
//            items.add(item);
//        }
//
//        return items.iterator();
    }
}
