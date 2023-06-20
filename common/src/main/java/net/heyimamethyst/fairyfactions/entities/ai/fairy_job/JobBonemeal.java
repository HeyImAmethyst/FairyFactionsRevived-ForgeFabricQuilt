package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JobBonemeal extends FairyJob
{
    public JobBonemeal(FairyEntity fairy) {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        y = y - 1;

        for ( int a = 0; a < 3; a++ )
        {
            final BlockPos pos = new BlockPos(x,y+1,z);
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();

            if ( block instanceof BonemealableBlock && ((BonemealableBlock) block).isValidBonemealTarget(world, pos, state, false) && (fairy.blockPosition() != pos || fairy.blockPosition() != pos.above()))
            {

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": bonemealing");

                ((BonemealableBlock) block).performBonemeal((ServerLevel)world, fairy.getRandom(), pos, state);

                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.shrink(1);

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
        return FairyUtils.isBonemealItem(itemStack.getItem());
    }
}
