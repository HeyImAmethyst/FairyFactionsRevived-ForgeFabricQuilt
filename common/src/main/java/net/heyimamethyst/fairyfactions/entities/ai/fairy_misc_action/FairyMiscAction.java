package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class FairyMiscAction
{
    FairyEntity fairy;
    ItemStack itemStack;

    public FairyMiscAction(FairyEntity fairy)
    {
        this.fairy = fairy;
    }

    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public boolean canRun(final ChestBlockEntity chestBlockEntity, int x, int y, int z, final Level world)
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
