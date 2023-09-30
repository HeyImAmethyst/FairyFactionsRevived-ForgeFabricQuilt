package net.heyimamethyst.fairyfactions.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy_bed.FairyBedModel;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureAtlas;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FairyBedRenderer extends EntityRenderer<FairyBedEntity>
{
    //private final Map<FairyBedEntity.Type, Pair<ResourceLocation, FairyBedModel>> fairyBedResources;
    //private final Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, Pair<ResourceLocation, FairyBedModel>> fairyBedResources = new HashMap<>();
    private final Map<Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType>, Pair<FairyBedTextureAtlas, FairyBedModel>> fairyBedResources = new HashMap<>();

    public FairyBedRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.8f;
        //this.fairyBedResources = Stream.of(FairyBedEntity.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/fairy_bed/" + type.getName() + ".png"), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(type))))));

//        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
//        {
//            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
//            {
//                Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);
//                fairyBedResources.put(pair, Pair.of(new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/fairy_bed/" + logType.getName() + "_" + woolType.getName() + ".png-atlas"), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(pair)))));
//            }
//        }

        for (FairyBedEntity.LogType logType : FairyBedEntity.LogType.values())
        {
            for (FairyBedEntity.WoolType woolType : FairyBedEntity.WoolType.values())
            {
                Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> pair = Pair.of(logType, woolType);
                //fairyBedResources.put(pair, Pair.of(FairyBedTextureGenerator.INSTANCE.getSprite(new ResourceLocation(FairyFactions.MOD_ID,"fairy_bed/textures/atlas/entity/fairy_bed/" + logType.getName() + "_" + woolType.getName() + ".png-atlas")), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(pair)))));
                //fairyBedResources.put(pair, Pair.of(FairyBedTextureGenerator.INSTANCE.FAIRY_BED_TEXTURE_ATLASES.get(new ResourceLocation(FairyFactions.MOD_ID,"fairy_bed/textures/atlas/entity/fairy_bed/" + logType.getName() + "_" + woolType.getName() + ".png-atlas")), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(pair)))));

                fairyBedResources.put(pair, Pair.of(FairyBedTextureGenerator.INSTANCE.FAIRY_BED_TEXTURE_ATLASES.get(pair), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(pair)))));
                //fairyBedResources.put(pair, Pair.of(new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/fairy_bed/" + logType.getName() + "_" + woolType.getName() + ".png"), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(pair)))));
            }
        }
    }

    @Override
    public void render(FairyBedEntity fairyBed, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i)
    {
        float k;

        poseStack.pushPose();

        //poseStack.translate(0.0, -1.5, 0.0);
        //poseStack.translate(0.0, 0.375, 0.0);
        poseStack.translate(0.0, -0.525, 0.0);

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - f));
        float h = (float)fairyBed.getHurtTime() - g;
        float j = fairyBed.getDamage() - g;

        if (j < 0.0f)
        {
            j = 0.0f;
        }

        if (h > 0.0f)
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0f * (float)fairyBed.getHurtDir()));
        }

        //Pair<ResourceLocation, FairyBedModel> pair = this.fairyBedResources.get((Object)fairyBed.getFairyBedType());

        Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> typePair = Pair.of(fairyBed.getFairyBedLogType(), fairyBed.getFairyBedWoolType());
        //Pair<ResourceLocation, FairyBedModel> pair = this.fairyBedResources.get(typePair);
        Pair<FairyBedTextureAtlas, FairyBedModel> pair = this.fairyBedResources.get(typePair);

        //ResourceLocation resourceLocation = pair.getFirst();
        FairyBedTextureAtlas textureAtlas = pair.getFirst();
        FairyBedModel fairyBedModel = pair.getSecond();

        //poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        //fairyBedModel.setupAnim(fairyBed, g, 0.0f, -0.1f, 0.0f, 0.0f);

        //VertexConsumer vertexConsumer = multiBufferSource.getBuffer(fairyBedModel.renderType(resourceLocation));
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(fairyBedModel.renderType(textureAtlas.location()));
        fairyBedModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        super.render(fairyBed, f, g, poseStack, multiBufferSource, i);

        poseStack.popPose();

        //FairyBedTextureGenerator.INSTANCE.CopyAtlasTextureToDisk(Minecraft.getInstance().getResourceManager(), textureAtlas);
    }

    @Override
    public ResourceLocation getTextureLocation(FairyBedEntity fairyBed)
    {
        //return this.fairyBedResources.get((Object)boat.getFairyBedType()).getFirst();

        Pair<FairyBedEntity.LogType, FairyBedEntity.WoolType> typePair = Pair.of(fairyBed.getFairyBedLogType(), fairyBed.getFairyBedWoolType());
        //return this.fairyBedResources.get(typePair).getFirst();
        return this.fairyBedResources.get(typePair).getFirst().location();
    }
}
