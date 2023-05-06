package net.heyimamethyst.fairyfactions.forge.events.mod;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{

	//@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void gen(BiomeLoadingEvent event)
	{
		addEntityToSpecificBiomeCategories(event, ModEntities.FAIRY_ENTITY.get(), 40, 1, 4, Biome.BiomeCategory.PLAINS, Biome.BiomeCategory.FOREST, Biome.BiomeCategory.MOUNTAIN, Biome.BiomeCategory.MUSHROOM);
	}
	
	@SubscribeEvent
	public static void RegisterEntityAttributes(EntityAttributeCreationEvent event) 
	{
		event.put(ModEntities.FAIRY_ENTITY.get(), FairyEntity.createAttributes().build());
	}

	private static void addEntityToAllBiomesExceptThese(BiomeLoadingEvent event, EntityType<?> type,
														int weight, int minCount, int maxCount, ResourceKey<Biome>... biomes)
	{
		// Goes through each entry in the biomes and sees if it matches the current biome we are loading
		boolean isBiomeSelected = Arrays.stream(biomes).map(ResourceKey::location)
				.map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));

		if(!isBiomeSelected)
		{
			addEntityToAllBiomes(event, type, weight, minCount, maxCount);
		}
	}

	@SafeVarargs
	private static void addEntityToSpecificBiomes(BiomeLoadingEvent event, EntityType<?> type,
												  int weight, int minCount, int maxCount, ResourceKey<Biome>... biomes)
	{
		// Goes through each entry in the biomes and sees if it matches the current biome we are loading
		boolean isBiomeSelected = Arrays.stream(biomes).map(ResourceKey::location)
				.map(Object::toString).anyMatch(s -> s.equals(event.getName().toString()));

		if(isBiomeSelected)
		{
			addEntityToAllBiomes(event, type, weight, minCount, maxCount);
		}
	}

	private static void addEntityToSpecificBiomeCategories(BiomeLoadingEvent event, EntityType<?> type,
													  int weight, int minCount, int maxCount, Biome.BiomeCategory... biomeCategories)
	{

		// Goes through each entry in the biomeCategories and sees if it matches the current biome category the biome we are loading has

//		for (Biome.BiomeCategory category: biomeCategories )
//		{
//			if(event.getCategory().equals(category))
//			{
//				addEntityToAllBiomes(event, type, weight, minCount, maxCount);
//			}
//		}

		if(event.getCategory().equals(Biome.BiomeCategory.PLAINS)
				|| event.getCategory().equals(Biome.BiomeCategory.FOREST)
				|| event.getCategory().equals(Biome.BiomeCategory.MOUNTAIN)
				|| event.getCategory().equals(Biome.BiomeCategory.MUSHROOM))
		{
			List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(type.getCategory());
			base.add(new MobSpawnSettings.SpawnerData(type,weight, minCount, maxCount));
		}
	}

	private static void addEntityToAllOverworldBiomes(BiomeLoadingEvent event, EntityType<?> type,
													  int weight, int minCount, int maxCount)
	{
		if(!event.getCategory().equals(Biome.BiomeCategory.THEEND) && !event.getCategory().equals(Biome.BiomeCategory.NETHER))
		{
			addEntityToAllBiomes(event, type, weight, minCount, maxCount);
		}
	}

	private static void addEntityToAllBiomesNoNether(BiomeLoadingEvent event, EntityType<?> type,
													 int weight, int minCount, int maxCount)
	{
		if(!event.getCategory().equals(Biome.BiomeCategory.NETHER)) {
			List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(type.getCategory());
			base.add(new MobSpawnSettings.SpawnerData(type,weight, minCount, maxCount));
		}
	}

	private static void addEntityToAllBiomesNoEnd(BiomeLoadingEvent event, EntityType<?> type,
												  int weight, int minCount, int maxCount)
	{
		if(!event.getCategory().equals(Biome.BiomeCategory.THEEND))
		{
			List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(type.getCategory());
			base.add(new MobSpawnSettings.SpawnerData(type,weight, minCount, maxCount));
		}
	}

	private static void addEntityToAllBiomes(BiomeLoadingEvent event, EntityType<?> type,
											 int weight, int minCount, int maxCount)
	{
		List<MobSpawnSettings.SpawnerData> base = event.getSpawns().getSpawner(type.getCategory());
		base.add(new MobSpawnSettings.SpawnerData(type,weight, minCount, maxCount));
	}
}
