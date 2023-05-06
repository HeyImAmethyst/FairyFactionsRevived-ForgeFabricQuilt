package net.heyimamethyst.fairyfactions.registry;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags
{
	public static final TagKey<Item> IS_FISH_LOOT = makeTag("is_fish_loot");
	public static final TagKey<Item> IS_ADDITIONAL_ITEM_PICKUP = makeTag("is_additional_item_pickup");

	private static TagKey<Item> bind(String p_203855_)
	{
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(p_203855_));
	}

	public static TagKey<Item> create(final ResourceLocation name)
	{
		return TagKey.create(Registry.ITEM_REGISTRY, name);
	}

	private static TagKey<Item> makeTag(String name)
	{
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(FairyFactions.MOD_ID,  name));
	}

}
