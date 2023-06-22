package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

//TODO: Not yet finished implementing, still WIP!!

public class JobSmelt extends FairyJob
{

    public JobSmelt(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        return smelt(stack, x, y, z, world);
    }

    private boolean smelt(final ItemStack stack, int x, final int y, int z, final Level world )
    {
        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( assesBlastFurnace( stack, world, x, y, z) )
            {
                stack.shrink(1);
                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

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

    private boolean assesBlastFurnace(final ItemStack stack, final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof BlastFurnaceBlock )
        {
            //FairyFactions.LOGGER.debug(this.fairy.toString()+": chopping wood");
            //world.destroyBlock(new BlockPos(x, y, z), true, fairy);
            BlastFurnaceBlock beehiveBlock = (BlastFurnaceBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if ( blockEntity != null && blockEntity instanceof BlastFurnaceBlockEntity)
            {
                BlastFurnaceBlockEntity blastFurnace = (BlastFurnaceBlockEntity) blockEntity;

                Map<Item, Integer> fuels = BlastFurnaceBlockEntity.getFuel();

                if(fuels.containsKey(stack.getItem()))
                {
                    if (stack != null && stack.getCount() > 0)
                    {
                        ItemStack furnaceStack = blastFurnace.getItem(1);

                        if (furnaceStack == null)
                        {
                            blastFurnace.setItem(1, stack);

                            return true;
                        }
                        else
                        {
                            assert stack.getItem() == furnaceStack.getItem(); // avoid duplication glitch?
                            assert stack.getCount() + furnaceStack.getCount() < furnaceStack.getMaxStackSize();

                            furnaceStack.setCount(furnaceStack.getCount() + stack.getCount());

                            return true;
                        }
                    }

                }

//                    if(!(fuels.containsKey(stack.getItem())))
//                    {
//                        if (stack != null && stack.getCount() > 0)
//                        {
//                            ItemStack furnaceStack = blastFurnace.getItem(0);
//
//                            if (furnaceStack == null)
//                            {
//                                blastFurnace.setItem(0, stack);
//                            }
//                            else
//                            {
//                                assert stack.getItem() == furnaceStack.getItem(); // avoid duplication glitch?
//                                assert stack.getCount() + furnaceStack.getCount() < furnaceStack.getMaxStackSize();
//
//                                furnaceStack.setCount(furnaceStack.getCount() + stack.getCount());
//                            }
//                        }
//
//                        return true;
//                    }

//                    if(!blastFurnace.getItem(2).isEmpty())
//                    {
//                        BlastFurnaceBlock.popResource(world, blastFurnace.getBlockPos(), blastFurnace.getItem(2));
//
//                        //FairyJobManager.INSTANCE.cleanSlotInBlastFurnace(blastFurnace, 2);
//                        blastFurnace.setItem( 2, (ItemStack) null );
//
//                        return true;
//                    }

//                    if(!world.isClientSide)
//                    {
//                        Recipe recipe = world.getRecipeManager().getRecipeFor(blastFurnace.recipeType, blastFurnace, world).orElse(null);
//                        int maxStackSize = blastFurnace.getMaxStackSize();
//
//                        if(canBurn(world.registryAccess(), recipe, blastFurnace, maxStackSize))
//                        {
//                            blastFurnace.setItem(0, stack);
//                            return true;
//                        }
//                    }

            }
            else
            {
                return false;
            }
        }

        return false;
    }

    private boolean canBurn(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, AbstractFurnaceBlockEntity entity, int i)
    {

        if (entity.getItem(0).isEmpty() || recipe == null)
        {
            return false;
        }

        ItemStack itemStack = recipe.getResultItem(registryAccess);

        if (itemStack.isEmpty())
        {
            return false;
        }

        ItemStack itemStack2 = entity.getItem(2);

        if (itemStack2.isEmpty())
        {
            return true;
        }

        if (!ItemStack.isSameItem(itemStack2, itemStack))
        {
            return false;
        }

        if (itemStack2.getCount() < i && itemStack2.getCount() < itemStack2.getMaxStackSize())
        {
            return true;
        }

        return itemStack2.getCount() < itemStack.getMaxStackSize();
    }

    @Override
    public boolean canStart()
    {
        return false;
    }
}
