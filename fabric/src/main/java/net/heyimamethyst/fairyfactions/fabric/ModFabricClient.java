package net.heyimamethyst.fairyfactions.fabric;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyEyesModel;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyModel;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyProps2Model;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyPropsModel;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyFishHookEntityRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyRenderer;
import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLikeClient;
import net.heyimamethyst.fairyfactions.fabriclike.registry.KeyInputHandler;
import net.heyimamethyst.fairyfactions.registry.ModEntities;

public class ModFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModFabricLikeClient.init();
    }
}
