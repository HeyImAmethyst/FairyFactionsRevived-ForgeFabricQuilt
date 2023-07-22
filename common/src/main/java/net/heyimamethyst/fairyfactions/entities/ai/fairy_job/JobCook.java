package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JobCook extends FairyJob
{

    public JobCook(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world) {
        return cook(stack, x, y, z, world);
    }

    private boolean cook(final ItemStack stack, int x, final int y, int z, final Level world)
    {
        if(!canStart())
            return false;

        final int m = x;
        final int n = z;

        for (int a = 0; a < 9; a++) {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if (assesSmoker(stack, world, x, y, z)) {
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

    private boolean assesSmoker(final ItemStack stack, final Level world, final int x, final int y, final int z) {
        final BlockPos pos = new BlockPos(x, y, z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof SmokerBlock)
        {
            SmokerBlock smokerBlock = (SmokerBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity != null && blockEntity instanceof SmokerBlockEntity) {
                SmokerBlockEntity smoker = (SmokerBlockEntity) blockEntity;

                fairy.getNavigation().moveTo(x + 0.5, y, z + 0.5, 0.3D);

                if (populateFuel(smoker, stack))
                {
                    return true;
                }

                if (populateInput(world, smoker, stack))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean populateFuel(SmokerBlockEntity smoker, ItemStack stack)
    {
        if(BlastFurnaceBlockEntity.isFuel(stack))
        {
            if (stack.getCount() > 0)
            {

                ItemStack furnaceStack = smoker.getItem(1);
                ItemStack itemStackForFurnace = new ItemStack(stack.getItem());

                if(furnaceStack.getCount() == furnaceStack.getMaxStackSize())
                {
                    return false;
                }

                itemStackForFurnace.setCount(furnaceStack.getCount() + stack.getCount());

                smoker.setItem(1, itemStackForFurnace);

                return true;
            }
        }

        return false;
    }

    private boolean populateInput(Level world, SmokerBlockEntity smoker, ItemStack stack)
    {

        if(stack.is(ModItemTags.ITEM_TO_COOK))
        {
            ItemStack furnaceStack = smoker.getItem(0);
            ItemStack itemStackForFurnace = new ItemStack(stack.getItem());

            if(furnaceStack.getCount() == furnaceStack.getMaxStackSize())
            {
                return false;
            }

            itemStackForFurnace.setCount(furnaceStack.getCount() + stack.getCount());

            smoker.setItem(0, itemStackForFurnace);

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
