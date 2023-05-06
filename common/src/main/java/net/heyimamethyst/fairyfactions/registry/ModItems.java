package net.heyimamethyst.fairyfactions.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
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
}
