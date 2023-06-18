package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class JobForesting extends FairyJob
{
    // Recursion limit for trees.
    private final int	maxTreeHeight = 99;

    public JobForesting(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        final int m = x;
        final int n = z;

        // TODO: handle additional tree types
        // TODO: clean up treecapitation logic, integrate additionalAxeUse

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( chopLog( world, x, y, z) )
            {
                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                if ( stack.getCount() > 0 )
                {
                    additionalAxeUse( stack, x, y + 1, z, world, maxTreeHeight );
                }

                fairy.attackAnim = 30;

                if ( !fairy.flymode() && fairy.getFlyTime() > 0 )
                {
                    fairy.setFlyTime( 0 );
                }

                return true;
            }
        }

        return false;
    }

    private void additionalAxeUse( final ItemStack stack, int x, final int y, int z, final Level world, int recurse )
    {
        if ( recurse > maxTreeHeight )
        {

            recurse = maxTreeHeight;
        }

        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( chopLog( world, x, y, z ) )
            {
                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                if ( stack.getCount() > 0 && recurse > 0 )
                {
                    if ( a != 4 )
                    {
                        additionalAxeUse( stack, x, y, z, world, recurse - 1 );
                    }

                    if ( stack.getCount() > 0 && recurse > 0 )
                    {
                        additionalAxeUse( stack, x, y + 1, z, world, recurse - 1 );
                    }
                }
            }
        }
    }

    private boolean chopLog( final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if ( state.is(BlockTags.LOGS) )
        {

            //FairyFactions.LOGGER.debug(this.fairy.toString()+": chopping wood");

            world.destroyBlock(new BlockPos(x, y, z), true, fairy);

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean canStart()
    {
        return FairyUtils.isAxeItem(stack);
    }
}
