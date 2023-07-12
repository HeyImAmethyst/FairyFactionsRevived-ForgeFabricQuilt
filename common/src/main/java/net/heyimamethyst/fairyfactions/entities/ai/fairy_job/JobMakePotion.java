package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import com.google.common.collect.ImmutableSet;
import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

//TODO: Not yet finished implementing, still WIP!!

public class JobMakePotion extends FairyJob
{

    public JobMakePotion(FairyEntity fairy)
    {
        super(fairy);
    }

    @Override
    public boolean canRun(ItemStack stack, int x, int y, int z, Level world)
    {
        return makePotion(stack, x, y, z, world);
    }

    private boolean makePotion(final ItemStack stack, int x, final int y, int z, final Level world)
    {
        if(!canStart())
            return false;

        final int m = x;
        final int n = z;

        for (int a = 0; a < 9; a++) {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if (assesPotionStand(stack, world, x, y, z))
            {
                stack.shrink(1);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.getItem());

                fairy.attackAnim = 30;

                if (!fairy.flymode() && fairy.getFlyTime() > 0)
                {
                    fairy.setFlyTime(0);
                }

                return true;
            }
        }

        return false;
    }

    private boolean assesPotionStand(final ItemStack stack, final Level world, final int x, final int y, final int z)
    {
        final BlockPos pos = new BlockPos(x, y, z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if (state.getBlock() instanceof BrewingStandBlock)
        {
            BrewingStandBlock brewingStandBlock = (BrewingStandBlock) block;

            final BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity != null && blockEntity instanceof BrewingStandBlockEntity)
            {
                BrewingStandBlockEntity brewingStand = (BrewingStandBlockEntity) blockEntity;

                fairy.getNavigation().moveTo(x + 0.5, y, z + 0.5, 0.3D);

                if (populateBlasePowder(brewingStand, stack))
                {
                    return true;
                }

//                if (populateWaterBottles(brewingStand, stack))
//                {
//                    return true;
//                }

                if (populateInput(brewingStand, stack))
                {
                    return true;
                }

                if (setStandForClearing(brewingStand))
                {

                    return true;
                }
            }
        }

        return false;
    }

    private boolean populateBlasePowder(BrewingStandBlockEntity brewingStand, ItemStack stack)
    {
        if(stack.is(Items.BLAZE_POWDER))
        {
            if (stack.getCount() > 0)
            {
                ItemStack brewingStandStack = brewingStand.getItem(1);
                ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());

                if(brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
                {
                    return false;
                }

                itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());

                brewingStand.setItem(4, itemStackForBrewingStand);

                return true;
            }
        }

        return false;
    }

//    private boolean populateWaterBottles(BrewingStandBlockEntity brewingStand, ItemStack stack)
//    {
//
//        if(stack.is(Items.POTION) && (PotionUtils.getPotion(stack) == Potions.WATER || PotionUtils.getPotion(stack) == Potions.AWKWARD || PotionUtils.getPotion(stack) == Potions.MUNDANE))
//        {
//
//            for (int i = 0; i < 3; ++i)
//            {
//                ItemStack brewingStandStack = brewingStand.getItem(i);
//                ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
//
//                if(brewingStandStack.isEmpty())
//                {
//                    brewingStand.setItem(i, PotionUtils.setPotion(new ItemStack(stack.getItem()), PotionUtils.getPotion(stack)));
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private boolean populateInput(BrewingStandBlockEntity brewingStand, ItemStack stack)
    {
        List<PotionBrewing.Mix<Potion>> potionMixes =  PotionBrewing.POTION_MIXES;
        List<PotionBrewing.Mix<Item>> containerMixes =  PotionBrewing.CONTAINER_MIXES;

        NonNullList<ItemStack> chestItems = chest.getItems();

//        for (PotionBrewing.Mix<Potion> potionMix : potionMixes)
//        {
//
////            if(isPotionContainer(stack.getItem()))
////            {
////                if(ModExpectPlatform.getStartingPotionFromMix(potionMix) != null)
////                {
////                    if(PotionUtils.getPotion(stack) == ModExpectPlatform.getStartingPotionFromMix(potionMix))
////                    {
////                        for (int j = 0; j < 3; ++j)
////                        {
////                            ItemStack brewingStandStack = brewingStand.getItem(j);
////                            ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
////
////                            if(brewingStandStack.isEmpty())
////                            {
////                                brewingStand.setItem(j, PotionUtils.setPotion(itemStackForBrewingStand, PotionUtils.getPotion(stack)));
////                                return true;
////                            }
////                        }
////                    }
////                }
////            }
//
//            if(Ingredient.of(stack) == potionMix.ingredient)
//            {
//                ItemStack brewingStandStack = brewingStand.getItem(3);
//                ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
//
//                if(brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
//                {
//                    return false;
//                }
//
//                itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
//
//                brewingStand.setItem(3, itemStackForBrewingStand);
//
//                return true;
//            }
//        }

//        for (PotionBrewing.Mix<Item> containerMix : containerMixes)
//        {
//
////            if(isPotionContainer(stack.getItem()))
////            {
////                if(ModExpectPlatform.getStartingContainerFromMix(containerMix) != null)
////                {
////                    if(stack.getItem() == ModExpectPlatform.getStartingContainerFromMix(containerMix))
////                    {
////                        for (int j = 0; j < 3; ++j)
////                        {
////                            ItemStack brewingStandStack = brewingStand.getItem(j);
////                            ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
////
////                            if(brewingStandStack.isEmpty())
////                            {
////                                brewingStand.setItem(j, PotionUtils.setPotion(itemStackForBrewingStand, PotionUtils.getPotion(stack)));
////                                return true;
////                            }
////                        }
////                    }
////                }
////            }
//
//            if(Ingredient.of(stack) == containerMix.ingredient)
//            {
//                ItemStack brewingStandStack = brewingStand.getItem(3);
//                ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
//
//                if(brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
//                {
//                    return false;
//                }
//
//                itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
//
//                brewingStand.setItem(3, itemStackForBrewingStand);
//
//                return true;
//            }
//        }

        if(canPutPotionIn(brewingStand, stack))
        {
            for (int j = 0; j < 3; ++j)
            {
                ItemStack brewingStandStack = brewingStand.getItem(j);
                ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());

                if(!brewingStandStack.isEmpty())
                {
                    return false;
                }

                brewingStand.setItem(j, PotionUtils.setPotion(itemStackForBrewingStand, PotionUtils.getPotion(stack)));
            }
        }

        if(canPutIngredientIn(brewingStand, stack))
        {
            ItemStack brewingStandStack = brewingStand.getItem(3);
            ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());

            if(brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
            {
                return false;
            }
            else
            {
                itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
                brewingStand.setItem(3, itemStackForBrewingStand);

                return true;
            }
        }

        return false;
    }

    private boolean setStandForClearing(BrewingStandBlockEntity brewingStandBlockEntity)
    {
        if(!BrewingStandBlockEntity.isBrewable(brewingStandBlockEntity.items))
        {
            FairyJobManager.INSTANCE.setBrewingStandForClearing = true;

            return true;
        }

        return false;
    }

    private static boolean canPutIngredientIn(BrewingStandBlockEntity brewingStand, ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return false;
        }

        if (!PotionBrewing.isIngredient(itemStack))
        {
            return false;
        }

        if(!brewingStand.getItem(3).isEmpty())
        {
            return false;
        }

        for (int i = 0; i < 3; ++i)
        {
            ItemStack itemStack2 = brewingStand.getItem(i);
            if (itemStack2.isEmpty() || !PotionBrewing.hasMix(itemStack2, itemStack)) continue;
            return true;
        }

        return false;
    }

    private static boolean canPutPotionIn(BrewingStandBlockEntity brewingStand, ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return false;
        }

        if (!FairyUtils.isPotionContainer(itemStack))
        {
            return false;
        }

        for (int i = 0; i < 3; ++i)
        {
            ItemStack itemStack2 = brewingStand.getItem(i);
            if (!itemStack2.isEmpty()) continue;
            return true;
        }

        ItemStack itemStack2 = brewingStand.getItem(3);
        if (itemStack2.isEmpty() || !PotionBrewing.hasMix(itemStack2, itemStack))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean canStart()
    {
        return super.canStart();
    }
}
