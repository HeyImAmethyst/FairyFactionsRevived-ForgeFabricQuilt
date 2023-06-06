package net.heyimamethyst.fairyfactions.entities.ai;

import com.google.common.collect.Maps;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.registry.ModBlockTags;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FairyJob
{

    // Recursion limit for trees.
    private static final int	maxTreeHeight	= 99;

    // Will be referenced a lot. It's the fairy who's doing the job
    private final FairyEntity fairy;

    // Used to make sure fairies don't just tear apart random trees.
    private boolean				doHaveAxe;

    // Make sure that breeding is only attempted once per chest.
    private boolean				triedBreeding;

    // Make sure that shearing is only attempted once per chest.
    private boolean				triedShearing;

    // A list of items on the ground.
    private ArrayList<ItemEntity> goodies;

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

    public FairyJob( final FairyEntity entityfairy )
    {
        fairy = entityfairy;
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
        getNearbyChest2( x, y, z, world );
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

        getNearbyChest3( x, y, z, world );
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

    private static final int radius = 5;

    private void getNearbyChest2( final int x, final int y, final int z, final Level world )
    {
        int i, j, k;

        for ( int a = -radius; a <= radius; a++ )
        {
            for ( int b = -2; b <= 2; b++ )
            {
                for ( int c = -radius; c <= radius; c++ )
                {
                    i = x + a;
                    j = y + b;
                    k = z + c;

                    final BlockPos pos = new BlockPos(i, j, k);

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
                                    cleanSlot(chest, p );
                                    fairy.postedCount = 2;
                                    return;
                                }
                            }

                            if ( miscActions(chest, x, y, z, world ) )
                            {
                                fairy.postedCount = 2;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void getNearbyChest3( final int x, final int y, final int z, final Level world )
    {
        int i, j, k;

        for ( int a = -radius; a <= radius; a++ )
        {
            for ( int b = -2; b <= 2; b++ )
            {
                for ( int c = -radius; c <= radius; c++ )
                {
                    i = x + a;
                    j = y + b;
                    k = z + c;

                    final BlockPos pos = new BlockPos( i, j, k );

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

                                if ( stack != null && isFishingItem( stack )
                                        && onFishingUse( stack, x, y, z, world ) )
                                {
                                    cleanSlot( chest, p );
                                    fairy.postedCount = 2;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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

            // Farming
            if (isHoeItem(stack) && onHoeUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            if (isSeedItem(stack.getItem()) && onSeedUse(stack, x, y, z, world))
            {
                return true;
            }

            if (isBonemealItem(stack.getItem()) && onBonemealUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            // Foresting
            if (isAxeItem(stack) && onAxeUse(stack, x, y, z, world))
            {
                return true;
            }

            if ( isSaplingBlock(stack) && onSaplingUse( stack, x, y, z, world ) )
            {
                return true;
            }

            if ( isBerryBushItem(stack) && onBerryBushUse( stack, x, y, z, world ) )
            {
                return true;
            }

            if ( isBambooBlock(stack) && onBambooUse( stack, x, y, z, world ) )
            {
                return true;
            }

            // Breeding
            if (!triedBreeding && onBreedingUse(stack, world))
            {
                return true;
            }

            // Sheep shearing
            if (!triedShearing && isShearingItem(stack) && (shearSheep(stack, world) || harvestHoneyComb(stack, x, y, z, world)))
            {
                return true;
            }

            // Fishing
            if (isFishingItem(stack) && onFishingUse(stack, x, y, z, world))
            {
                return true;
            }

            // Snack
            if (FairyUtils.acceptableFoods(fairy, stack) && snackTime(stack))
            {
                return true;
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
        if ( cutTallGrass( x, y, z, world ) )
        {
            return true;
        }

        if (gatherFromBerryBush(x, y, z, world))
        {
            return true;
        }

        if ( doHaveAxe && trimExcessLeaves( x, y, z, world ) )
        {
            return true;
        }

        return false;
    }

    // Remove an itemstack that's been used up.
    private void cleanSlot( final ChestBlockEntity chest, final int p )
    {
        if ( chest.getItem(p) != null && chest.getItem( p ).getItem() == null )
        {
            chest.setItem( p, (ItemStack) null );
        }
    }

    // What to do with a hoe
    private boolean onHoeUse( final ItemStack stack, int x, final int y, int z, final Level world )
    {
        for ( int a = 0; a < 3; a++ )
        {
            BlockPos blockPos = new BlockPos(x, y, z);

            BlockState blockState = world.getBlockState(blockPos);
            final Block i = blockState.getBlock();

            //BlockState toolModifiedState = world.getBlockState(blockPos).getToolModifiedState(pContext, net.minecraftforge.common.ToolActions.HOE_TILL, false);

            if ( world.getBlockState(new BlockPos(x, y + 1, z )).is(Blocks.AIR) && (i == Blocks.GRASS_BLOCK || i == Blocks.DIRT || i == Blocks.COARSE_DIRT))
            {
                final Block block = i == Blocks.COARSE_DIRT ? Blocks.DIRT : Blocks.FARMLAND;

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": hoeing land");

                world.setBlockAndUpdate(new BlockPos( x, y, z), block.defaultBlockState());

                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

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

    // What to do with seeds
    private boolean onSeedUse( final ItemStack stack, int x, final int y, int z, final Level world )
    {

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
            if (state != null && !state.is(Blocks.BAMBOO) && !state.is(Blocks.BAMBOO_SAPLING) && !state.is(ModBlockTags.IS_BERRY_BUSH_BLOCK) && !state.is(BlockTags.SAPLINGS) && world.getBlockState(new BlockPos(x,y,z)).getMaterial().isReplaceable() && state.canSurvive(world, new BlockPos(x,y,z)))//state.getMaterial().isReplaceable()) // world.getBlockState(new BlockPos(x,y,z).above()).is(Blocks.AIR) && state.canSurvive(world, new BlockPos(x,y,z)))
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

    private boolean gatherFromBerryBush(int x, final int y, int z, final Level world )
    {
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

    // Use bonemeal to speed up wheat growth
    private boolean onBonemealUse( final ItemStack stack, int x, final int y, int z, final Level world )
    {
        for ( int a = 0; a < 3; a++ )
        {
            final BlockPos pos = new BlockPos(x,y+1,z);
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();

            if ( block instanceof BonemealableBlock && ((BonemealableBlock) block).isValidBonemealTarget(world, pos, state, false) && (fairy.blockPosition() != pos || fairy.blockPosition() != pos.above()))
            {

                //FairyFactions.LOGGER.debug(this.fairy.toString()+": bonemealing");

                ((BonemealableBlock) block).performBonemeal((ServerLevel)world, fairy.getRandom(), pos, state);

                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.shrink(1);

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

        triedShearing = true;

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

    // What to do with an axe
    private boolean onAxeUse( final ItemStack stack, int x, final int y, int z, final Level world )
    {
        final int m = x;
        final int n = z;

        // TODO: handle additional tree types
        // TODO: clean up treecapitation logic, integrate additionalAxeUse

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( chopLog( world, x, y, z) )
            {
                fairy.armSwing( !fairy.didSwing );
                fairy.setTempItem(stack.getItem());

                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                if ( stack.getCount() > 0 )
                {
                    additionalAxeUse( stack, x, y + 1, z, world, maxTreeHeight );
                }

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

    private boolean chopLog( final Level world, final int x, final int y, final int z )
    {
        final BlockPos pos = new BlockPos(x,y,z);
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if ( state.is(BlockTags.LOGS) )
        {

            //FairyFactions.LOGGER.debug(this.fairy.toString()+": chopping wood");

            world.destroyBlock(new BlockPos(x, y, z), true, fairy);

            return true;
        }
        else
        {
            return false;
        }
    }

    @Deprecated
    private void additionalAxeUse( final ItemStack stack, int x, final int y, int z, final Level world, int recurse )
    {
        if ( recurse > maxTreeHeight )
        {

            recurse = maxTreeHeight;
        }

        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;

            if( chopLog( world, x, y, z ) )
            {
                stack.hurtAndBreak(1, fairy, (p_29822_) ->
                {
                    p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                if ( stack.getCount() > 0 && recurse > 0 )
                {
                    if ( a != 4 )
                    {
                        additionalAxeUse( stack, x, y, z, world, recurse - 1 );
                    }

                    if ( stack.getCount() > 0 && recurse > 0 )
                    {
                        additionalAxeUse( stack, x, y + 1, z, world, recurse - 1 );
                    }
                }
            }
        }
    }

    // What to do with saplings
    private boolean onSaplingUse( final ItemStack stack, int x, final int y, int z, final Level world )
    {
        // TODO: experiment more with sapling placing code. Fairies place saplings less frequently than intended
        //return true;

        Block plantable;

        if (FairyUtils.isIPlantable(Block.byItem(stack.getItem())))
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
            if (state.is(BlockTags.SAPLINGS) && world.getBlockState(new BlockPos(x,y,z)).getMaterial().isReplaceable() && state.canSurvive(world, new BlockPos(x,y,z)))//state.getMaterial().isReplaceable()) // world.getBlockState(new BlockPos(x,y,z).above()).is(Blocks.AIR) && state.canSurvive(world, new BlockPos(x,y,z)))
            {

                for (int l = -2; l < 3; l++)
                {
                    for (int i1 = -2; i1 < 3; i1++)
                    {
                        if (l == 0 && i1 == 0)
                        {
                            if (goodPlaceForTrees(x + l, y, z + i1, world) > 0)
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
                                int l1 = goodPlaceForTrees(x + l, y + k1, z + i1, world);
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

    private boolean onBerryBushUse(final ItemStack stack, int x, final int y, int z, final Level world )
    {
        // TODO: experiment more with sapling placing code. Fairies place saplings less frequently than intended
        //return true;

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
            if (state.is(ModBlockTags.IS_BERRY_BUSH_BLOCK) && world.getBlockState(new BlockPos(x,y,z)).getMaterial().isReplaceable() && state.canSurvive(world, new BlockPos(x,y,z)))//state.getMaterial().isReplaceable()) // world.getBlockState(new BlockPos(x,y,z).above()).is(Blocks.AIR) && state.canSurvive(world, new BlockPos(x,y,z)))
            {

                for (int l = -2; l < 3; l++)
                {
                    for (int i1 = -2; i1 < 3; i1++)
                    {
                        if (l == 0 && i1 == 0)
                        {
                            if (goodPlaceForBerryBush(x + l, y, z + i1, world) > 0)
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
                                int l1 = goodPlaceForBerryBush(x + l, y + k1, z + i1, world);
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

    private boolean onBambooUse(final ItemStack stack, int x, final int y, int z, final Level world )
    {
        // TODO: experiment more with sapling placing code. Fairies place saplings less frequently than intended
        //return true;

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
                            if (goodPlaceForBamboo(x + l, y, z + i1, world) > 0)
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
                                int l1 = goodPlaceForBamboo(x + l, y + k1, z + i1, world);
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

    // Check if it's a good place to put a sapling down
    private int goodPlaceForTrees( final int x, final int y, final int z, final Level world )
    {
        return placePlantTagWithSpace(x, y, z, world, BlockTags.SAPLINGS);
    }

    private int goodPlaceForBerryBush( final int x, final int y, final int z, final Level world )
    {
        //return placePlantWithSpace(x, y, z, world, Blocks.SWEET_BERRY_BUSH);
        return placePlantTagWithSpace(x, y, z, world, ModBlockTags.IS_BERRY_BUSH_BLOCK);
    }

    private int goodPlaceForBamboo(final int x, final int y, final int z, final Level world )
    {
        return placePlantWithSpace(x, y, z, world, Blocks.BAMBOO_SAPLING);
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

    // Attempt to breed animals
    private boolean onBreedingUse( final ItemStack stack, final Level world )
    {
        final ArrayList<Animal> animals = getAnimals( world );

        if ( animals == null )
        {
            return false;
        }

        int count = 0;


        for ( int i = 0; i < animals.size() && count < 3 && stack.getCount() > 0; i++ )
        {
            final Animal entity = (Animal) animals.get( i );

            int isBreedingCounter = entity.getInLoveTime();

            // skip unbreedable animals
            if (!entity.isFood(stack) // can't breed with this item
                    || entity.getAge() != 0 // is juvenile (negative) or recently proceated (positive)
                    || isBreedingCounter != 0 // literally breeding now.
            )
            {
                continue;
            }

            triedBreeding = true;

            if ( fairy.distanceTo( entity ) < 3F )
            {
                //FairyFactions.LOGGER.debug(this.fairy.toString()+": breeding animals");

                entity.setInLoveTime(600);
                count++;
                stack.setCount(stack.getCount() - 1);
            }
        }

        if ( count > 0 )
        {
            fairy.armSwing( !fairy.didSwing );
            fairy.setTempItem(stack.getItem());

            fairy.attackAnim = 1;
            fairy.setHearts( !fairy.didHearts );

            if ( fairy.flymode() && fairy.getFlyTime() > 0 )
            {
                fairy.setFlyTime( 0 );
            }

            return true;
        }

        return false;
    }

    private boolean shearSheep(final ItemStack stack, final Level world )
    {
        final ArrayList<Sheep> sheep = getSheep( world );
        triedShearing = true;

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

    private static final float pia = -(float) Math.PI / 180F;

    private boolean onFishingUse( final ItemStack stack, final int x, final int y, final int z, final Level world )
    {
        int fishingSpeedBonus = EnchantmentHelper.getFishingSpeedBonus(stack);
        int fishingLuckBonus = EnchantmentHelper.getFishingLuckBonus(stack);

        fairy.fishingSpeedBonus = fishingSpeedBonus;
        fairy.fishingLuckBonus = fishingLuckBonus;

        if ( fairy.isInWater() /*&& !fairy.getNavigation().isDone() */)
        {
            getToLand( x, y, z, world );
            return false;
        }
        else if ( fairy.flymode() && fairy.getFlyTime() > 0 /*&& !fairy.getNavigation().isDone() */)
        {
            fairy.setFlyTime( 0 );
            return false;
        }
        else if ( !fairy.isOnGround() || fairy.isInWater() )
        {
            return false;
        }

        final float angle = fairy.yBodyRot - 30F + (fairy.getRandom().nextFloat() * 60F);
        final double posX = fairy.position().x + Math.sin( angle * pia ) * 6D;
        final double posY = fairy.position().y;
        final double posZ = fairy.position().z + Math.cos( angle * pia ) * 6D;
        final int a = (int)Math.floor( posX );
        final int b = y;
        final int c = (int)Math.floor( posZ );
        final BlockPos pos = new BlockPos(a,b,c);

        for ( int j = -4; j < 0; j++ )
        {
            if ( b + j > 0 && b + j < world.getHeight() - 10 )
            {
                boolean flag = false;

                for ( int i = -1; i <= 1 && !flag; i++ )
                {
                    for ( int k = -1; k <= 1 && !flag; k++ )
                    {
                        final BlockPos pos2 = pos.offset(i, j, k);
                        if ( world.getBlockState( pos2 ).getBlock() != Blocks.WATER
                                || world.getBlockState( pos2.above() ) != Blocks.AIR.defaultBlockState()
                                || world.getBlockState( pos2.above(2) ) != Blocks.AIR.defaultBlockState()
                                || world.getBlockState( pos2.above(3) ) != Blocks.AIR.defaultBlockState() )
                        {
                            flag = true;
                        }
                    }
                }

                if ( !flag )
                {
                    final Path path = fairy.getNavigation().createPath(new BlockPos(a, b + j, c), 1);

                    if (path != null && canSeeToSpot( posX, posY, posZ, world ))
                    {
                        //fairy.yHeadRot = angle;
                        fairy.getLookControl().setLookAt(posX,posY,posZ, angle, fairy.getMaxHeadXRot());
                        fairy.castRod();
                        fairy.playSound(SoundEvents.FISHING_BOBBER_THROW, 1, 1);

                        // TODO: player fishing normally damages the rod when the reels something in; should we do that?
                        //Currently can't figure out how to achieve that. Need to pass the itemstack to the fairy tasks class for the other
                        //"castRod" method call for reeling the fish in

                        stack.hurtAndBreak(1, fairy, (p_29822_) ->
                        {
                            p_29822_.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                        });

//                        CompoundTag tag = stack.getTag();
//
//                        if(tag != null && tag.contains("Enchantments"))
//                        {
//                            ListTag stackEnchantmentTags = stack.getEnchantmentTags();
//                            //ListTag chestStackEnchantmentTags = chestStack.getEnchantmentTags();
//                            //chestStackEnchantmentTags.clear();
//
//                            if(fairy.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.STICK)
//                            {
////                                ListTag listTag = fairy.getItemInHand(InteractionHand.MAIN_HAND).getEnchantmentTags();
////                                listTag.clear();
////                                //listTag.add(stackEnchantmentTags);
////
////                                for (Tag t :stackEnchantmentTags)
////                                {
////                                    listTag.add(t);
////                                }
//
//                                CompoundTag stickTag = tag;
//                                stickTag.remove("Damage");
//
//                                fairy.getItemInHand(InteractionHand.MAIN_HAND).setTag(stickTag);
//                            }
//                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void getToLand( final int x, final int y, final int z, final Level world )
    {
        for ( int q = 0; q < 16; q++ )
        {
            final int i = x - 5 + fairy.getRandom().nextInt( 11 );
            final int j = y + 1 + fairy.getRandom().nextInt( 5 );
            final int k = z - 5 + fairy.getRandom().nextInt( 11 );

            if ( y > 1 && y < world.getHeight() - 1 )
            {
                if ( FairyUtils.isAirySpace( fairy, i, j, k ) && !FairyUtils.isAirySpace(fairy, i, j - 1, k )
                        && world.getBlockState( new BlockPos(i, j - 1, k) ).isSolidRender(fairy.level, new BlockPos(i, j - 1, k)))
                {
                    final Path path = fairy.getNavigation().createPath(new BlockPos(i, j, k), 1);

                    if ( path != null )
                    {
                        fairy.getNavigation().moveTo(path, fairy.speedModifier);

                        if ( !fairy.flymode() )
                        {
                            fairy.setFlyTime( 0 );
                        }

                        return;
                    }
                }
            }
        }
    }

    private boolean canSeeToSpot( final double posX, final double posY, final double posZ, final Level world )
    {

//        return world.rayTraceBlocks(
//                new Vec3( fairy.position().x, fairy.position().y + fairy.getEyeHeight(), fairy.position().z ),
//                new Vec3( posX, posY, posZ ) ) == null;

        //CREATE THE RAY
        ClipContext rayCtx = new ClipContext(
                new Vec3( fairy.position().x, fairy.position().y + fairy.getEyeHeight(), fairy.position().z ),
                new Vec3( posX, posY, posZ ),
                ClipContext.Block.VISUAL, ClipContext.Fluid.WATER, fairy);

        BlockHitResult rayBlockHit = world.clip(rayCtx);
        BlockPos pos = rayBlockHit.getBlockPos();

//        //CAST THE RAY
//        BlockHitResult rayBlockHit = world.clip(rayCtx);

        return pos != null;
    }

    private boolean cutTallGrass( int x, final int y, int z, final Level world )
    {
        final int m = x;
        final int n = z;

        for ( int a = 0; a < 9; a++ )
        {
            x = m + (a / 3) - 1;
            z = n + (a % 3) - 1;
            //final BlockPos pos = new BlockPos(x,y,z);
            final BlockState state = world.getBlockState(new BlockPos(x,y,z));
            final Block above = world.getBlockState(new BlockPos(x,y + 1, z)).getBlock();
            final Block below = world.getBlockState(new BlockPos(x,y - 1, z)).getBlock();

            if ( breakablePlant( state, above, below ) )
            {
                final Block block = state.getBlock();

                world.destroyBlock(new BlockPos(x, y, z), true, fairy);

                fairy.armSwing( !fairy.didSwing );
                fairy.attackAnim = 30;

                if ( fairy.flymode() && fairy.getFlyTime() > 0 )
                {
                    fairy.setFlyTime( 0 );
                }

                return true;
            }
        }

        return false;
    }

    // Pick apart trees
    private boolean trimExcessLeaves( int x, int y, int z, final Level world )
    {
        for ( int d = 0; d < 3; d++ )
        {
            final int a = fairy.getRandom().nextInt( 3 );
            final int b = (fairy.getRandom().nextInt( 2 ) * 2) - 1;

            if ( a == 0 )
            {
                x += b;
            }
            else if ( a == 1 )
            {
                y += b;
            }
            else
            {
                z += b;
            }

            final BlockPos pos = new BlockPos(x,y,z);
            final BlockState state = world.getBlockState(pos);

            if ( state.is(BlockTags.LEAVES))
            {
                world.destroyBlock(pos, true, fairy);

                fairy.armSwing( !fairy.didSwing );

                fairy.attackAnim = 20;

                return true;
            }
        }

        return false;
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

    // Is the item a hoe?
    private boolean isHoeItem( final ItemStack i )
    {
        return i.is(ModItemTags.IS_HOE_ITEM);
    }

    // Is the item an axe?
    private boolean isAxeItem( final ItemStack i )
    {
        if (i.is(ModItemTags.IS_AXE_ITEM))
        {
            doHaveAxe = true;
            return true;
        }

        return false;
    }

    // Is it a plant that should be broken
    private boolean breakablePlant(final BlockState state, final Block above, final Block below )
    {
        // we're gonna treat this as everything block that should be punched.
        // cocoa?... hrmmm
        // mushrooms: tricky, when there are at least 4 other mushrooms of same type in 9x3x9 area.
        // snow?  maybe?  if there's plants?  if there's no shovel?

        // crops: that should be wheat, carrots and potatoes, when MD level is 7.
        final Block block = state.getBlock();

        return (
                block instanceof CropBlock
                && (state.hasProperty(((CropBlock)block).getAgeProperty()) && ((CropBlock)block).isMaxAge(state)))
                // not a crop, a bush apparently...
                || block == Blocks.NETHER_WART && state.getValue(NetherWartBlock.AGE) == 3
                // reeds: when above reeds.
                || block == Blocks.SUGAR_CANE && above == Blocks.SUGAR_CANE && below != Blocks.SUGAR_CANE
                // cactus: break only when above sand and below cactus, to prevent losing drops.
                || block == Blocks.CACTUS && above == Blocks.CACTUS && below != Blocks.CACTUS
                || block == Blocks.BAMBOO && above == Blocks.BAMBOO && below != Blocks.BAMBOO
                // melons/pumkins... always?
                || block == Blocks.MELON || block == Blocks.PUMPKIN
                || block == Blocks.COCOA && state.getValue(CocoaBlock.AGE) == 2
                // tallgrass, which drops seeds!
                || block == Blocks.TALL_GRASS
                || block == Blocks.GRASS
                // all other doo-dads? ie bushes and tall plants?
                //|| block == Blocks.DANDELION
                //|| block == Blocks.POPPY
                || block == Blocks.SNOW;
    }

    private boolean isSeedItem( final Item item )
    {
        return FairyUtils.isIPlantable(Block.byItem(item))
                || item == Items.SUGAR_CANE
                || Block.byItem(item) instanceof CocoaBlock;
    }

    private boolean isBonemealItem( final Item item)
    {
        return item == Items.BONE_MEAL;
    }

    // Is the item a sapling?
    private boolean isSaplingBlock( final ItemStack item )
    {
        return item.is(ItemTags.SAPLINGS);
    }

    // Is the item a sweetberry like item?
    private boolean isBerryBushItem(final ItemStack i )
    {
        return i.is(ModItemTags.IS_BERRY_BUSH_ITEM);
    }

    private boolean isBambooBlock(final ItemStack item )
    {
        return item.is(Items.BAMBOO);
    }

    // Is the item a log block?
    private boolean isLogBlock( final ItemStack item )
    {
        return item.is(ItemTags.LOGS);
    }

    private boolean isShearingItem( ItemStack i )
    {
        return i.is(ModItemTags.IS_SHEARING_ITEM);
    }

    private boolean isClothBlock( final ItemStack i )
    {
        return i.is(ItemTags.WOOL);
    }

    // A fishing rod, used to fish
    private boolean isFishingItem( final ItemStack i )
    {
        return i.is(ModItemTags.IS_FISHING_ITEM);
    }

    // Item gotten from fishing, also used to tame Ocelots
    private boolean isRawFish( final ItemStack i )
    {
        return i.is(ItemTags.FISHES);
    }

    private boolean isAnimalProduct(ItemStack i)
    {
        return i.is(ModItemTags.IS_ANIMAL_PRODUCT);
    }

    private boolean isFishLoot( final ItemStack item )
    {
        return item.is(ModItemTags.IS_FISH_LOOT);
    }

    private boolean isFlower( final Item item )
    {
        return  Block.byItem(item).defaultBlockState().is(BlockTags.FLOWERS);
    }

    private boolean isBreedingItem(ItemStack i)
    {
        return i.is(ModItemTags.IS_BREEDING_ITEM);
    }

    private boolean isAdditionalItemPickup( final ItemStack item )
    {
        return item.is(ModItemTags.IS_ADDITIONAL_ITEM_PICKUP);
    }

    // Items worth picking up
    private boolean goodItem(final ItemStack stack, final int j )
    {
        return isHoeItem(stack) || isSeedItem(stack.getItem()) || isBonemealItem(stack.getItem()) || isAxeItem(stack)
                || isSaplingBlock(stack) || isLogBlock(stack) || FairyUtils.acceptableFoods(fairy, stack)
                ||  isBreedingItem(stack) ||  isShearingItem(stack) || isClothBlock(stack) || isFishingItem(stack)
                || isAnimalProduct(stack) || isRawFish(stack) || isFishLoot(stack) || isFlower( stack.getItem())
                || stack.is(Items.STICK) || stack.is(Blocks.PUMPKIN.asItem())|| isAdditionalItemPickup(stack);
    }
}
