package net.heyimamethyst.fairyfactions.forge.registry;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModSpawns
{
	public static void registerSpawns() 
	{
          registerSpawn(ModEntities.FAIRY_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FairyEntity::checkFairySpawnRules);
    }

    private static <T extends Mob> void registerSpawn(EntityType<T> entityType, SpawnPlacements.Type decoratorType, Heightmap.Types heightMapType, SpawnPlacements.SpawnPredicate<T> predicate) 
    {
        SpawnPlacements.register(entityType, decoratorType, heightMapType, predicate);
    }
}
