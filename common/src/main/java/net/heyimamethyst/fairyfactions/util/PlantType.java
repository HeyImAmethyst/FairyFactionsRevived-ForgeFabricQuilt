package net.heyimamethyst.fairyfactions.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PlantType
{
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case. And use _ to separate words.
    private static final Map<String, PlantType> VALUES = new ConcurrentHashMap<>();

    public static final PlantType PLAINS = get("plains");
    public static final PlantType DESERT = get("desert");
    public static final PlantType BEACH = get("beach");
    public static final PlantType CAVE = get("cave");
    public static final PlantType WATER = get("water");
    public static final PlantType NETHER = get("nether");
    public static final PlantType CROP = get("crop");

    public static PlantType get(String name)
    {
        return VALUES.computeIfAbsent(name, e ->
        {
            if (INVALID_CHARACTERS.matcher(e).find())
                throw new IllegalArgumentException("PlantType.get() called with invalid name: " + name);
            return new PlantType(e);
        });
    }

    private final String name;

    private PlantType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
