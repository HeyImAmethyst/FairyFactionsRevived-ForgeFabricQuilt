package net.heyimamethyst.fairyfactions.forge.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider
{

	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, FairyFactions.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		//simpleItem(ModItems.VOID_STICK.get());

		handheldItem(ModItems.FAIRY_WAND);

		for (RegistrySupplier<FairyBedItem> supplier:ModItems.FAIRY_BED_ITEMS.values())
		{
			fairyBedItem(supplier, supplier.get().getLogType(), supplier.get().getWoolType());
		}

		spawnEggItem(ModItems.FAIRY_SPAWN_EGG);
		
	}

	private <T extends Item> ItemModelBuilder simpleItem(RegistrySupplier<T> item)
	{
		return withExistingParent(item.getId().getPath(),
						new ResourceLocation("item/generated")).texture("layer0",
										new ResourceLocation(FairyFactions.MOD_ID, "item/" + item.getId().getPath()));
	}

	private <T extends Item> ItemModelBuilder handheldItem(RegistrySupplier<T> item)
	{
		return withExistingParent(item.getId().getPath(),
						new ResourceLocation("item/handheld")).texture("layer0",
										new ResourceLocation(FairyFactions.MOD_ID, "item/" + item.getId().getPath()));
	}

//	private ItemModelBuilder transformationGemItem(Item item)
//	{
//		return withExistingParent(item.getRegistryName().getPath(),
//						new ResourceLocation("item/generated")).texture("layer0",
//										new ResourceLocation(MagiThings.MOD_ID, "item/transformation_gems/" + item.getRegistryName().getPath()));
//	}

	private <T extends Item> ItemModelBuilder fairyBedItem(RegistrySupplier<T> item, FairyBedEntity.LogType logType, FairyBedEntity.WoolType woolType)
	{
		return withExistingParent(item.getId().getPath(),
				new ResourceLocation("fairyfactions:item/fairy_bed"))
				.texture("0",new ResourceLocation("block/" + logType.getName() + "_log_top"))
				.texture("1",new ResourceLocation("block/" + logType.getName() + "_log"))
				.texture("2",new ResourceLocation("block/" + woolType.getName() + "_wool"))
				.texture("3",new ResourceLocation(FairyFactions.MOD_ID,"block/framed_white_wool"))
				.texture("4",new ResourceLocation("block/chain"))
				.texture("5",new ResourceLocation("block/iron_block"))
				.texture("6",new ResourceLocation("minecraft:block/glowstone"));
	}
	
	private <T extends Item> ItemModelBuilder spawnEggItem(RegistrySupplier<T> item)
	{
		return withExistingParent(item.getId().getPath(),
						new ResourceLocation("item/template_spawn_egg"));
	}

	/**
	 * Adds an item model that uses a parent model.
	 *
	 * @param item   The item to generate the model for.
	 * @param parent The parent model to use.
	 */
	private ItemModelBuilder withExistingParent(RegistrySupplier<BlockItem> item, ResourceLocation parent)
	{
		return withExistingParent(item.getId().getPath() + "_item", parent);
	}

//	/**
//	 * Adds a block item model that uses the corresponding block model as the parent model.
//	 *
//	 * @param item The item to generate the model for.
//	 */
	private ItemModelBuilder blockItem(String blockItem, RegistrySupplier<Block> block)
	{
		return withExistingParent(blockItem, new ResourceLocation(
						"magithings",
						"block/" + block.getId().getPath()));

//		return withExistingParent(
//				item,
//				new ResourceLocation(
//						"minecraft",
//						"block/" + item.get().getRegistryName().getPath()));
	}

}
