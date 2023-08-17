package net.heyimamethyst.fairyfactions.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy_bed.FairyBedModel;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.stream.Stream;

public class FairyBedRenderer extends EntityRenderer<FairyBedEntity>
{
    private final Map<FairyBedEntity.Type, Pair<ResourceLocation, FairyBedModel>> fairyBedResources;

    public FairyBedRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.8f;
        this.fairyBedResources = Stream.of(FairyBedEntity.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/fairy_bed/" + type.getName() + ".png"), new FairyBedModel(context.bakeLayer(ModModelLayers.FAIRY_BED_LAYER_LOCATION.get(type))))));
    }

    @Override
    public void render(FairyBedEntity fairyBed, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i)
    {
        float k;

        poseStack.pushPose();

        //poseStack.translate(0.0, -1.5, 0.0);
        //poseStack.translate(0.0, 0.375, 0.0);
        poseStack.translate(0.0, -0.525, 0.0);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f));
        float h = (float)fairyBed.getHurtTime() - g;
        float j = fairyBed.getDamage() - g;

        if (j < 0.0f)
        {
            j = 0.0f;
        }

        if (h > 0.0f)
        {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0f * (float)fairyBed.getHurtDir()));
        }

        Pair<ResourceLocation, FairyBedModel> pair = this.fairyBedResources.get((Object)fairyBed.getFairyBedType());
        ResourceLocation resourceLocation = pair.getFirst();
        FairyBedModel fairyBedModel = pair.getSecond();

        //poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        //fairyBedModel.setupAnim(fairyBed, g, 0.0f, -0.1f, 0.0f, 0.0f);

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(fairyBedModel.renderType(resourceLocation));
        fairyBedModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        super.render(fairyBed, f, g, poseStack, multiBufferSource, i);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FairyBedEntity boat)
    {
        return this.fairyBedResources.get((Object)boat.getFairyBedType()).getFirst();
    }
}
