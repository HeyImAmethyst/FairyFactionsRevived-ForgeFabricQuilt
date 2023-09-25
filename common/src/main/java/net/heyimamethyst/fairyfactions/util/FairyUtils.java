package net.heyimamethyst.fairyfactions.util;


import com.google.common.collect.Maps;
import net.heyimamethyst.fairyfactions.Loc;
import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.FairyEntityBase;
import net.heyimamethyst.fairyfactions.entities.ai.fairy_job.FairyJobManager;
import net.heyimamethyst.fairyfactions.registry.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FairyUtils
{

    /**
     *  NB: These name strings must match on client and server - so cannot
     *  simply be moved into the loc system. They CAN, however, be moved into
     *  config files when the time comes.
     */

    public static final String	name_prefixes[]		= { "Silly", "Fire",
            "Twinkle", "Bouncy", "Speedy", "Wiggle", "Fluffy", "Cloudy",
            "Floppy", "Ginger", "Sugar", "Winky", "Giggle", "Cutie", "Sunny",
            "Honey" };

    public static final String	name_suffixes[]		= { "puff", "poof", "butt",
            "munch", "star", "bird", "wing", "shine", "snap", "kins", "bee",
            "chime", "button", "bun", "heart", "boo" };

    public static final String	faction_colors[]	= { "§0", "§1", "§2",
            "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a",
            "§b", "§c", "§d", "§e", "§f" };

    public static final ChatFormatting faction_colors_formatting[]	= { ChatFormatting.BLACK, ChatFormatting.DARK_BLUE, ChatFormatting.DARK_GREEN,
            ChatFormatting.DARK_AQUA, ChatFormatting.DARK_RED, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.BLUE, ChatFormatting.GREEN,
            ChatFormatting.AQUA, ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE, ChatFormatting.YELLOW, ChatFormatting.WHITE };


    public static final String	faction_names[]		= { "no queen",
            Loc.FACTION_1.get(), Loc.FACTION_2.get(), Loc.FACTION_3.get(),
            Loc.FACTION_4.get(), Loc.FACTION_5.get(), Loc.FACTION_6.get(),
            Loc.FACTION_7.get(), Loc.FACTION_8.get(), Loc.FACTION_9.get(),
            Loc.FACTION_10.get(), Loc.FACTION_11.get(), Loc.FACTION_12.get(),
            Loc.FACTION_13.get(), Loc.FACTION_14.get(), Loc.FACTION_15.get() };

    public static void nameFairyEntity(FairyEntity entity, @Nullable String name)
    {
        if (!name.equals(""))
        {
            //entity.setFairyCustomName(name);
            entity.setCustomName(Component.literal(name));
        }
        else if(name == null || name.equals(""))
        {
            //entity.setFairyCustomName("");
            entity.setCustomName(null);
        }
    }

    public static BlockState getPlant(Block block, BlockGetter world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() != block ? block.defaultBlockState() : state;
    }

    public static PlantType getPlantType(Block block, BlockGetter level, BlockPos pos)
    {
        if (block instanceof CropBlock) return PlantType.CROP;
        if (block instanceof SaplingBlock) return PlantType.PLAINS;
        if (block instanceof FlowerBlock) return PlantType.PLAINS;
        if (block == Blocks.DEAD_BUSH)      return PlantType.DESERT;
        if (block == Blocks.LILY_PAD)       return PlantType.WATER;
        if (block == Blocks.RED_MUSHROOM)   return PlantType.CAVE;
        if (block == Blocks.BROWN_MUSHROOM) return PlantType.CAVE;
        if (block == Blocks.NETHER_WART)    return PlantType.NETHER;
        if (block == Blocks.TALL_GRASS)      return PlantType.PLAINS;
        return PlantType.PLAINS;
    }

//    public static <K, V> Map<K, V> getCollectionFromIterable(Iterable<V extends SpawnEggItem> itr)
//    {
//        // Create an empty Collection to hold the result
//        Map<K, V> cltn = Maps.newIdentityHashMap();
//
//        //iterable.forEach(target::add);
//
//        // Iterate through the iterable to
//        // add each element into the collection
//        for (V t : itr)
//            cltn.put(t.getType(null), t);
//        itr.forEach(cltn.put());
//
//        // Return the converted collection
//        return cltn;
//    }

    public static boolean isIPlantable(Block block)
    {
        if (block instanceof BushBlock) return true;
        if (block instanceof CropBlock) return true;
        if (block instanceof FlowerBlock) return true;
        if (block instanceof BambooSaplingBlock) return true;
        if (block instanceof BambooStalkBlock) return true;
        if (block instanceof SugarCaneBlock) return true;
        if (block instanceof CactusBlock) return true;
        if (block == Blocks.DEAD_BUSH)      return true;
        if (block == Blocks.LILY_PAD)       return true;
        if (block == Blocks.RED_MUSHROOM)   return true;
        if (block == Blocks.BROWN_MUSHROOM) return true;
        if (block == Blocks.NETHER_WART)    return true;
        if (block == Blocks.TALL_GRASS)      return true;

        return false;
    }

    public static boolean percentChance(FairyEntity fairy, double chance)
    {
        //return fairy.getRandom().nextDouble() < chance;
        return Math.random() <= chance;
    }

    public static boolean sameTeam(FairyEntity thisFairy, FairyEntity entity)
    {
        if (thisFairy.tamed())
        {
            return entity.tamed() && entity.getFaction() == 0
                    && entity.rulerName().equals(thisFairy.rulerName());
        }
        else if (thisFairy.getFaction() > 0)
        {
            return entity.getFaction() == thisFairy.getFaction();
        }

        return false;
    }

    // Foods that can be used for taming
    public static boolean acceptableFoods(FairyEntity fairy, ItemStack i)
    {
        if (i.is(ModItemTags.IS_FAIRY_QUEEN_FOOD))
        {
            return true;
        }
        else if (fairy.tamed() || !fairy.queen())
        {
            return i.is(ModItemTags.IS_FAIRY_FOOD);
        }

        return false;
    }

    // Things used for disbanding
    public static boolean vileSubstance(Item i)
    {
        return i == Items.SLIME_BALL || i == Items.ROTTEN_FLESH
                || i == Items.SPIDER_EYE || i == Items.FERMENTED_SPIDER_EYE;
    }

    // The quickest way to Daphne
    public static boolean realFreshOysterBars(Item i)
    {
        return i == Items.MAGMA_CREAM;
    }

    // Item used to rename a fairy, paper
    public static boolean namingItem(Item i)
    {
        return i == Items.PAPER;
    }

    // Is the item a snowball or not.
    public static boolean snowballItem(Item i)
    {
        return i == Items.SNOWBALL;
    }

    // Can the item give a haircut.
    public static boolean haircutItem(ItemStack i)
    {
        return isShearingItem(i) || i.is(ModItemTags.IS_HAIRCUT_ITEM);
    }

    public static boolean checkGroundBelow(FairyEntity fairy)
    {
        int a = (int)Math.floor(fairy.position().x);
        int b = (int)Math.floor(fairy.getBoundingBox().minY);
        int b1 = (int)Math.floor(fairy.getBoundingBox().minY - 0.5D);
        int c = (int)Math.floor(fairy.position().z);

        if (!isAirySpace(fairy, a, b - 1, c) || !isAirySpace(fairy, a, b1 - 1, c))
        {
            return true;
        }

        return false;
    }

    public static boolean isAirySpace(FairyEntityBase fairy, int a, int b, int c)
    {
        if (b < 0 || b >= fairy.level.getHeight())
        {
            return false;
        }

        Block block = fairy.level.getBlockState(new BlockPos(a, b, c)).getBlock();

        if (block == null || block == Blocks.AIR)
            return true;

        Material matt = block.defaultBlockState().getMaterial();

        if (matt == null || matt == Material.AIR || matt == Material.PLANT
                || matt == Material.REPLACEABLE_PLANT || matt == Material.FIRE
                || matt == Material.DECORATION || matt == Material.SNOW)
        {
            return true;
        }

        return false;
    }

    public static boolean checkFlyBlocked(FairyEntity fairy)
    {
        int a = (int)Math.floor(fairy.position().x);
        int b = (int)Math.floor(fairy.getBoundingBox().minY);
        int c = (int)Math.floor(fairy.position().x);

        if (!isAirySpace(fairy, a, b + 1, c) || !isAirySpace(fairy, a, b + 2, c))
        {
            return true;
        }

        return false;
    }

    public static List<Item> getItemsFromFairyFoodTag()
    {
        Iterator<Item> items = ModExpectPlatform.getItemsOfTag(ModItemTags.IS_FAIRY_FOOD);
        List<Item> itemsList = new ArrayList<>();

        if(items != null)
        {
            while(items.hasNext())
            {
                Item item = items.next().asItem();
                itemsList.add(item);
            }

            //System.out.println(itemsList);

            return itemsList;
        }
        else
        {
            //System.out.println(itemsList);

            return null;
        }
    }

    public static boolean peacefulAnimal(Animal animal)
    {
        Class thing = animal.getClass();

        return thing == Pig.class || thing == Cow.class
                || thing == Chicken.class || thing == Sheep.class
                || thing == MushroomCow.class;
    }

    public static void shuffle(List<?> list, RandomSource rnd)
    {
        int SHUFFLE_THRESHOLD = 5;
        int size = list.size();
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess)
        {
            for (int i=size; i>1; i--)
                swap(list, i-1, rnd.nextInt(i));
        }
        else
        {
            Object[] arr = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, rnd.nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (Object e : arr)
            {
                it.next();
                it.set(e);
            }
        }
    }

    public static void swap(List<?> list, int i, int j)
    {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }

    private static void swap(Object[] arr, int i, int j)
    {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    //Fairy Job

    // Is the item a hoe?
    public static boolean isHoeItem( final ItemStack i )
    {
        return i.is(ModItemTags.IS_HOE_ITEM);
    }

    // Is the item an axe?
    public static boolean isAxeItem( final ItemStack i )
    {
        if (i.is(ModItemTags.IS_AXE_ITEM))
        {
            FairyJobManager.INSTANCE.doHaveAxe = true;
            return true;
        }

        return false;
    }


    // Is the item a sword?
    public static boolean isSwordItem( final ItemStack i )
    {
        return i.is(ModItemTags.IS_SWORD_ITEM);
    }

    public static boolean isSeedItem( final Item item )
    {
        return FairyUtils.isIPlantable(Block.byItem(item))
                || item == Items.SUGAR_CANE
                || Block.byItem(item) instanceof CocoaBlock;
    }

    public static boolean isBonemealItem( final Item item)
    {
        return item == Items.BONE_MEAL;
    }

    // Is the item a sapling?
    public static boolean isSaplingBlock( final ItemStack item )
    {
        return item.is(ItemTags.SAPLINGS);
    }

    // Is the item a sweetberry like item?
    public static boolean isBerryBushItem(final ItemStack i )
    {
        return i.is(ModItemTags.IS_BERRY_BUSH_ITEM);
    }

    public static boolean isBambooBlock(final ItemStack item )
    {
        return item.is(Items.BAMBOO);
    }

    // Is the item a log block?
    public static boolean isLogBlock( final ItemStack item )
    {
        return item.is(ItemTags.LOGS) || item.is(Items.MANGROVE_ROOTS);
    }

    public static boolean isShearingItem( ItemStack i )
    {
        return i.is(ModItemTags.IS_SHEARING_ITEM);
    }

    public static boolean isClothBlock( final ItemStack i )
    {
        return i.is(ItemTags.WOOL);
    }

    // A fishing rod, used to fish
    public static boolean isFishingItem( final ItemStack i )
    {
        return i.is(ModItemTags.IS_FISHING_ITEM);
    }

    // Item gotten from fishing, also used to tame Ocelots
    public static boolean isRawFish( final ItemStack i )
    {
        return i.is(ItemTags.FISHES);
    }

    public static boolean isAnimalProduct(ItemStack i)
    {
        return i.is(ModItemTags.IS_ANIMAL_PRODUCT);
    }

    public static boolean isFishLoot( final ItemStack item )
    {
        return item.is(ModItemTags.IS_FISH_LOOT);
    }

    public static boolean isFlower( final Item item )
    {
        return  Block.byItem(item).defaultBlockState().is(BlockTags.FLOWERS);
    }

    public static boolean isBreedingItem(ItemStack i)
    {
        return i.is(ModItemTags.IS_BREEDING_ITEM);
    }

    public static boolean isPotionContainer(ItemStack item)
    {
        return item.getItem() == Items.POTION || item.getItem() == Items.SPLASH_POTION || item.getItem() == Items.LINGERING_POTION;
    }

    public static boolean isAdditionalItemPickup( final ItemStack item )
    {
        return item.is(ModItemTags.IS_ADDITIONAL_ITEM_PICKUP);
    }

    // Is it a plant that should be broken
    public static boolean breakablePlant(final BlockState state, final Block above, final Block below )
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

    // ---------- Code from https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/common/entity/goal/carbuncle/StarbyTransportBehavior.java ----------

    public static boolean doesItemMatchItemInFrameOnChest(FairyEntity fairy, BlockEntity tile, ItemStack stack)
    {
        List<ItemFrame> list = fairy.level.getEntitiesOfClass(ItemFrame.class, new AABB(tile.getBlockPos()).inflate(1));

        if(list == null || list.size() == 0)
        {
            return true;
        }
        else
        {
            for (ItemFrame i : list)
            {
                // Check if these frames are attached to the tile
                BlockEntity adjTile = fairy.level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));

                if (adjTile == null || !adjTile.equals(tile))
                    continue;

                if (i.getItem().isEmpty())
                    continue;

                if (i.getItem().getItem() != stack.getItem())
                {
                    return false;
                }
                else if (i.getItem().getItem() == stack.getItem())
                {
                    return true;
                }
            }

            return true;
        }
    }

    // --------------------
}
