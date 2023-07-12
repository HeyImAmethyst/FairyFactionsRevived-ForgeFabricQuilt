package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class JobPlantVegitation extends FairyJob
{
    public JobPlantVegitation(FairyEntity fairy)
    {
        super(fairy);
    }

    public int goodPlaceForPlant( final int x, final int y, final int z, final Level world)
    {
        return placePlantWithSpace(x, y, z, world, getPlant());
    }

    public int goodPlaceForPlantTag( final int x, final int y, final int z, final Level world)
    {
        return placePlantTagWithSpace(x, y, z, world, getPlantTag());
    }

    public int placePlantWithSpace(final int x, final int y, final int z, final Level world, Block blockToCheck)
    {
//        int saplingRadius = 4;
//
//        int i, j, k;
//
//        for ( int a = -saplingRadius; a <= saplingRadius; a++ )
//        {
//            for (int c = -saplingRadius; c <= saplingRadius; c++)
//            {
//                i = x + a;
//                j = y;
//                k = z + c;
//
//                BlockPos pos = new BlockPos(i,j,k);
//                BlockState state = world.getBlockState(pos);
//
//                if(state.is(BlockTags.SAPLINGS))
//                {
//                    return 2;
//                }
//
//                BlockState stateAbove = world.getBlockState(new BlockPos(i, j + 1, k));
//
//                if ( stateAbove.is(BlockTags.SAPLINGS))
//                {
//                    return 2;
//                }
//
//                if (stateAbove.is(Blocks.AIR) && world.canSeeSky(new BlockPos( i, j + 1, k )) )
//                {
//                    return 0;
//                }
//            }
//        }

        BlockPos pos = new BlockPos(x,y,z);
        BlockState state = world.getBlockState(pos);

//        BlockPos north = pos.north();
//        BlockPos south = pos.south();
//        BlockPos east = pos.east();
//        BlockPos west = pos.west();

        BlockPos north = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos south = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos east = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos west = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());

        BlockState northState = world.getBlockState(north);
        BlockState southState = world.getBlockState(south);
        BlockState eastState = world.getBlockState(east);
        BlockState westState = world.getBlockState(west);

        if(state.is(blockToCheck) && (!northState.is(blockToCheck)
                && !southState.is(blockToCheck)
                && !eastState.is(blockToCheck)
                && !westState.is(blockToCheck)))
        {
            return 2;
        }

        BlockPos posAbove = new BlockPos(x, y + 1, z);
        BlockState stateAbove = world.getBlockState(posAbove);

        BlockPos posBelow = new BlockPos(x, y - 1, z);
        BlockState stateBelow = world.getBlockState(posBelow);

//        BlockPos northAbove = posAbove.north();
//        BlockPos southAbove = posAbove.south();
//        BlockPos eastAbove = posAbove.east();
//        BlockPos westAbove = posAbove.west();

        BlockPos northAbove = new BlockPos(posAbove.getX(), posAbove.getY(), posAbove.getZ() - 1);
        BlockPos southAbove = new BlockPos(posAbove.getX(), posAbove.getY(), posAbove.getZ() + 1);
        BlockPos eastAbove = new BlockPos(posAbove.getX() + 1, posAbove.getY(), posAbove.getZ());
        BlockPos westAbove = new BlockPos(posAbove.getX() - 1, posAbove.getY(), posAbove.getZ());

        BlockState northStateAbove = world.getBlockState(northAbove);
        BlockState southStateAbove = world.getBlockState(southAbove);
        BlockState eastStateAbove = world.getBlockState(eastAbove);
        BlockState westStateAbove = world.getBlockState(westAbove);

        if ( stateAbove.is(blockToCheck) && (!northStateAbove.is(blockToCheck)
                && !southStateAbove.is(blockToCheck)
                && !eastStateAbove.is(blockToCheck)
                && !westStateAbove.is(blockToCheck)))
        {
            return 2;
        }

        if (stateAbove.is(Blocks.AIR) && world.canSeeSky(new BlockPos( x, y + 1, z )) && !stateBelow.is(blockToCheck))
        {
            return 0;
        }

        return 1;
        //return (!stateAbove.is(Blocks.AIR) || !world.canSeeSky(new BlockPos( x, y + 1, z ))) ? 1 : 0;
        //return canPlaceSapling(x, y + 1, z, j, world) ? 1 : 0;
    }

    public int placePlantTagWithSpace(final int x, final int y, final int z, final Level world, TagKey blockToCheck)
    {
//        int saplingRadius = 4;
//
//        int i, j, k;
//
//        for ( int a = -saplingRadius; a <= saplingRadius; a++ )
//        {
//            for (int c = -saplingRadius; c <= saplingRadius; c++)
//            {
//                i = x + a;
//                j = y;
//                k = z + c;
//
//                BlockPos pos = new BlockPos(i,j,k);
//                BlockState state = world.getBlockState(pos);
//
//                if(state.is(BlockTags.SAPLINGS))
//                {
//                    return 2;
//                }
//
//                BlockState stateAbove = world.getBlockState(new BlockPos(i, j + 1, k));
//
//                if ( stateAbove.is(BlockTags.SAPLINGS))
//                {
//                    return 2;
//                }
//
//                if (stateAbove.is(Blocks.AIR) && world.canSeeSky(new BlockPos( i, j + 1, k )) )
//                {
//                    return 0;
//                }
//            }
//        }

        BlockPos pos = new BlockPos(x,y,z);
        BlockState state = world.getBlockState(pos);

//        BlockPos north = pos.north();
//        BlockPos south = pos.south();
//        BlockPos east = pos.east();
//        BlockPos west = pos.west();

        BlockPos north = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos south = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos east = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos west = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());

        BlockState northState = world.getBlockState(north);
        BlockState southState = world.getBlockState(south);
        BlockState eastState = world.getBlockState(east);
        BlockState westState = world.getBlockState(west);

        if(state.is(blockToCheck) && (!northState.is(blockToCheck)
                && !southState.is(blockToCheck)
                && !eastState.is(blockToCheck)
                && !westState.is(blockToCheck)))
        {
            return 2;
        }

        BlockPos posAbove = new BlockPos(x, y + 1, z);
        BlockState stateAbove = world.getBlockState(posAbove);

        BlockPos posBelow = new BlockPos(x, y - 1, z);
        BlockState stateBelow = world.getBlockState(posBelow);

//        BlockPos northAbove = posAbove.north();
//        BlockPos southAbove = posAbove.south();
//        BlockPos eastAbove = posAbove.east();
//        BlockPos westAbove = posAbove.west();

        BlockPos northAbove = new BlockPos(posAbove.getX(), posAbove.getY(), posAbove.getZ() - 1);
        BlockPos southAbove = new BlockPos(posAbove.getX(), posAbove.getY(), posAbove.getZ() + 1);
        BlockPos eastAbove = new BlockPos(posAbove.getX() + 1, posAbove.getY(), posAbove.getZ());
        BlockPos westAbove = new BlockPos(posAbove.getX() - 1, posAbove.getY(), posAbove.getZ());

        BlockState northStateAbove = world.getBlockState(northAbove);
        BlockState southStateAbove = world.getBlockState(southAbove);
        BlockState eastStateAbove = world.getBlockState(eastAbove);
        BlockState westStateAbove = world.getBlockState(westAbove);

        if ( stateAbove.is(blockToCheck) && (!northStateAbove.is(blockToCheck)
                && !southStateAbove.is(blockToCheck)
                && !eastStateAbove.is(blockToCheck)
                && !westStateAbove.is(blockToCheck)))
        {
            return 2;
        }

        if (stateAbove.is(Blocks.AIR) && world.canSeeSky(new BlockPos( x, y + 1, z )) && !stateBelow.is(blockToCheck))
        {
            return 0;
        }

        return 1;
        //return (!stateAbove.is(Blocks.AIR) || !world.canSeeSky(new BlockPos( x, y + 1, z ))) ? 1 : 0;
        //return canPlaceSapling(x, y + 1, z, j, world) ? 1 : 0;
    }

    public TagKey getPlantTag()
    {
        return null;
    }

    public Block getPlant()
    {
        return null;
    }
}
