package net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;

public class MiscActionGatherFromBerryBush extends FairyMiscAction
{

    public MiscActionGatherFromBerryBush(FairyEntity fairy)
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

            if( harvestBerryBush( world, x, y, z) )
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

    private boolean harvestBerryBush(final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        //this.stateDefinition.any().setValue(AGE, 0)
        //state.is(ModBlockTags.IS_BERRY_BUSH_BLOCK)
        //state.is(Blocks.SWEET_BERRY_BUSH)
        //SweetBerryBushBlock.AGE

        if (state.is(ModBlockTags.IS_BERRY_BUSH_BLOCK))
        {
            IntegerProperty ageProperty = null;

            Collection<Property<?>> stateProperties = state.getProperties();

            for (Property<?> property: stateProperties)
            {
                if(property.getName() == "age")
                {
                    ageProperty = (IntegerProperty) property;
                }
            }

            if(ageProperty != null)
            {
                int i = state.getValue(ageProperty);
                boolean flag = i == 3;

                if (i > 1)
                {
                    int j = 1 + world.random.nextInt(2);
                    //block.popResource(world, pos, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
                    block.popResource(world, pos, new ItemStack(block.getCloneItemStack(world, pos, state).getItem(), j + (flag ? 1 : 0)));
                    world.playSound((Player)null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
                    world.setBlock(pos, state.setValue(ageProperty, Integer.valueOf(1)), 2);

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
