package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MiscActionCutTallGrass extends FairyMiscAction
{

    public MiscActionCutTallGrass(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ChestBlockEntity chestBlockEntity, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + (a / 3) - 1;
            z = n + (a % 3) - 1;
            //final BlockPos pos = new BlockPos(x,y,z);
            final BlockState state = world.getBlockState(new BlockPos(x,y,z));
            final Block above = world.getBlockState(new BlockPos(x,y + 1, z)).getBlock();
            final Block below = world.getBlockState(new BlockPos(x,y - 1, z)).getBlock();

            if (FairyUtils.breakablePlant( state, above, below ) )
            {
                final Block block = state.getBlock();

                world.destroyBlock(new BlockPos(x, y, z), true, fairy);

                fairy.armSwing( !fairy.didSwing );
                fairy.attackAnim = 30;

                if ( fairy.flymode() && fairy.getFlyTime() > 0 )
                {
                    fairy.setFlyTime( 0 );
                }

                return true;
            }
        }

        return false;
    }
}
