package net.heyimamethyst.fairyfactions.quilt;

import net.fabricmc.api.ClientModInitializer;
import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLikeClient;

public class ModQuiltClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModFabricLikeClient.init();
    }
}
