package net.heyimamethyst.fairyfactions.entities.ai.fairy_job;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_misc_action.*;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//Parts of chest storing code came from : https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/common/entity/goal/carbuncle/StarbyTransportBehavior.java

public class FairyJobManager
{
    public static FairyJobManager INSTANCE;

    // Will be referenced a lot. It's the fairy who's doing the job
    private final FairyEntity fairy;

    // Used to make sure fairies don't just tear apart random trees.
    public boolean				doHaveAxe;

    // Make sure that breeding is only attempted once per chest.
    public boolean				triedBreeding;

    // Make sure that shearing is only attempted once per chest.
    public boolean				triedShearing;
    public boolean				canRequestFood;
    public boolean              setBrewingStandForClearing;

    // A list of items on the ground.
    private ArrayList<ItemEntity> goodies;

    public List<FairyJob> fairyJobs = new ArrayList<>();
    public List<FairyMiscAction> fairyMiscActions = new ArrayList<>();

    public List<FairyMiscAction> fairyBasicMiscActions = new ArrayList<>();

    public FairyJobManager(final FairyEntity entityfairy )
    {
        fairy = entityfairy;
        INSTANCE = this;

        createFairyJobs();
        createFairyMiscActions();
    }

    public void createFairyJobs()
    {
        fairyJobs.add(new JobTillLand(fairy));
        fairyJobs.add(new JobPlantSeed(fairy));
        fairyJobs.add(new JobBonemeal(fairy));
        fairyJobs.add(new JobForesting(fairy));
        fairyJobs.add(new JobPlantSapling(fairy));
        fairyJobs.add(new JobPlantBerryBush(fairy));
        fairyJobs.add(new JobPlantBamboo(fairy));
        fairyJobs.add(new JobBreeding(fairy));
        fairyJobs.add(new JobShearing(fairy));
        fairyJobs.add(new JobFishing(fairy));
        fairyJobs.add(new JobSmelt(fairy));
        fairyJobs.add(new JobCook(fairy));
        fairyJobs.add(new JobMakePotion(fairy));
        fairyJobs.add(new JobButcher(fairy));
    }

    public void createFairyMiscActions()
    {
        fairyMiscActions.add(new MiscActionCutTallGrass(fairy));
        fairyMiscActions.add(new MiscActionGatherFromBerryBush(fairy));
        fairyMiscActions.add(new MiscActionGatherProductFromFurnace(fairy));
        fairyMiscActions.add(new MiscActionGatherProductFromBrewingStand(fairy));
        fairyMiscActions.add(new MiscActionTrimExcessLeaves(fairy));

        fairyBasicMiscActions.add(new MiscActionBecomeEmotional(fairy));
    }

    public void discover( final Level world )
    {
        if ( fairy.getTarget() != null || fairy.getEntityFear() != null || fairy.getCryTime() > 0
                 || fairy.getHealth() <= 0 )
        {
            return;
        }

        final int x = (int)Math.floor( fairy.position().x );
        int y = (int)Math.floor( fairy.getBoundingBox().minY );

        if ( fairy.flymode() )
        {
            y--;
        }

        final int z = (int)Math.floor( fairy.position().z );

        if ( y < 0 || y >= world.getHeight() )
        {
            return;
        }

        goodies = getGoodies( world );

        //getNearbyChest( x, y, z, world );

        if(fairy.getDeliveryMode())
        {
            prepareToDeliver(world);
        }
        else if (!fairy.getDeliveryMode())
        {
            if(fairy.getPostChestLocation().isPresent())
                prepareToWork( x, y, z, world );
        }
    }

    public void sittingFishing( final Level world )
    {
        if ( fairy.getTempItem() != null || fairy.getEntityFear() != null || fairy.getCryTime() > 0
                 || fairy.getHealth() <= 0 )
        {
            return;
        }

        final int x = (int) Math.floor( fairy.position().x );
        int y = (int)Math.floor( fairy.getBoundingBox().minY );

        if ( fairy.flymode() )
        {
            y--;
        }

        final int z = (int)Math.floor( fairy.position().z );

        if ( y < 0 || y >= world.getHeight() )
        {
            return;
        }

        //getNearbyChest3( x, y, z, world );

        //getNearbyChest( x, y, z, world );

        if(fairy.getPostChestLocation().isPresent())
            prepareToFish(x, y, z, world);
    }

    public static final int radius = 5;

//    private void getNearbyChest(final int x, final int y, final int z, final Level world)
//    {
//        int i, j, k;
//
//        if(fairy.getPostChestLocation().isEmpty())
//        {
//            for ( int a = -radius; a <= radius; a++ )
//            {
//                for ( int b = -2; b <= 2; b++ )
//                {
//                    for ( int c = -radius; c <= radius; c++ )
//                    {
//                        i = x + a;
//                        j = y + b;
//                        k = z + c;
//
//                        final BlockPos pos = new BlockPos(i, j, k);
//
//                        if ( world.getBlockState(pos).getBlock() instanceof ChestBlock )
//                        {
//                            final BlockEntity blockEntity = world.getBlockEntity(pos);
//
//                            if ( blockEntity != null && blockEntity instanceof ChestBlockEntity)
//                            {
//                                fairy.setPostChestLocation(pos);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        else if(fairy.getPostChestLocation().isPresent())
//        {
//            BlockPos pos = fairy.getPostChestLocation().get();
//
//            if ( !(world.getBlockState(pos).getBlock() instanceof ChestBlock) )
//            {
//                fairy.clearPostChestLocation();
//            }
//            else
//            {
//                return;
//            }
//
//        }
//
//    }

    private void prepareToWork(final int x, final int y, final int z, final Level world)
    {
        if(fairy.getPostChestLocation().isPresent())
        {
            BlockPos pos = fairy.getPostChestLocation().get();

            if ( world.getBlockState(pos).getBlock() instanceof ChestBlock )
            {
                final BlockEntity blockEntity = world.getBlockEntity(pos);

                if ( blockEntity != null && blockEntity instanceof ChestBlockEntity)
                {
                    ChestBlockEntity chest = (ChestBlockEntity) blockEntity;

                    if ( goodies != null && collectGoodies( chest, world ) )
                    {
                        fairy.postedCount = 2;
                        return;
                    }

                    for ( int p = 0; p < chest.getContainerSize(); p++ )
                    {
                        if ( checkChestItem(chest, p, x, y, z, world ) )
                        {
                            cleanSlotInChest(chest, p );
                            fairy.postedCount = 2;
                            return;
                        }
                    }

                    if ( miscActions(chest, x, y, z, world ) )
                    {
                        fairy.postedCount = 2;
                        return;
                    }

                    if ( basicMiscActions(chest, x, y, z, world ) )
                    {
                        fairy.postedCount = 2;
                        return;
                    }
                }
            }
        }
    }

    private void prepareToFish(final int x, final int y, final int z, final Level world)
    {
        if(fairy.getPostChestLocation().isPresent())
        {
            BlockPos pos = fairy.getPostChestLocation().get();

            if ( world.getBlockState(pos).getBlock() instanceof ChestBlock )
            {
                final BlockEntity tent = world.getBlockEntity(pos);

                if ( tent != null && tent instanceof ChestBlockEntity )
                {
                    triedBreeding = false;
                    triedShearing = false;
                    final ChestBlockEntity chest = (ChestBlockEntity) tent;

                    for ( int p = 0; p < chest.getContainerSize(); p++ )
                    {
                        final ItemStack stack = chest.getItem( p );

                        JobFishing fishingJob = (JobFishing) fairyJobs.get(9);

                        if ( stack != null && FairyUtils.isFishingItem( stack )
                                &&  fishingJob.canRun( stack, x, y, z, world ) )
                        {
                            cleanSlotInChest( chest, p );
                            fairy.postedCount = 2;
                            return;
                        }
                    }
                }
            }
        }

    }

    private void prepareToDeliver(final Level world)
    {
        boolean chance = FairyUtils.percentChance(fairy, fairy.isEmotional() ? 0.75 : 1.0);

        if(chance)
        {
            if(fairy.getHeldItem() == null || fairy.getHeldItem().isEmpty())
            {
                BlockPos takeChestDestination = getTakeChestDestination();

                if(takeChestDestination == null)
                {
                    return;
                }

                takeItemFromTakeChest(takeChestDestination, world);
            }

            if (fairy.getHeldItem() != null || !fairy.getHeldItem().isEmpty())
            {
                BlockPos storeChestDestination = getStoreChestDestination(fairy.getHeldItem());

                if(storeChestDestination == null)
                {
                    return;
                }

                storeItemInStoreChest(storeChestDestination, fairy.getHeldItem(), world);
            }
        }
        else
        {
            return;
        }
    }

    private void takeItemFromTakeChest(BlockPos takeChestDestination, Level world)
    {
        ItemStack stackToDeliver;

        if ( world.getBlockState(takeChestDestination).getBlock() instanceof ChestBlock )
        {
            final BlockEntity blockEntity = world.getBlockEntity(takeChestDestination);

            if ( blockEntity != null && blockEntity instanceof ChestBlockEntity)
            {
                ChestBlockEntity takeChest = (ChestBlockEntity) blockEntity;

                if ( basicMiscActions(takeChest, fairy.getBlockX(), fairy.getBlockY(), fairy.getBlockZ(), world ) )
                {
                    fairy.postedCount = 2;
                    return;
                }
                else
                {
                    for ( int p = 0; p < takeChest.getContainerSize(); p++ )
                    {

                        ItemStack stack = takeChest.getItem(p);

                        //&& FairyUtils.doesItemMatchItemInFrameOnChest(fairy, takeChest, stack)
                        if(!stack.isEmpty() && FairyUtils.doesItemMatchItemInFrameOnChest(fairy, takeChest, stack))
                        {

                            fairy.getNavigation().moveTo((double)takeChestDestination.getX() + 0.5, takeChestDestination.getY() + 1, (double)takeChestDestination.getZ() + 0.5, 0.3D);

                            if(fairy.blockPosition().closerThan(takeChestDestination, 3D))
                            {
                                fairy.armSwing( !fairy.didSwing );
                                fairy.setHeldItem(new ItemStack(stack.getItem(), stack.getCount()));
                                //fairy.setTempItem(stack.getItem());

                                //cleanSlotInChest(takeChest, p );

                                takeChest.removeItem(p, stack.getCount());

                                fairy.postedCount = 2;

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void storeItemInStoreChest(BlockPos storeChestDestination, ItemStack heldStack, Level world)
    {
        if (world.getBlockState(storeChestDestination).getBlock() instanceof ChestBlock)
        {
            final BlockEntity blockEntity = world.getBlockEntity(storeChestDestination);

            if (blockEntity != null && blockEntity instanceof ChestBlockEntity)
            {
                ChestBlockEntity storeChest = (ChestBlockEntity) blockEntity;

                if ( basicMiscActions(storeChest, fairy.getBlockX(), fairy.getBlockY(), fairy.getBlockZ(), world ) )
                {
                    fairy.postedCount = 2;
                    return;
                }
                else
                {
                    fairy.getNavigation().moveTo((double) storeChestDestination.getX() + 0.5, storeChestDestination.getY(), (double) storeChestDestination.getZ() + 0.5, 0.3D);
                    //fairy.getNavigation().moveTo((double) storeChestDestination.getX(), storeChestDestination.getY(), (double) storeChestDestination.getZ(), 0.3D);

                    if(fairy.blockPosition().closerThan(storeChestDestination, 3D))
                    {
                        final int emptySpace = getEmptySpace(storeChest, heldStack);

                        if (emptySpace >= 0)
                        {
                            storeChest.setItem(emptySpace, heldStack);
                        }

                        fairy.armSwing(!fairy.didSwing);
                        fairy.setHeldItem(ItemStack.EMPTY);
                        fairy.setTempItem(null);

                        fairy.postedCount = 2;

                        //return;
                    }
                }
            }
        }
    }

    public BlockPos getTakeChestDestination()
    {
        return getValidTakePos();
    }

    public BlockPos getStoreChestDestination(ItemStack stack)
    {
        return getValidStorePos(stack);
    }


    // ---------- Code from https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/common/entity/goal/carbuncle/StarbyTransportBehavior.java ----------

    public @Nullable BlockPos getValidTakePos()
    {

        if (fairy.FROM_LIST == null)
            return null;

        for (BlockPos p : fairy.FROM_LIST)
        {
            if(isPositionValidTake(p))
                return p;
        }

        return null;
    }

    public boolean isPositionValidTake(BlockPos p)
    {
        if(p == null || fairy.level.getBlockEntity(p) == null)
            return false;

        ChestBlockEntity chestBlockEntity = (ChestBlockEntity) fairy.level.getBlockEntity(p);

        if (chestBlockEntity == null)
            return false;

        for (int j = 0; j < chestBlockEntity.getContainerSize(); j++)
        {
            if (!chestBlockEntity.getItem(j).isEmpty() && isValidItem(chestBlockEntity.getItem(j)) && getValidStorePos(chestBlockEntity.getItem(j)) != null)
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getValidStorePos(ItemStack stack)
    {
        if (fairy.TO_LIST == null)
            return null;

        BlockPos returnPos = null;

        for (BlockPos b : fairy.TO_LIST)
        {
//            if(b != null)
//            {
//
//            }

            boolean validStorePos = isValidStorePos(b, stack);

            if(validStorePos)
                returnPos = b;
        }

        return returnPos;
    }

    public boolean isValidStorePos(@Nullable BlockPos b, ItemStack stack)
    {
        if(stack == null || stack.isEmpty() || b == null)
            return false;

        BlockEntity blockEntity = fairy.level.getBlockEntity(b);

        if(blockEntity != null)
        {
            return FairyUtils.doesItemMatchItemInFrameOnChest(fairy, blockEntity, stack);
        }
        else
        {
            return false;
        }
    }

    public boolean isValidItem(ItemStack stack)
    {
        if (stack.isEmpty())
            return false;

        if (getValidStorePos(stack) == null)
        {
            return false;
        }

        return true;
    }

    // --------------------

    // Actions related to specific items.
    private boolean checkChestItem(ChestBlockEntity chest, int slot, final int x, final int y, final int z, final Level world )
    {
        ItemStack stack = chest.removeItem(slot, 1);

        try
        {
            if (stack == null || stack.getCount() == 0)
            {
                return false;
            }

            BlockPos postPos = new BlockPos(fairy.postX, fairy.postY, fairy.postZ);

            if (fairy.blockPosition().closerThan(postPos, radius))
            {
                for (FairyJob fairyJob: fairyJobs )
                {
                    fairyJob.setChest(chest);
                    fairyJob.setItemStack(stack);

                    if(fairyJob.canRun(stack, x, y, z, world))
                    {
                        return true;
                    }
                }

                // Snack
                if (FairyUtils.acceptableFoods(fairy, stack) && snackTime(stack))
                {
                    return true;
                }
            }

            return false;
        }
        finally
        {
            // return the remainder to the chest.
            if (stack != null && stack.getCount() > 0)
            {
                ItemStack chestStack = chest.getItem(slot);

                if (chestStack == null)
                {
                    chest.setItem(slot, stack);
                }
                else
                {
                    assert stack.getItem() == chestStack.getItem(); // avoid duplication glitch?
                    assert stack.getCount() + chestStack.getCount() < chestStack.getMaxStackSize();

                    //chestStack.setTag(new CompoundTag());
                    chestStack.setCount(chestStack.getCount() + stack.getCount());

                    CompoundTag tag = stack.getTag();

                    if(tag != null && tag.contains("Damage"))
                    {
                        chestStack.setDamageValue(stack.getDamageValue());
                    }

                    if(tag != null && tag.contains("Enchantments"))
                    {
                        ListTag stackEnchantmentTags = stack.getEnchantmentTags();
                        //ListTag chestStackEnchantmentTags = chestStack.getEnchantmentTags();
                        //chestStackEnchantmentTags.clear();

                        if(fairy.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.STICK)
                        {
                            ListTag listTag = fairy.getItemInHand(InteractionHand.MAIN_HAND).getEnchantmentTags();
                            listTag.clear();
                            listTag.add(stackEnchantmentTags);

                            //Objects.requireNonNull(fairy.getItemInHand(InteractionHand.MAIN_HAND).getTag()).put("Enchantments", listTag);
                        }
                    }
                }
            }
        }
    }

    // Actions that only require a chest.
    private boolean miscActions( final ChestBlockEntity chest, final int x, final int y, final int z,
                                 final Level world )
    {

        for (FairyMiscAction fairyMiscAction: fairyMiscActions )
        {
            if(fairyMiscAction.canRun(chest, x, y, z, world) && !fairy.isEmotional())
            {
                return true;
            }
        }

        return false;
    }

    // Misc Actions that dont require a chest.
    private boolean basicMiscActions( final ChestBlockEntity chest, final int x, final int y, final int z,
                                 final Level world )
    {

        for (FairyMiscAction fairyBasicMiscAction: fairyBasicMiscActions )
        {
            if(fairyBasicMiscAction.canRun(chest, x, y, z, world) && !fairy.isEmotional())
            {
                return true;
            }
        }

        return false;
    }

    // Remove an itemstack that's been used up.
    private void cleanSlotInChest(final ChestBlockEntity chest, final int p )
    {
        if ( chest.getItem(p) != null && chest.getItem( p ).getItem() == null )
        {
            chest.setItem( p, (ItemStack) null );
        }
    }

    public ArrayList<ItemEntity> getGoodies( final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(ItemEntity.class, fairy.getBoundingBox().inflate( 2.5D, 2.5D, 2.5D ) );
        final ArrayList<ItemEntity> list2 = new ArrayList<ItemEntity>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final ItemEntity entity1 = (ItemEntity) list.get( i );

            final ItemStack stack = entity1.getItem();
            if ( stack != null)
            {
                if ( stack.getCount() > 0 && goodItem( stack, stack.getDamageValue() ) )
                {
                    list2.add( entity1 );
                }
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList<Animal> getAnimals(final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(Animal.class, fairy.getBoundingBox().inflate( 5D, 5D, 5D ) );

        if ( list.size() < 2 )
        {
            return null;
        }

        final ArrayList<Animal> list2 = new ArrayList<Animal>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final Animal entity1 = (Animal) list.get( i );

            // TODO: track combat correctly

            if ( FairyUtils.peacefulAnimal( entity1 ) && fairy.hasLineOfSight( entity1 ) && entity1.getHealth() > 0
                    && entity1.getTarget() == null && !entity1.isInLove()
                    && entity1.getAge() == 0 )
            {
                for ( int j = 0; j < list.size(); j++ )
                {
                    final Animal entity2 = (Animal) list.get( j );

                    if ( entity1 != entity2 && entity1.getClass() == entity2.getClass()
                            && entity2.getAge() == 0 )
                    {
                        list2.add( entity1 );
                    }
                }
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList<Pig> getPigs(final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(Pig.class, fairy.getBoundingBox().inflate( 5D, 5D, 5D ) );

        if ( list.size() < 1 )
        {
            return null;
        }

        final ArrayList<Pig> list2 = new ArrayList<Pig>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final Pig entity1 = (Pig) list.get( i );

            // TODO: track combat correctly

            if ( fairy.hasLineOfSight( entity1 ) && entity1.getHealth() > 0 && entity1.getTarget() == null
                    && entity1.getAge() >= 0)
            {
                list2.add( entity1 );
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList<Cow> getCows(final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(Cow.class, fairy.getBoundingBox().inflate( 5D, 5D, 5D ) );

        if ( list.size() < 1 )
        {
            return null;
        }

        final ArrayList<Cow> list2 = new ArrayList<Cow>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final Cow entity1 = (Cow) list.get( i );

            // TODO: track combat correctly

            if ( fairy.hasLineOfSight( entity1 ) && entity1.getHealth() > 0 && entity1.getTarget() == null
                    && entity1.getAge() >= 0)
            {
                list2.add( entity1 );
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList<Chicken> getChickens( final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(Chicken.class, fairy.getBoundingBox().inflate( 5D, 5D, 5D ) );

        if ( list.size() < 1 )
        {
            return null;
        }

        final ArrayList<Chicken> list2 = new ArrayList<Chicken>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final Chicken entity1 = (Chicken) list.get( i );

            // TODO: track combat correctly

            if ( fairy.hasLineOfSight( entity1 ) && entity1.getHealth() > 0 && entity1.getTarget() == null
                    && entity1.getAge() >= 0)
            {
                list2.add( entity1 );
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList<Sheep> getSheep( final Level world )
    {
        final List<?> list = world.getEntitiesOfClass(Sheep.class, fairy.getBoundingBox().inflate( 5D, 5D, 5D ) );

        if ( list.size() < 1 )
        {
            return null;
        }

        final ArrayList<Sheep> list2 = new ArrayList<Sheep>();

        for ( int i = 0; i < list.size(); i++ )
        {
            final Sheep entity1 = (Sheep) list.get( i );

            // TODO: track combat correctly

            if ( fairy.hasLineOfSight( entity1 ) && entity1.getHealth() > 0 && entity1.getTarget() == null
                    && entity1.getAge() >= 0 && !entity1.isSheared() )
            {
                list2.add( entity1 );
            }
        }

        if ( list2.size() <= 0 )
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    // Pick up useful objects off of the ground
    private boolean collectGoodies( final ChestBlockEntity chest, final Level world )
    {
        int count = 0;

        fairy.getNavigation().moveTo(goodies.get(0), fairy.speedModifier);

        for (int i = 0; i < goodies.size() && count < 3; i++)
        {
            final ItemEntity entity = (ItemEntity) goodies.get( i );
            final ItemStack stack = entity.getItem();

            final int emptySpace = getEmptySpace( chest, stack );

            if ( emptySpace >= 0 )
            {
                chest.setItem( emptySpace, stack );
                entity.discard();
                count++;
            }

//            if (doesItemMatchItemInFrame(chest, stack))
//            {
//                final int emptySpace = getEmptySpace( chest, stack );
//
//                if ( emptySpace >= 0 )
//                {
//                    chest.setItem( emptySpace, stack );
//                    entity.discard();
//                    count++;
//                }
//            }

        }

        if (count > 0)
        {

            world.playSound( null, fairy.position().x, fairy.position().y, fairy.position().z, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL,  0.4F,
                    ((fairy.getRandom().nextFloat() - fairy.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);

            fairy.armSwing( !fairy.didSwing );

            fairy.attackAnim = 1;

            // if(fairy.flymode() && fairy.flyTime > 0) {
            // fairy.flyTime = 0;
            // }

            return true;
        }

        return false;
    }

    // Look for a spot to put collected items
    private int getEmptySpace( final ChestBlockEntity chest, final ItemStack stack )
    {
        int temp = -1;

        for ( int i = 0; i < chest.getContainerSize(); i++ )
        {
            final ItemStack stack2 = chest.getItem( i );

            if ( temp < 0 && (stack2 == null || stack2.getCount() == 0) )
            {
                temp = i;
            }
            else if ( stack2 != null && stack.getItem() == stack2.getItem() && stack2.getCount() > 0
                    && stack2.getCount() + stack.getCount() <= stack.getMaxStackSize() && !stack2.isDamaged()
                    && !stack.isDamaged() && stack.getDamageValue() == stack2.getDamageValue() )
            {
                stack.setCount(stack.getCount() + stack2.getCount());
                return i;
            }
        }

        return temp;
    }

    // Fairy can heal itself if damaged.
    private boolean snackTime( final ItemStack stack )
    {
        if ( fairy.getHealth() < fairy.getMaxHealth() )
        {
            stack.setCount(stack.getCount() - 1);

            fairy.setHearts( !fairy.hearts() );

            if ( stack.getItem() == Items.SUGAR )
            {
                fairy.heal( 5 );
            }
            else
            {
                fairy.heal( 99 );

                if (stack.getItem() == Items.GLISTERING_MELON_SLICE)
                {
                    fairy.setWithered( false );
                    fairy.witherTime = 0;
                }
            }

            fairy.armSwing( !fairy.didSwing );
            fairy.attackAnim = 1;
            return true;
        }

        return false;
    }

    // Items worth picking up
    private boolean goodItem(final ItemStack stack, final int j )
    {
        return FairyUtils.isHoeItem(stack) || FairyUtils.isSeedItem(stack.getItem()) || FairyUtils.isBonemealItem(stack.getItem()) || FairyUtils.isAxeItem(stack)
                || FairyUtils.isSaplingBlock(stack) || FairyUtils.isLogBlock(stack) || FairyUtils.acceptableFoods(fairy, stack)
                ||  FairyUtils.isBreedingItem(stack) ||  FairyUtils.isShearingItem(stack) || FairyUtils.isClothBlock(stack) || FairyUtils.isFishingItem(stack)
                || FairyUtils.isAnimalProduct(stack) || FairyUtils.isRawFish(stack) || FairyUtils.isFishLoot(stack) || FairyUtils.isFlower( stack.getItem())
                || stack.is(Items.STICK) || stack.is(Blocks.PUMPKIN.asItem())|| FairyUtils.isAdditionalItemPickup(stack) || stack.is(ModItemTags.ITEM_TO_SMELT) || stack.is(ItemTags.COALS)
                || PotionBrewing.isIngredient(stack) || FairyUtils.isPotionContainer(stack);
    }

}
