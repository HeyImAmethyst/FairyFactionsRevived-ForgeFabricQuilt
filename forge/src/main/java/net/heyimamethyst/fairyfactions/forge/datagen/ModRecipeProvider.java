package net.heyimamethyst.fairyfactions.forge.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder
{

	public ModRecipeProvider(DataGenerator p_125973_)
	{
		super(p_125973_);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer)
	{

		AddFairyBedRecipies(finishedRecipeConsumer);

		ShapedRecipeBuilder.shaped(ModItems.FAIRY_WAND.get(), 1)
				.define('#', Items.STICK)
				.define('W', Items.PINK_STAINED_GLASS)
				.define('G', Items.GLOWSTONE)
				.pattern(" WG")
				.pattern(" #W")
				.pattern("#  ").unlockedBy("has_stick", has(Items.STICK)).save(finishedRecipeConsumer);
	}

	private static void AddFairyBedRecipies(Consumer<FinishedRecipe> finishedRecipeConsumer)
	{
		for (RegistrySupplier<FairyBedItem> supplier: ModItems.FAIRY_BED_ITEMS.values())
		{
			fairyBedBuilder(supplier.get(), supplier.get().getLogType(), supplier.get().getWoolType()).unlockedBy("has_log", has(ItemTags.LOGS)).save(finishedRecipeConsumer);
		}
	}

	protected static RecipeBuilder fairyBedBuilder(ItemLike itemResult, FairyBedEntity.LogType logType, FairyBedEntity.WoolType woolType)
	{
		return ShapedRecipeBuilder.shaped(itemResult, 1).group("fairy_bed")
				.define('#', ForgeRegistries.ITEMS.getValue(new ResourceLocation(logType.getName() + "_log")))
				.define('W', ForgeRegistries.ITEMS.getValue(new ResourceLocation(woolType.getName() + "_wool")))
				.define('G', Items.GLOWSTONE)
				.pattern("###")
				.pattern("#G#")
				.pattern("#W#");
	}
	
	protected static SimpleCookingRecipeBuilder cook(ItemLike itemResult, Ingredient ingredient)
	{
		return SimpleCookingRecipeBuilder.smelting(ingredient, itemResult, 0.7F, 200).unlockedBy(getHasName(itemResult), has(itemResult));
	}
}
