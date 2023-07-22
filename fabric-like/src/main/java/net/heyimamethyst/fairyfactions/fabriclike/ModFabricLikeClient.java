package net.heyimamethyst.fairyfactions.fabriclike;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.*;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyBedRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyFishHookEntityRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyRenderer;
import net.heyimamethyst.fairyfactions.fabriclike.registry.KeyInputHandler;
import net.heyimamethyst.fairyfactions.registry.ModEntities;

public class ModFabricLikeClient
{
    public static void init()
    {
        EntityModelLayerRegistry.register(ModModelLayers.FAIRY_LAYER_LOCATION, FairyModel::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayers.FAIRY_EYES_LAYER_LOCATION, FairyEyesModel::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayers.FAIRY_PROPS_LAYER_LOCATION, FairyPropsModel::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayers.FAIRY_PROPS2_LAYER_LOCATION, FairyProps2Model::createBodyLayer);
        EntityModelLayerRegistry.register(ModModelLayers.FAIRY_WITHERED_LAYER_LOCATION, FairyModel::createBodyLayer);

        ModModelLayers.FAIRY_BED_LAYER_LOCATION.values().forEach(fairyBed ->
        {
            EntityModelLayerRegistry.register(fairyBed, FairyBedModel::createBodyLayer);
        });

        EntityRendererRegistry.register(ModEntities.FAIRY_ENTITY, FairyRenderer::new);
        EntityRendererRegistry.register(ModEntities.FAIRY_FISHING_BOBBER_ENTITY, FairyFishHookEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.FAIRY_BED_ENTITY, FairyBedRenderer::new);

        KeyInputHandler.register();
    }
}
