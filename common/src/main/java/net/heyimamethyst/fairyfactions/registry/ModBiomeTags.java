package net.heyimamethyst.fairyfactions.registry;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTags
{
    public static final TagKey<Biome> IS_PLAINS = makeTag("is_plains");
    public static final TagKey<Biome> IS_FAIRY_BIOME = makeTag("is_fairy_biome");

    private static TagKey<Biome> makeTag(String p_203855_)
    {
        return TagKey.create(Registries.BIOME, new ResourceLocation(FairyFactions.MOD_ID, p_203855_));
    }

    public static TagKey<Biome> makeTag(final ResourceLocation name)
    {
        return TagKey.create(Registries.BIOME, name);
    }
}
