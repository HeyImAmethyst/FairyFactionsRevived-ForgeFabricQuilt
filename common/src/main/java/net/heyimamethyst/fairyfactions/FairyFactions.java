package net.heyimamethyst.fairyfactions;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.network.ModNetwork;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.heyimamethyst.fairyfactions.registry.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

public class FairyFactions
{
    public static final String MOD_ID = "fairyfactions";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
//    public static final CreativeModeTab EXAMPLE_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "example_tab"), () ->
//            new ItemStack(FairyFactions.EXAMPLE_ITEM.get()));
    
    public static void init()
    {
        ModEntities.Init();
        ModItems.Init();
        ModSounds.Init();

        System.out.println(ModExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }

    /**
     * Find a fairy in the world by entity id. This method was in the base class
     * in the original mod, and I can't find a better place to put it for now...
     *
     * @param player
     * @param fairyID
     * @return The fairy in question, null if not found.
     */
    public static FairyEntity getFairyFromID(ServerPlayer player, int fairyID)
    {
        MinecraftServer server = player.getServer();

        for( ServerLevel dim : server.getAllLevels())
        {
            if (dim != null)
            {
//                List<Entity> entities = new ArrayList<>();
//                entities.add(dim.getEntities().getAll().iterator().next());

//                if (entities != null && !entities.isEmpty())
//                {
//                    for (Entity entity : entities)
//                    {
//                        if (entity instanceof FairyEntity && entity.getId() == fairyID)
//                            return (FairyEntity) entity;
//                    }
//                }

                Entity entity = dim.getEntities().get(fairyID);

                if (entity instanceof FairyEntity && entity.getId() == fairyID)
                    return (FairyEntity) entity;
            }
        }

        return null;
    }

    /**
     * Find a fairy in the world by entity uuid.
     *
     * @param player
     * @param fairyUUID
     * @return The fairy in question, null if not found.
     */
    public static FairyEntity getFairyFromUUID(ServerPlayer player, UUID fairyUUID)
    {
        MinecraftServer server = player.getServer();

        for( ServerLevel dim : server.getAllLevels())
        {
            if (dim != null)
            {
//                List<Entity> entities = new ArrayList<>();
//                entities.add(dim.getEntities().getAll().iterator().next());
//
//                if (entities != null && !entities.isEmpty())
//                {
//                    for (Entity entity : entities)
//                    {
//                        if (entity instanceof FairyEntity && entity.getUUID() == fairyUUID)
//                            return (FairyEntity) entity;
//                    }
//                }

                Entity entity = dim.getEntities().get(fairyUUID);

                if (entity instanceof FairyEntity && entity.getUUID() == fairyUUID)
                    return (FairyEntity) entity;
            }
        }

        return null;
    }
}
