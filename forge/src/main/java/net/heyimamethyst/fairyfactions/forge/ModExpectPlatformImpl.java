package net.heyimamethyst.fairyfactions.forge;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

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
    }

    public static Potion getStartingPotionFromMix(PotionBrewing.Mix<Potion> potionMix)
    {
        return potionMix.f_43532_.get();
    }

    public static Item getStartingContainerFromMix(PotionBrewing.Mix<Item> potionMix)
    {
        return potionMix.f_43534_.get();
    }
}
