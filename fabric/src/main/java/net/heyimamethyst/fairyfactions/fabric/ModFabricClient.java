package net.heyimamethyst.fairyfactions.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLikeClient;

public class ModFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModFabricLikeClient.init();
    }
}
