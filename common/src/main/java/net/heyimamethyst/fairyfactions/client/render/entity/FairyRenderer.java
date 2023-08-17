package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.fairy.FairyModel;
import net.heyimamethyst.fairyfactions.client.render.entity.layer.fairy.*;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.ClientMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FairyRenderer extends MobRenderer<FairyEntity, FairyModel<FairyEntity>>
{

    public FairyRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        //super(renderManagerIn, new FairyModel(renderManagerIn.bakeLayer(ModModelLayers.FAIRY_LAYER_LOCATION)));
        super(renderManagerIn, new FairyModel<>(renderManagerIn.bakeLayer(ModModelLayers.FAIRY_LAYER_LOCATION), 0.0F, 0.0F), 0.5F);

        this.addLayer(new FairyPropsLayer(this, renderManagerIn.getModelSet()));
        this.addLayer(new FairyProps2Layer(this, renderManagerIn.getModelSet()));
        this.addLayer(new FairyEyesLayer(this, renderManagerIn.getModelSet()));
        this.addLayer(new FairyWitheredLayer(this, renderManagerIn.getModelSet()));
        this.addLayer(new FairyItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }


    @Override
    public void render(FairyEntity fairy, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        poseStack.pushPose();

        FairyModel<FairyEntity> fairyModel = this.getModel();

        //p_115458_.scale(f1, f1, f1);

        fairyModel.sinage = fairy.sinage;

        fairyModel.flymode = fairy.flymode();
        fairyModel.showCrown = fairy.tamed() || fairy.queen();
        fairyModel.crouching = fairy.isSitting();
        fairyModel.scoutWings = fairy.scout();
        fairyModel.rogueParts = fairy.rogue();
        fairyModel.hairType = fairy.hairType();

        if (fairy.isSitting())
        {
            poseStack.translate(0F, -0.3F, 0F);
        }

        super.render(fairy, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);

        poseStack.popPose();
    }

    @Override
    protected boolean shouldShowName(FairyEntity pEntity)
    {
        if (pEntity.getFaction() != 0)
        {
            return true;
        }
        else if (pEntity.tamed())
        {
            if (pEntity.isRuler(ClientMethods.getCurrentPlayer()))
            {
                return true;
            }
            else
            {
                return pEntity == this.entityRenderDispatcher.crosshairPickEntity;
            }
        }
        else
        {
            return super.shouldShowName(pEntity);
        }
    }

    @Override
    protected void renderNameTag(FairyEntity pEntity, Component component, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        double d0 = this.entityRenderDispatcher.distanceToSqr(pEntity);

        //net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(pEntity, d0)
        if (!(d0 > 4096.0))
        {
            if(component != null)
            {
                boolean flag = !pEntity.isDiscrete();
                float f = pEntity.getBbHeight() + 0.5F;

                int i = "deadmau5".equals(component.getString()) ? -10 : 0;

                poseStack.pushPose();
                poseStack.translate(0.0D, (double)f, 0.0D);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                poseStack.scale(-0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = poseStack.last().pose();
                float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                int j = (int)(f1 * 255.0F) << 24;

                Font font = this.getFont();

                if(font != null && component != null)
                {
                    float f2 = (float)(-font.width(component.getString()) / 2);
                    font.drawInBatch(component, f2, (float)i, 553648127, false, matrix4f, pBuffer, flag, j, pPackedLight);

                    if (flag)
                    {
                        font.drawInBatch(component, f2, (float)i, -1, false, matrix4f, pBuffer, false, 0, pPackedLight);
                    }
                }

                poseStack.popPose();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FairyEntity fairy)
    {

        final String texturePath;
        int skin = fairy.getSkin();

        if ((fairy.getCustomName() != null && fairy.getCustomName().getString().equals("Steve")) || fairy.getFairyCustomName().equals("Steve"))
        {
            texturePath = "textures/entity/not_fairy.png";
        }
        else
        {
            final int idx;
            if (skin < 0)
            {
                idx = 1;
            }
            else if (skin > 3)
            {
                idx = 4;
            }
            else
            {
                idx = skin + 1;
            }

            texturePath = "textures/entity/fairy" + (fairy.queen() ? "q" : "")
                    + idx + ".png";
        }

        return new ResourceLocation(FairyFactions.MOD_ID, texturePath);
    }

    @Override
    protected void scale(FairyEntity pLivingEntity, PoseStack poseStack, float pPartialTickTime)
    {
        float f1 = 0.875F;
        poseStack.scale(f1, f1, f1);
    }
}
