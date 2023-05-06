package net.heyimamethyst.fairyfactions.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLike;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.level.ServerLevel;

public class ModFabric implements ModInitializer {
    @Override
    public void onInitialize()
    {
        ModFabricLike.init();
    }

//    private void loadEvents()
//    {
//        ServerTickEvents.START_WORLD_TICK.register((ServerLevel world) ->
//        {
//            DoorEvent.onWorldTick(world);
//        });
//    }

}
