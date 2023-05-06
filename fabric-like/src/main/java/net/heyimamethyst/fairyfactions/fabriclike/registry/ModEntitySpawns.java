package net.heyimamethyst.fairyfactions.fabriclike.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntitySpawns
{
    public static void addEntitySpawn()
    {
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.BiomeCategory.PLAINS, Biome.BiomeCategory.FOREST, Biome.BiomeCategory.MOUNTAIN, Biome.BiomeCategory.MUSHROOM),
                MobCategory.AMBIENT, ModEntities.FAIRY_ENTITY.get(), 25, 2, 5);

        SpawnRestrictionAccessor.callRegister(ModEntities.FAIRY_ENTITY.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }
}
