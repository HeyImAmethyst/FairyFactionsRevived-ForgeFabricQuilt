package net.heyimamethyst.fairyfactions.registry;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.items.FairyWandItem;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.HashMap;
import java.util.Map;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FairyFactions.MOD_ID, Registry.ITEM_REGISTRY);

    public static void Init()
    {
        ITEMS.register();
    }

//    public static final RegistryObject<ModSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistrySupplier<ModSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg", () ->
            new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistrySupplier<FairyWandItem> FAIRY_WAND = ITEMS.register("fairy_wand", () ->
            new FairyWandItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

//    public static final Map<FairyBedEntity.Type, RegistrySupplier<Item>> FAIRY_BED_ITEMS = creatFairyBedItemMap();
//
//    private static Map<FairyBedEntity.Type, RegistrySupplier<Item>> creatFairyBedItemMap()
//    {
//        Map<FairyBedEntity.Type, RegistrySupplier<Item>> map = new HashMap<>();
//
//        for (FairyBedEntity.Type type : FairyBedEntity.Type.values())
//        {
//            map.put(type, createFairyBedName(type));
//        }
//
//        return map;
//    }
//
//    public static RegistrySupplier<Item> createFairyBedName(FairyBedEntity.Type type)
//    {
//        return ITEMS.register(type.getName() + "_fairy_bed", () -> createFairyBed(type));
//    }
//
//    private static Item createFairyBed(FairyBedEntity.Type bedType)
//    {
//        return new FairyBedItem(bedType, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS));
//    }

    public static final Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, RegistrySupplier<FairyBedItem>> FAIRY_BED_ITEMS = creatFairyBedItemMap();

    private static Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, RegistrySupplier<FairyBedItem>> creatFairyBedItemMap()
    {
        Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, RegistrySupplier<FairyBedItem>> map = new HashMap<>();

        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
        {
            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
            {
                Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);
                map.put(pair, createFairyBedName(logType, woolType));
            }
        }

        return map;
    }

    public static RegistrySupplier<FairyBedItem> createFairyBedName(FairyBedEntity.LogType logType, FairyBedEntity.WoolType woolType)
    {
        return ITEMS.register(logType.getName() + "_" + woolType.getName() + "_fairy_bed", () -> createFairyBed(logType, woolType));
    }

    private static FairyBedItem createFairyBed(FairyBedEntity.LogType logType, FairyBedEntity.WoolType woolType)
    {
        return new FairyBedItem(logType, woolType, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS));
    }
}
