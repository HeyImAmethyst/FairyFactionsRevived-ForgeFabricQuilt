package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;

public class MisActionGatherProductFromFurnace extends FairyMiscAction
{

    public MisActionGatherProductFromFurnace(FairyEntity fairy)
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
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( popResoruceFromFurnace( world, x, y, z) )
            {
                fairy.armSwing( !fairy.didSwing );

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

    private boolean popResoruceFromFurnace(final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof AbstractFurnaceBlock)
        {
            AbstractFurnaceBlock blastFurnaceBlock = (AbstractFurnaceBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if ( blockEntity != null && blockEntity instanceof AbstractFurnaceBlockEntity)
            {
                AbstractFurnaceBlockEntity furnaceBlock = (AbstractFurnaceBlockEntity) blockEntity;

                if(!furnaceBlock.getItem(2).isEmpty())
                {
                    blastFurnaceBlock.popResource(world, pos, furnaceBlock.getItem(2));

                    //FairyJobManager.INSTANCE.cleanSlotInBlastFurnace(blastFurnace, 2);
                    furnaceBlock.setItem( 2, ItemStack.EMPTY );
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
