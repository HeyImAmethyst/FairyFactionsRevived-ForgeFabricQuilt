package net.heyimamethyst.fairyfactions.fabriclike.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModEntities;

public class ModRegistries
{
    public static void registerModStuffs()
    {
        registerEntityAtrributes();
    }

    private static void registerEntityAtrributes()
    {
        FabricDefaultAttributeRegistry.register(ModEntities.FAIRY_ENTITY.get(), FairyEntity.createAttributes());
    }
}
