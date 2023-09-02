package net.heyimamethyst.fairyfactions.forge.events.mod;

import dev.architectury.registry.registries.Registries;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModBlocks;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
	@SubscribeEvent
	public static void RegisterEntityAttributes(EntityAttributeCreationEvent event) 
	{
		event.put(ModEntities.FAIRY_ENTITY.get(), FairyEntity.createAttributes().build());
	}

	@SubscribeEvent
	public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event)
	{
		event.register(ModEntities.FAIRY_ENTITY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FairyEntity::checkFairySpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
	}

	@SubscribeEvent
	//@OnlyIn(Dist.CLIENT)
	public static void registerParticleFactories(RegisterParticleProvidersEvent event)
	{
		((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(FairyBedTextureGenerator.INSTANCE);
	}
}
