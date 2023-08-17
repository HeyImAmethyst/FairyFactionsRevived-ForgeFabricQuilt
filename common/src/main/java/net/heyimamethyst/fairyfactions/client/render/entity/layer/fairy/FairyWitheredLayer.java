package net.heyimamethyst.fairyfactions.client.render.entity.layer.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyModel;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FairyWitheredLayer <T extends FairyEntity, M extends FairyModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_FAIRY_WITHERED = new ResourceLocation(FairyFactions.MOD_ID, "textures/entity/fairy_withered1.png");
    private static final ResourceLocation TEXTURE_FAIRY_WITHERED2 = new ResourceLocation(FairyFactions.MOD_ID, "textures/entity/fairy_withered2.png");
    private static final ResourceLocation TEXTURE_FAIRY_WITHERED3 = new ResourceLocation(FairyFactions.MOD_ID, "textures/entity/fairy_withered3.png");

    private final FairyModel<FairyEntity> model;

    public FairyWitheredLayer(RenderLayerParent<T, M> entityRendererIn, EntityModelSet p_174494_)
    {
        super(entityRendererIn);
        this.model = new FairyModel<>(p_174494_.bakeLayer(ModModelLayers.FAIRY_WITHERED_LAYER_LOCATION), 0.015625F, 0.0F);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T fairy, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        matrixStackIn.pushPose();

        if(fairy.withered() || fairy.rogue())
        {
            model.sinage = fairy.sinage;

            model.flymode = fairy.flymode();
            model.showCrown = fairy.tamed() || fairy.queen();
            model.crouching = fairy.isSitting();
            model.scoutWings = fairy.scout();
            model.rogueParts = fairy.rogue();
            model.hairType = fairy.hairType();

            this.model.setupAnim(fairy, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(TEXTURE_FAIRY_WITHERED), false, false);
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FairyEntity fairy)
    {
        if(fairy.withered() || fairy.rogue())
        {
            if (fairy.queen())
            {
                if (fairy.getSkin() > 1)
                {
                    return TEXTURE_FAIRY_WITHERED3;
                }
                else
                {
                    return TEXTURE_FAIRY_WITHERED2;
                }
            }
            else
            {
                return TEXTURE_FAIRY_WITHERED;
            }
        }

        return TEXTURE_FAIRY_WITHERED;
    }
}
