package net.heyimamethyst.fairyfactions.fabric;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;
import java.util.Map;

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
}
