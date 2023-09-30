package net.heyimamethyst.fairyfactions.forge.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{
	private static final Logger LOGGER = LogManager.getLogger();
	
	@SubscribeEvent
	public static void GatherData(GatherDataEvent event)
	{
			DataGenerator generator = event.getGenerator();

			PackOutput packOutput = event.getGenerator().getPackOutput();
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

//			Path outputFolder = generator.getOutputFolder();
//			RegistryAccess registries = RegistryAccess.builtinCopy();
//			RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
//			Gson gson = new GsonBuilder()
//				.setPrettyPrinting()
//				.create();
			
			//generator.addProvider(MakeBuiltinRegistryProvider(MagiThings.MOD_ID, outputFolder, gson, ops, registries, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC, ArcaneLevelStem.ARCANE));
			generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
			//generator.addProvider(new ModLootTableProvider(generator));

			generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
			//generator.addProvider(new ModBlockStateProvider(generator, existingFileHelper));
	}
}
