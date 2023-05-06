package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.FairyProps2Model;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FairyProps2Layer<T extends FairyEntity, M extends FairyProps2Model<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_FAIRY_PROPS2 = new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/fairy_props2.png");

    private final FairyProps2Model<FairyEntity> fairyProps2;

    public FairyProps2Layer(RenderLayerParent<T, M> entityRendererIn, EntityModelSet entityModelSet)
    {
        super(entityRendererIn);
        this.fairyProps2 = new FairyProps2Model<>(entityModelSet.bakeLayer(ModModelLayers.FAIRY_PROPS2_LAYER_LOCATION), 0.0F, 0.0F);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T fairy, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if(!fairy.queen() && !fairy.normal())
        {
            if(fairy.rogue())
            {
                fairyProps2.flymode = fairy.flymode();
                fairyProps2.crouching = fairy.isSitting();
                fairyProps2.sinage = fairy.sinage;

                this.fairyProps2.setupAnim(fairy, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(TEXTURE_FAIRY_PROPS2), false, false);
                this.fairyProps2.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
