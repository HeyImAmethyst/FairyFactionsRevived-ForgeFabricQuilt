package net.heyimamethyst.fairyfactions.util;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


//Class from : https://github.com/baileyholl/Ars-Nouveau/blob/main/src/main/java/com/hollingsworth/arsnouveau/api/util/NBTUtil.java

public class NBTUtil {

    public static CompoundTag storeBlockPos(CompoundTag tag, String prefix, BlockPos pos) {
        if (pos == null)
            return tag;
        tag.putDouble(prefix + "_x", pos.getX());
        tag.putDouble(prefix + "_y", pos.getY());
        tag.putDouble(prefix + "_z", pos.getZ());
        return tag;
    }

    public static CompoundTag removeBlockPos(CompoundTag tag, String prefix) {
        tag.remove(prefix + "_x");
        tag.remove(prefix + "_y");
        tag.remove(prefix + "_z");
        return tag;
    }

    @Deprecated
    public static BlockPos getBlockPos(CompoundTag tag, String prefix) {
        return new BlockPos((int) tag.getDouble(prefix + "_x"), (int) tag.getDouble(prefix + "_y"), (int) tag.getDouble(prefix + "_z"));
    }

    public static @Nullable BlockPos getNullablePos(CompoundTag tag, String prefix) {
        if(!tag.contains(prefix + "_x"))
            return null;
        return new BlockPos((int) tag.getDouble(prefix + "_x"), (int) tag.getDouble(prefix + "_y"), (int) tag.getDouble(prefix + "_z"));
    }


    public static boolean hasBlockPos(CompoundTag tag, String prefix) {
        return tag.contains(prefix + "_x");
    }

    public static List<ItemStack> readItems(CompoundTag tag, String prefix) {
        List<ItemStack> stacks = new ArrayList<>();

        if (tag == null)
            return stacks;
        try {
            CompoundTag itemsTag = tag.getCompound(prefix + "_tag");
            int numItems = itemsTag.getInt("itemsSize");
            for (int i = 0; i < numItems; i++) {
                String key = prefix + "_" + i;
                stacks.add(ItemStack.of(itemsTag.getCompound(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stacks;
    }

    public static void writeItems(CompoundTag tag, String prefix, List<ItemStack> items) {
        CompoundTag allItemsTag = new CompoundTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            CompoundTag itemTag = new CompoundTag();
            stack.save(itemTag);
            allItemsTag.put(prefix + "_" + i, itemTag);
        }
        allItemsTag.putInt("itemsSize", items.size());
        tag.put(prefix + "_tag", allItemsTag);
    }


    public static List<String> readStrings(CompoundTag tag, String prefix) {
        List<String> strings = new ArrayList<>();
        if (tag == null)
            return strings;

        for (String s : tag.getAllKeys()) {
            if (s.contains(prefix)) {
                strings.add(tag.getString(s));
            }
        }
        return strings;
    }

    public static void writeStrings(CompoundTag tag, String prefix, Collection<String> strings) {
        int i = 0;
        for (String s : strings) {
            tag.putString(prefix + "_" + i, s);
            i++;
        }
    }

    public static void writeResourceLocations(CompoundTag tag, String prefix, Collection<ResourceLocation> resourceLocations) {
        writeStrings(tag, prefix, resourceLocations.stream().map(ResourceLocation::toString).collect(Collectors.toList()));
    }

    public static List<ResourceLocation> readResourceLocations(CompoundTag tag, String prefix) {
        return readStrings(tag, prefix).stream().map(ResourceLocation::new).collect(Collectors.toList());
    }

//    public static String getItemKey(ItemStack stack, String prefix)
//    {
//        return prefix + Registries.getId(stack.getItem(), (ResourceKey<Registry<Item>>) null).toString();
//    }
}
