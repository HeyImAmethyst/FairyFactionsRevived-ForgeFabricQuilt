package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO: Not yet finished implementing, still WIP!!

public class JobMakePotion extends FairyJob
{
    //List<Potion> potionsToInsert = new ArrayList<>();

    ConcurrentLinkedQueue<Potion> potionsToInsert = new ConcurrentLinkedQueue<>();

    Potion currentPotion;

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

        for (int a = 0; a < 9; a++)
        {
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

                if(brewingStand.getItem(4).isEmpty())
                {
                    if (stack.is(Items.BLAZE_POWDER) && populateBlasePowder(brewingStand, stack))
                    {
                        return true;
                    }
                }
                else
                {
                    if (!stack.is(Items.BLAZE_POWDER) && !brewingStand.getItem(4).isEmpty())
                    {
                        int valid = 0;

                        for (int slotNumber = 0; slotNumber < 3; ++slotNumber)
                        {
                            if(brewingStand.getItem(slotNumber).isEmpty() && PotionBrewing.isBrewablePotion(PotionUtils.getPotion(stack)) && canPutPotionIn(brewingStand, stack))
                            {
                                if (!stack.is(Items.BLAZE_POWDER) && populatePotions(brewingStand, stack, slotNumber))
                                {
                                    return true;
                                }
                            }

                            if(!brewingStand.getItem(slotNumber).isEmpty() && PotionBrewing.isIngredient(stack))
                            {
                                if(checkPotion(brewingStand, stack, slotNumber))
                                {
                                    valid += 1;
                                }
                            }
                        }

                        if(valid == 3 && populateIngredient(brewingStand, stack))
                        {
                            return true;
                        }
                    }
                }

//                if (setStandForClearing(brewingStand))
//                {
//                    return true;
//                }
            }
        }

        return false;
    }

    private boolean populateBlasePowder(BrewingStandBlockEntity brewingStand, ItemStack stack)
    {
        if (stack.getCount() > 0)
        {
            ItemStack brewingStandStack = brewingStand.getItem(4);
            ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());

            if(brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
            {
                return false;
            }

            itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());

            brewingStand.setItem(4, itemStackForBrewingStand);

            return true;
        }

        return false;
    }

    private boolean populateIngredient(BrewingStandBlockEntity brewingStand, ItemStack stack)
    {
        //ItemStack itemFrameStack = FairyUtils.getItemFromItemFrameOnChest(fairy, chest, stack);
        //Potion itemFramePotion = PotionUtils.getPotion(itemFrameStack);

//        if(!itemFrameStack.isEmpty())
//        {
////            for (PotionBrewing.Mix<Potion> potionMix: PotionBrewing.POTION_MIXES)
////            {
////                if (ModExpectPlatform.getStartingPotionFromMix(potionMix) == currentPotion)
////                {
////                    ItemStack[] mixIngredientItems = potionMix.ingredient.getItems();
////
////                    if(PotionBrewing.isIngredient(stack) && stack == mixIngredientItems[0])
////                    {
////                        ItemStack brewingStandStack = brewingStand.getItem(3);
////                        ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
////
////                        if (brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
////                        {
////                            return false;
////                        }
////
////                        itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
////
////                        brewingStand.setItem(3, itemStackForBrewingStand);
////
////                        return true;
////                    }
////
////                }
////            }
//
//
//
//        }

        if(PotionBrewing.isIngredient(stack))
        {
//            if(PotionBrewing.hasMix(stack, brewingStand.getItem(0)))
//            {
//
//            }

            ItemStack brewingStandStack = brewingStand.getItem(3);
            ItemStack itemStackForBrewingStand = stack.copy();

            if (brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
            {
                return false;
            }

            itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());

            brewingStand.setItem(3, itemStackForBrewingStand);

            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean checkPotion(BrewingStandBlockEntity brewingStand, ItemStack stack, int slotNumber)
    {

        if(!brewingStand.getItem(slotNumber).isEmpty())
        {
            ItemStack brewingStandStack = brewingStand.getItem(slotNumber);

            if(PotionBrewing.hasMix(stack, brewingStandStack))
            {
//                for (PotionBrewing.Mix<Potion> potionMix: PotionBrewing.POTION_MIXES)
//                {
//                    if (ModExpectPlatform.getStartingPotionFromMix(potionMix) == PotionUtils.getPotion(brewingStandStack))
//                    {
//                        return true;
//                    }
//                }

                return true;
            }
        }

        return false;
    }

    private void findPotionToInsert(BrewingStandBlockEntity brewingStand, Potion potion,ItemStack stack)
    {
        for (PotionBrewing.Mix<Potion> potionMix: PotionBrewing.POTION_MIXES)
        {
            if (ModExpectPlatform.getEndPotionFromMix(potionMix) == potion)
            {
                Potion startingPotion = ModExpectPlatform.getStartingPotionFromMix(potionMix);

                ItemStack normalPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), startingPotion);
                ItemStack splashPotion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), startingPotion);
                ItemStack lingeringPotion = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), startingPotion);

                if(ItemStack.isSameItemSameTags(stack, normalPotion))
                {
                    potionsToInsert.add(PotionUtils.getPotion(normalPotion));
                    System.out.println("Added " + normalPotion.getDisplayName().getString() + " to list");

                    return;
                }
                else if(ItemStack.isSameItemSameTags(stack, splashPotion))
                {
                    potionsToInsert.add(PotionUtils.getPotion(splashPotion));
                    System.out.println("Added " + splashPotion.getDisplayName().getString() + " to list");

                    return;
                }
                else if (ItemStack.isSameItemSameTags(stack, lingeringPotion))
                {
                    potionsToInsert.add(PotionUtils.getPotion(lingeringPotion));
                    System.out.println("Added " + lingeringPotion.getDisplayName().getString() + " to list");

                    return;
                }
                else
                {
                    if(doesChestHaveItem(chest, Items.POTION) || doesChestHaveItem(chest, Items.SPLASH_POTION) || doesChestHaveItem(chest, Items.LINGERING_POTION))
                    {
                        if(ItemStack.isSameItemSameTags(stack, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER))
                            || ItemStack.isSameItemSameTags(stack, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER))
                            || ItemStack.isSameItemSameTags(stack, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.WATER)))
                        {
                            potionsToInsert.add(PotionUtils.getPotion(stack));
                            System.out.println("Added " + stack.getDisplayName().getString() + "to list");

                            return;
                        }
                        else
                        {
                            //System.out.println("Stack " + stack.getDisplayName().getString() + " does not match potion " + startingPotion.getName(normalPotion.getDescriptionId() + ".effect.") + ". Recursing" );
                            System.out.println("Stack " + stack.getDisplayName().getString() + " does not match potion " + normalPotion.getDisplayName().getString() + ". Recursing" );

                            findPotionToInsert(brewingStand, startingPotion, stack);
                        }
                    }
                }
            }
        }
    }

    private boolean populatePotions(BrewingStandBlockEntity brewingStand, ItemStack stack, int slotNumber)
    {
        //ItemStack itemFrameStack = FairyUtils.getItemFromItemFrameOnChest(fairy, chest, stack);
        Potion potion = PotionUtils.getPotion(stack);
        
        if(/*!itemFrameStack.isEmpty() && itemFramePotion != null &&*/ potion != null  && !PotionBrewing.isIngredient(stack))
        {
            findPotionToInsert(brewingStand, potion, stack);

            System.out.println(potionsToInsert.size());

//            for (Potion potion: potionsToInsert)
//            {
//                currentPotion = potion;
//
//                for (PotionBrewing.Mix<Potion> potionMix: PotionBrewing.POTION_MIXES)
//                {
//                    if (ModExpectPlatform.getStartingPotionFromMix(potionMix) == potion)
//                    {
//                        ItemStack normalPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
//                        ItemStack splashPotion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
//                        ItemStack lingeringPotion = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
//
//                        if(stack == normalPotion
//                                || stack == splashPotion
//                                || stack == lingeringPotion)
//                        {
//                            ItemStack brewingStandStack = brewingStand.getItem(slotNumber);
//                            ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
//
//                            if (brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
//                            {
//                                return false;
//                            }
//
//                            itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
//                            brewingStand.setItem(slotNumber, itemStackForBrewingStand);
//
//                            return true;
//                        }
//                    }
//                }
//
//                potionsToInsert.remove(potion);
//            }
            if(potionsToInsert != null)
            {
                currentPotion = potionsToInsert.remove();

                if(currentPotion != null && PotionUtils.getPotion(stack) == currentPotion)
                {

                    ItemStack prevBrewingStandStack = null;

                    if(slotNumber != 0)
                    {
                        prevBrewingStandStack = brewingStand.getItem(slotNumber - 1);
                    }

                    if(prevBrewingStandStack != null)
                    {
                        if(ItemStack.isSameItemSameTags(stack, prevBrewingStandStack))
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }

                    ItemStack brewingStandStack = brewingStand.getItem(slotNumber);
                    //ItemStack itemStackForBrewingStand = new ItemStack(stack.getItem());
                    ItemStack itemStackForBrewingStand = stack.copy();

                    if (brewingStandStack.getCount() == brewingStandStack.getMaxStackSize())
                    {
                        return false;
                    }

                    itemStackForBrewingStand.setCount(brewingStandStack.getCount() + stack.getCount());
                    brewingStand.setItem(slotNumber, itemStackForBrewingStand);

                    return true;
                }
            }
        }
        else
        {
            return false;
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
        else if (!PotionBrewing.isIngredient(itemStack))
        {
            return false;
        }
        else if(!(brewingStand.getItem(3) == ItemStack.EMPTY))
        {
            return false;
        }

        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 3; ++i)
        {
            ItemStack itemStack2 = brewingStand.getItem(i);
            if (!itemStack2.isEmpty() || PotionBrewing.hasMix(itemStack2, itemStack))
            {
                items.add(itemStack2);
            }
        }

        return items.size() == 3;
    }

    private static boolean canPutPotionIn(BrewingStandBlockEntity brewingStand, ItemStack itemStack)
    {
        if (itemStack.isEmpty())
        {
            return false;
        }
        else if (!FairyUtils.isPotionContainer(itemStack))
        {
            return false;
        }

        for (int i = 0; i < 3; ++i)
        {
            ItemStack itemStack2 = brewingStand.getItem(i);
            if (itemStack2 != ItemStack.EMPTY) continue;
            return true;
        }

        ItemStack itemStack2 = brewingStand.getItem(3);
        return itemStack2.isEmpty() || !PotionBrewing.hasMix(itemStack2, itemStack);
    }

    @Override
    public boolean canStart()
    {
        return super.canStart();
    }
}
