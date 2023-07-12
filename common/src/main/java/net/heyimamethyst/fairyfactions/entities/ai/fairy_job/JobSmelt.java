package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

//TODO: Not yet finished implementing, still WIP!!

public class JobSmelt extends FairyJob
{

    public JobSmelt(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world) {
        return smelt(stack, x, y, z, world);
    }

    private boolean smelt(final ItemStack stack, int x, final int y, int z, final Level world)
    {
        if(!canStart())
            return false;

        final int m = x;
        final int n = z;

        for (int a = 0; a < 9; a++) {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if (assesBlastFurnace(stack, world, x, y, z)) {
                stack.shrink(1);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.getItem());

                fairy.attackAnim = 30;

                if (!fairy.flymode() && fairy.getFlyTime() > 0) {
                    fairy.setFlyTime(0);
                }

                return true;
            }
        }

        return false;
    }

    private boolean assesBlastFurnace(final ItemStack stack, final Level world, final int x, final int y, final int z) {
        final BlockPos pos = new BlockPos(x, y, z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof BlastFurnaceBlock )
        {
            BlastFurnaceBlock blastFurnaceBlock = (BlastFurnaceBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity != null && blockEntity instanceof BlastFurnaceBlockEntity) {
                BlastFurnaceBlockEntity blastFurnace = (BlastFurnaceBlockEntity) blockEntity;

                fairy.getNavigation().moveTo(x + 0.5, y, z + 0.5, 0.3D);

                if (populateFuel(blastFurnace, stack))
                {
                    return true;
                }

                if (populateInput(world, blastFurnace, stack))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean populateFuel(BlastFurnaceBlockEntity blastFurnace, ItemStack stack)
    {
        if(BlastFurnaceBlockEntity.isFuel(stack))
        {
            if (stack.getCount() > 0)
            {

                ItemStack furnaceStack = blastFurnace.getItem(1);
                ItemStack itemStackForFurnace = new ItemStack(stack.getItem());

                if(furnaceStack.getCount() == furnaceStack.getMaxStackSize())
                {
                    return false;
                }

                itemStackForFurnace.setCount(furnaceStack.getCount() + stack.getCount());

                blastFurnace.setItem(1, itemStackForFurnace);

                return true;
            }
        }

        return false;
    }

    private boolean populateInput(Level world, BlastFurnaceBlockEntity blastFurnace, ItemStack stack)
    {

        if(stack.is(ModItemTags.ITEM_TO_SMELT))
        {
            ItemStack furnaceStack = blastFurnace.getItem(0);
            ItemStack itemStackForFurnace = new ItemStack(stack.getItem());

            if(furnaceStack.getCount() == furnaceStack.getMaxStackSize())
            {
                return false;
            }

            itemStackForFurnace.setCount(furnaceStack.getCount() + stack.getCount());

            blastFurnace.setItem(0, itemStackForFurnace);

            return true;
        }

        return false;
    }

    @Override
    public boolean canStart()
    {
        return super.canStart();
    }
}
