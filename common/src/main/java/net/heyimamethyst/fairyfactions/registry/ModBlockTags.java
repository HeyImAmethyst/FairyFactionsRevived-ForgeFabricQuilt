package net.heyimamethyst.fairyfactions.registry;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModBlockTags
{
    public static final TagKey<Block> IS_BERRY_BUSH_BLOCK = makeTag("is_berry_bush_block");

    private static TagKey<Block> makeTag(String name)
    {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(FairyFactions.MOD_ID,  name));
    }
}