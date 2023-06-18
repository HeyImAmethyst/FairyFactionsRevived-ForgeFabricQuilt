package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class FairyJob
{
    FairyEntity fairy;
    ItemStack stack;

    public FairyJob(FairyEntity fairy)
    {
        this.fairy = fairy;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public boolean canRun(final ItemStack stack, int x, int y, int z, final Level world)
    {
        return false;
    }

    public abstract boolean canStart();
}
