package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyPropsModel;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FairyPropsLayer<T extends FairyEntity, M extends FairyPropsModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_FAIRY_PROPS = new ResourceLocation(FairyFactions.MOD_ID, "textures/entity/fairy_props.png");

    private final FairyPropsModel<FairyEntity> fairyProps;

    public FairyPropsLayer(RenderLayerParent<T, M> entityRendererIn, EntityModelSet entityModelSet)
    {
        super(entityRendererIn);
        this.fairyProps = new FairyPropsModel<>(entityModelSet.bakeLayer(ModModelLayers.FAIRY_PROPS_LAYER_LOCATION), 0.0F, 0.0F);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T fairy, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        if(!fairy.queen() && !fairy.normal() && !fairy.rogue())
        {

            fairyProps.flymode = fairy.flymode();
            fairyProps.jobType = fairy.getJob() - 1;
            fairyProps.crouching = fairy.isSitting();
            fairyProps.sinage = fairy.sinage;

            this.fairyProps.setupAnim(fairy, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(TEXTURE_FAIRY_PROPS), false, false);
            this.fairyProps.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        }
    }
}
