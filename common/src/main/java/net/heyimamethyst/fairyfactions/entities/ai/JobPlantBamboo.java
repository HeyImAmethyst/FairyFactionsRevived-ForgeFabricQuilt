package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class JobPlantBamboo extends JobPlantVegitation
{
    public JobPlantBamboo(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {

        if(!canStart())
            return false;

        Block plantable;

        if (FairyUtils.isIPlantable( Block.byItem(stack.getItem()) ))
        {
            plantable = Block.byItem(stack.getItem());
        }
        else
        {
            throw new NullPointerException("stack doesn't look plantable to me.");
        }

        final BlockState state = FairyUtils.getPlant(plantable, world, new BlockPos(x,y,z));

        for ( int a = 0; a < 3; a++ )
        {
            //canPlaceBlockAt
            if (state.is(Blocks.BAMBOO) && world.getBlockState(new BlockPos(x,y,z)).getMaterial().isReplaceable() && state.canSurvive(world, new BlockPos(x,y,z)))//state.getMaterial().isReplaceable()) // world.getBlockState(new BlockPos(x,y,z).above()).is(Blocks.AIR) && state.canSurvive(world, new BlockPos(x,y,z)))
            {

                for (int l = -2; l < 3; l++)
                {
                    for (int i1 = -2; i1 < 3; i1++)
                    {
                        if (l == 0 && i1 == 0)
                        {
                            if (goodPlaceForPlant(x + l, y, z + i1, world) > 0)
                                return false;

                            BlockPos pos = new BlockPos(x + l, y, z + i1);
                            BlockState j1 = world.getBlockState(pos);

                            if (!j1.is(Blocks.AIR) && !j1.is(Blocks.TALL_GRASS))
                                return false;
                        }
                        else if (Math.abs(l) != 2 || Math.abs(i1) != 2)
                        {
                            boolean flag = false;
                            int k1 = -1;
                            while (k1 < 2)
                            {
                                int l1 = goodPlaceForPlant(x + l, y + k1, z + i1, world);
                                if (l1 == 2)
                                    return false;
                                if (l1 == 0)
                                {
                                    flag = true;
                                    break;
                                }
                                k1++;
                            }

                            if (!flag)
                                return false;
                        }
                    }
                }

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": planting sapling");

                world.setBlockAndUpdate(new BlockPos(x,y,z), Blocks.BAMBOO_SAPLING.defaultBlockState());
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
    public Block getPlant()
    {
        return Blocks.BAMBOO_SAPLING;
    }

    @Override
    public boolean canStart()
    {
        return FairyUtils.isBambooBlock(stack);
    }
}
