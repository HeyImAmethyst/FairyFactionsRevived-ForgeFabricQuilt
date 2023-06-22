package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModBlockTags;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JobPlantSeed extends FairyJob
{

    public JobPlantSeed(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        Block plantable = null;

        if (FairyUtils.isIPlantable(Block.byItem(stack.getItem())))
        {
            plantable = Block.byItem(stack.getItem());
        }
        else if (stack.getItem() == Items.SUGAR_CANE)
        {
            plantable = (SugarCaneBlock) Blocks.SUGAR_CANE;
        }
//        else
//        {
//            throw new NullPointerException("stack doesn't look plantable to me.");
//        }

        BlockState state = null;

        if(plantable != null)
        {
            state = FairyUtils.getPlant(plantable, world, new BlockPos(x,y,z));
        }
        else if (Block.byItem(stack.getItem()) instanceof CocoaBlock)
        {
            state = Block.byItem(stack.getItem()).defaultBlockState();
        }

        for ( int a = 0; a < 3; a++ )
        {
            //canPlaceBlockAt
            if (state != null && !state.is(Blocks.BAMBOO) && !state.is(Blocks.BAMBOO_SAPLING) && !state.is(ModBlockTags.IS_BERRY_BUSH_BLOCK) && !state.is(BlockTags.SAPLINGS) && world.getBlockState(new BlockPos(x,y,z)).canBeReplaced() && state.canSurvive(world, new BlockPos(x,y,z)))//state.getMaterial().isReplaceable()) // world.getBlockState(new BlockPos(x,y,z).above()).is(Blocks.AIR) && state.canSurvive(world, new BlockPos(x,y,z)))
            {

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": planting seed");

                world.setBlockAndUpdate(new BlockPos(x,y,z), state);
                stack.shrink(1);

                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

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
        return FairyUtils.isSeedItem(itemStack.getItem());
    }
}
