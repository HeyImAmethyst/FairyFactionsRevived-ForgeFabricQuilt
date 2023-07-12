package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class JobTillLand extends FairyJob
{

    public JobTillLand(FairyEntity fairy)
    {
        super(fairy);
    }

    public boolean canRun(final ItemStack stack, int x, int y, int z, final Level world)
    {
        if(!canStart())
            return false;

        y = y - 1;

        for ( int a = 0; a < 3; a++ )
        {
            BlockPos blockPos = new BlockPos(x, y, z);

            BlockState blockState = world.getBlockState(blockPos);
            final Block i = blockState.getBlock();

            //BlockState toolModifiedState = world.getBlockState(blockPos).getToolModifiedState(pContext, net.minecraftforge.common.ToolActions.HOE_TILL, false);

            if ( world.getBlockState(new BlockPos(x, y + 1, z )).is(Blocks.AIR) && (i == Blocks.GRASS_BLOCK || i == Blocks.DIRT || i == Blocks.COARSE_DIRT))
            {
                final Block block = i == Blocks.COARSE_DIRT ? Blocks.DIRT : Blocks.FARMLAND;

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": hoeing land");

                world.setBlockAndUpdate(new BlockPos( x, y, z), block.defaultBlockState());

                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                fairy.attackAnim = 30;

                if ( fairy.flymode() && fairy.getFlyTime() > 0 )
                {
                    fairy.setFlyTime( 0 );
                }

                return true;
            }

            x += fairy.getRandom().nextInt( 3 ) - 1;
            z += fairy.getRandom().nextInt( 3 ) - 1;
        }

        return false;
    }

    @Override
    public boolean canStart()
    {
        return super.canStart() && FairyUtils.isHoeItem(itemStack);
    }
}
