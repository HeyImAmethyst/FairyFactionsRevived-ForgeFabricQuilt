package net.heyimamethyst.fairyfactions.fabric;

import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLike;
import net.fabricmc.api.ModInitializer;

public class ModFabric implements ModInitializer
{
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
