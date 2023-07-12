package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MisActionGatherProductFromBrewingStand extends FairyMiscAction
{

    public MisActionGatherProductFromBrewingStand(FairyEntity fairy)
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

            if( popResoruceFromBrewingStand( world, x, y, z) )
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

    private boolean popResoruceFromBrewingStand(final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof BrewingStandBlock)
        {
            BrewingStandBlock brewingStandBlock = (BrewingStandBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if ( blockEntity != null && blockEntity instanceof BrewingStandBlockEntity)
            {
                BrewingStandBlockEntity brewingStand = (BrewingStandBlockEntity) blockEntity;

                if(FairyJobManager.INSTANCE.setBrewingStandForClearing)
                {
                    FairyJobManager.INSTANCE.setBrewingStandForClearing = false;

                    for (int i = 0; i < 3; ++i)
                    {
                        if(brewingStand.getItem(i).getItem() instanceof PotionItem)
                        {
                            brewingStandBlock.popResource(world, pos, brewingStand.getItem(i));

                            //FairyJobManager.INSTANCE.cleanSlotInBlastFurnace(blastFurnace, 2);
                            brewingStand.setItem( i, ItemStack.EMPTY );

                        }
                    }

//                    brewingStandBlock.popResource(world, pos, brewingStand.getItem(3));
//                    brewingStand.setItem( 3, ItemStack.EMPTY );

                    return true;
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

        return false;
    }
}
