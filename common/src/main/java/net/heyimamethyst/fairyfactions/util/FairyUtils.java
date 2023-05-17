package net.heyimamethyst.fairyfactions.util;


import com.google.common.collect.Maps;
import net.heyimamethyst.fairyfactions.Loc;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.FairyEntityBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

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
            entity.setCustomName(new TextComponent(name));
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
        if (block instanceof BambooBlock) return true;
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
        return fairy.getRandom().nextDouble() <= chance;
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
    public static boolean acceptableFoods(FairyEntity fairy, Item i)
    {
        if (i == Items.GLISTERING_MELON_SLICE)
        {
            return true;
        }
        else if (fairy.tamed() || !fairy.queen())
        {
            return i == Items.APPLE || i == Items.MELON_SLICE || i == Items.SUGAR
                    || i == Items.CAKE || i == Items.COOKIE;
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
    public static boolean haircutItem(Item i)
    {
        return i == Items.SHEARS;
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

    public static boolean peacefulAnimal(Animal animal)
    {
        Class thing = animal.getClass();

        return thing == Pig.class || thing == Cow.class
                || thing == Chicken.class || thing == Sheep.class
                || thing == MushroomCow.class;
    }
}
