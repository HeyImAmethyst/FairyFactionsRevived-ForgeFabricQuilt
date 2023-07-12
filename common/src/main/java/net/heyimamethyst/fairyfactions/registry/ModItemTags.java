package net.heyimamethyst.fairyfactions.registry;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags
{
	//For adding other items as fairy food
	public static final TagKey<Item> IS_FAIRY_QUEEN_FOOD = makeTag("is_fairy_queen_food");
	public static final TagKey<Item> IS_FAIRY_FOOD = makeTag("is_fairy_food");

	//Used for fairy item pickup
	public static final TagKey<Item> IS_BREEDING_ITEM = makeTag("is_breeding_item");
	//Used for fairy item pickup
	public static final TagKey<Item> IS_ANIMAL_PRODUCT = makeTag("is_animal_product");

	//For adding modded fishing rods
	public static final TagKey<Item> IS_FISHING_ITEM = makeTag("is_fishing_item");
	public static final TagKey<Item> IS_FISH_LOOT = makeTag("is_fish_loot");

	//For adding modded shears
	public static final TagKey<Item> IS_SHEARING_ITEM = makeTag("is_shearing_item");

	public static final TagKey<Item> IS_HAIRCUT_ITEM = makeTag("is_haircut_item");

	//For adding modded axes
	public static final TagKey<Item> IS_AXE_ITEM = makeTag("is_axe_item");

	//For adding modded swords
	public static final TagKey<Item> IS_SWORD_ITEM = makeTag("is_sword_item");

	//For adding modded hoe
	public static final TagKey<Item> IS_HOE_ITEM = makeTag("is_hoe_item");

	//For adding modded berry bush
	public static final TagKey<Item> IS_BERRY_BUSH_ITEM = makeTag("is_berry_bush_item");

	public static final TagKey<Item> IS_ADDITIONAL_ITEM_PICKUP = makeTag("is_additional_item_pickup");

	public static final TagKey<Item> ITEM_TO_SMELT = makeTag("item_to_smelt");
	public static final TagKey<Item> ITEM_TO_COOK = makeTag("item_to_cook");

	private static TagKey<Item> bind(String p_203855_)
	{
		return TagKey.create(Registries.ITEM, new ResourceLocation(p_203855_));
	}

	public static TagKey<Item> create(final ResourceLocation name)
	{
		return TagKey.create(Registries.ITEM, name);
	}

	private static TagKey<Item> makeTag(String name)
	{
		return TagKey.create(Registries.ITEM, new ResourceLocation(FairyFactions.MOD_ID,  name));
	}

}
