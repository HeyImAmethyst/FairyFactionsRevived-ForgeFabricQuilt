package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import com.google.common.collect.Maps;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Map;

public class JobShearing extends FairyJob
{
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) ->
    {
        p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
        p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
        p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
        p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });

    public JobShearing(FairyEntity fairy) {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        if(!canStart())
            return false;

        return (shearSheep(stack, world) || harvestHoneyComb(stack, x, y, z, world));
    }

    private boolean shearSheep(final ItemStack stack, final Level world )
    {
        final ArrayList<Sheep> sheep = FairyJobManager.INSTANCE.getSheep(world);
        FairyJobManager.INSTANCE.triedShearing = true;

        if ( sheep == null )
        {
            return false;
        }

        for (Object one_sheep_raw : sheep)
        {
            Sheep one_sheep = (Sheep) one_sheep_raw;
            if (one_sheep.readyForShearing())
            {
                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                fairy.attackAnim = 30;

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": shearing sheep");

                one_sheep.spawnAtLocation(ITEM_BY_DYE.get(one_sheep.getColor()), 1);
                one_sheep.setSheared(true);

                one_sheep.shear(SoundSource.NEUTRAL);

                break; // shear one at a time... looks better.
            }
        }

        return false;
    }

    private boolean harvestHoneyComb(final ItemStack stack, int x, final int y, int z, final Level world )
    {
        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( shearBeeHive( world, x, y, z) )
            {
                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

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

    private boolean shearBeeHive( final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        FairyJobManager.INSTANCE.triedShearing = true;

        if (state.is(BlockTags.BEEHIVES))
        {
            //FairyFactions.LOGGER.debug(this.fairy.toString()+": chopping wood");
            //world.destroyBlock(new BlockPos(x, y, z), true, fairy);
            BeehiveBlock beehiveBlock = (BeehiveBlock) block;

            if(CampfireBlock.isSmokeyPos(world, pos) && state.getValue(beehiveBlock.HONEY_LEVEL) >= 5)
            {
                beehiveBlock.dropHoneycomb(world, pos);
                beehiveBlock.resetHoneyLevel(world, state, pos);

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

    @Override
    public boolean canStart()
    {
        return !FairyJobManager.INSTANCE.triedShearing && FairyUtils.isShearingItem(itemStack);
    }
}
