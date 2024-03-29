package net.heyimamethyst.fairyfactions.fabriclike;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyEyesModel;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyModel;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyProps2Model;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyPropsModel;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy_bed.FairyBedModel;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyBedRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyFishHookEntityRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyRenderer;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.fabriclike.registry.KeyInputHandler;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ModFabricLikeClient
{
    public static void init()
    {
//        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
//        resourceManagerHelper.registerReloadListener(new ReloadListenerWrapper<>(
//                new ResourceLocation("fairyfactions", "fairy_bed_texture_generator"),
//                FairyBedTextureGenerator.INSTANCE
//        ));

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
