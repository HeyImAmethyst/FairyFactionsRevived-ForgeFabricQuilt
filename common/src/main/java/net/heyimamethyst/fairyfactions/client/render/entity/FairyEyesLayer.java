package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyEyesModel;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FairyEyesLayer<T extends FairyEntity, M extends FairyEyesModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_FAIRY_EYES = new ResourceLocation(FairyFactions.MOD_ID, "textures/entity/fairy1.png");

    private final FairyEyesModel<FairyEntity> fairyEyes;

    public FairyEyesLayer(RenderLayerParent<T, M> entityRendererIn, EntityModelSet entityModelSet)
    {
        super(entityRendererIn);
        this.fairyEyes = new FairyEyesModel<>(entityModelSet.bakeLayer(ModModelLayers.FAIRY_EYES_LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T fairy, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {

        float transp = 1.0F - ((float)fairy.getHealth() / (float)(fairy.getMaxHealth()));

        if (transp < 0.1F)
        {
            return;
        }

        fairyEyes.flymode = fairy.flymode();

        this.fairyEyes.setupAnim(fairy, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityTranslucent(TEXTURE_FAIRY_EYES), false, false);
        this.fairyEyes.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, transp);

    }

    @Override
    public ResourceLocation getTextureLocation(FairyEntity fairy)
    {
        return TEXTURE_FAIRY_EYES;
    }
}
