package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MiscActionTrimExcessLeaves extends FairyMiscAction
{

    public MiscActionTrimExcessLeaves(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ChestBlockEntity chestBlockEntity, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        for ( int d = 0; d < 3; d++ )
        {
            final int a = fairy.getRandom().nextInt( 3 );
            final int b = (fairy.getRandom().nextInt( 2 ) * 2) - 1;

            if ( a == 0 )
            {
                x += b;
            }
            else if ( a == 1 )
            {
                y += b;
            }
            else
            {
                z += b;
            }

            final BlockPos pos = new BlockPos(x,y,z);
            final BlockState state = world.getBlockState(pos);

            if ( state.is(BlockTags.LEAVES) && FairyJobManager.INSTANCE.doHaveAxe)
            {
                world.destroyBlock(pos, true, fairy);

                fairy.armSwing( !fairy.didSwing );

                fairy.attackAnim = 20;

                return true;
            }
        }

        return false;
    }
}
