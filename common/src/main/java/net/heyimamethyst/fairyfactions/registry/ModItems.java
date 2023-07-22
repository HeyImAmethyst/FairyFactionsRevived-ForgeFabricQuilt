package net.heyimamethyst.fairyfactions.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.heyimamethyst.fairyfactions.items.FairyBedItem;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FairyFactions.MOD_ID, Registry.ITEM_REGISTRY);

    public static void Init()
    {
        ITEMS.register();
    }

//    public static final RegistryObject<ModSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg",
//            () -> new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistrySupplier<Item> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg", () ->
            new ModSpawnEggItem(ModEntities.FAIRY_ENTITY, 0xea8fde, 0x8658bf, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistrySupplier<Item> OAK_WHITE_FAIRY_BED = ITEMS.register("oak_white_fairy_bed", () ->
        new FairyBedItem(FairyBedEntity.Type.OAK_WHITE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_ORANGE_FAIRY_BED = ITEMS.register("oak_orange_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_ORANGE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_MAGENTA_FAIRY_BED = ITEMS.register("oak_magenta_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_MAGENTA, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_LIGHT_BLUE_FAIRY_BED = ITEMS.register("oak_light_blue_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_LIGHT_BLUE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_YELLOW_FAIRY_BED = ITEMS.register("oak_yellow_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_YELLOW, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_LIME_FAIRY_BED = ITEMS.register("oak_lime_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_LIME, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_PINK_FAIRY_BED = ITEMS.register("oak_pink_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_PINK, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_GRAY_FAIRY_BED = ITEMS.register("oak_gray_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_GRAY, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_LIGHT_GRAY_FAIRY_BED = ITEMS.register("oak_light_gray_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_LIGHT_GRAY, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_CYAN_FAIRY_BED = ITEMS.register("oak_cyan_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_CYAN, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_PURPLE_FAIRY_BED = ITEMS.register("oak_purple_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_PURPLE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_BLUE_FAIRY_BED = ITEMS.register("oak_blue_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_BLUE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_BROWN_FAIRY_BED = ITEMS.register("oak_brown_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_BROWN, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_GREEN_FAIRY_BED = ITEMS.register("oak_green_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_GREEN, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_RED_FAIRY_BED = ITEMS.register("oak_red_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_RED, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistrySupplier<Item> OAK_BLACK_FAIRY_BED = ITEMS.register("oak_black_fairy_bed", () ->
            new FairyBedItem(FairyBedEntity.Type.OAK_BLACK, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_DECORATIONS)));
}
