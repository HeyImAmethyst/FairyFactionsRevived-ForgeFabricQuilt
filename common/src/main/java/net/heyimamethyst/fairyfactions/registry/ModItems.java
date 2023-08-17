package net.heyimamethyst.fairyfactions.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.HashMap;
import java.util.Map;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FairyFactions.MOD_ID, Registries.ITEM);

    public static void Init()
    {
        ITEMS.register();
    }

//    public static final RegistryObject<ModSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistrySupplier<Item> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg", () ->
            new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties()));

    public static final Map<FairyBedEntity.Type, RegistrySupplier<Item>> FAIRY_BED_ITEMS = creatFairyBedItemMap();

    private static Map<FairyBedEntity.Type, RegistrySupplier<Item>> creatFairyBedItemMap()
    {
        Map<FairyBedEntity.Type, RegistrySupplier<Item>> map = new HashMap<>();

        for (FairyBedEntity.Type type : FairyBedEntity.Type.values())
        {
            map.put(type, createFairyBedName(type));
        }

        return map;
    }

    public static RegistrySupplier<Item> createFairyBedName(FairyBedEntity.Type type)
    {
        return ITEMS.register(type.getName() + "_fairy_bed", () -> createFairyBed(type));
    }

    private static Item createFairyBed(FairyBedEntity.Type bedType)
    {
        return new FairyBedItem(bedType, new Item.Properties().stacksTo(1));
    }
}
