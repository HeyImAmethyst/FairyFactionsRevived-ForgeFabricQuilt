package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class FairyJob
{
    FairyEntity fairy;
    ItemStack itemStack;

    public FairyJob(FairyEntity fairy)
    {
        this.fairy = fairy;
    }

    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public boolean canRun(final ItemStack stack, int x, int y, int z, final Level world)
    {
        return false;
    }

    public abstract boolean canStart();
}
