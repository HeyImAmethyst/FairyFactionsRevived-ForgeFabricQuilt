package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import com.google.common.collect.ImmutableSet;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class FairyJob
{
    FairyEntity fairy;
    ChestBlockEntity chest;
    ItemStack itemStack;

    public FairyJob(FairyEntity fairy)
    {
        this.fairy = fairy;
    }

    public void setChest(ChestBlockEntity chest)
    {
        this.chest = chest;
    }
    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public boolean doesChestHaveItem(ChestBlockEntity chest, Item item)
    {
        return chest.hasAnyOf(ImmutableSet.of(item));
    }

    public boolean canRun(final ItemStack stack, int x, int y, int z, final Level world)
    {
        return false;
    }

    public boolean canStart()
    {
        boolean chance = FairyUtils.percentChance(fairy, fairy.isEmotional() ? 0.75 : 1.0);

        if(chance)
        {
            return true;
        }

        return false;
    }
}
