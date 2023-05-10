package net.heyimamethyst.fairyfactions.forge;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.nio.file.Path;
import java.util.Map;

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
}
