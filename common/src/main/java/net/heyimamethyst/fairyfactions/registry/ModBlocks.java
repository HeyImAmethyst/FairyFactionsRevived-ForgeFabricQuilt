package net.heyimamethyst.fairyfactions.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(FairyFactions.MOD_ID, Registry.BLOCK_REGISTRY);

    public static void Init()
    {
        BLOCKS.register();
    }
}
