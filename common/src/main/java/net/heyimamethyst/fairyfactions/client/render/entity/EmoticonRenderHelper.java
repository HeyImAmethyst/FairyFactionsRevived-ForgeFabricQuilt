package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EmoticonRenderHelper
{
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/emoticon.png");
    private static final ResourceLocation SLEEPING_TEXTURE_LOCATION = new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/sleeping_emoticon.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final RenderType SLEEPING_RENDER_TYPE = RenderType.entityCutout(SLEEPING_TEXTURE_LOCATION);

    public static void renderEmoticon(FairyEntity fairy, Quaternion cameraOrientation, PoseStack poseStack, MultiBufferSource pBuffer,
                                      double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, int light)
    {
        float f = fairy.getBbHeight() + 0.5F;

        poseStack.pushPose();

        poseStack.translate(0.0D, fairy.isSleeping() ? (double)(f + 0.5F) : (double)f, 0.0D);

        poseStack.mulPose(cameraOrientation);

        if(fairy.isEmotional() && !fairy.isSleeping())
        {
            poseStack.pushPose();

            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            VertexConsumer builder = pBuffer.getBuffer(RENDER_TYPE);

            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            vertex(builder, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
            vertex(builder, matrix4f, matrix3f, light, 1.0F, 0, 1, 1);
            vertex(builder, matrix4f, matrix3f, light, 1.0F, 1, 1, 0);
            vertex(builder, matrix4f, matrix3f, light, 0.0F, 1, 0, 0);

            poseStack.popPose();

            poseStack.pushPose();

            if(fairy.getWantedFoodItem() != null)
                renderIcon(new ItemStack(fairy.getWantedFoodItem()), poseStack, pBuffer);

            poseStack.popPose();

        }

        if(fairy.isSleeping())
        {
            poseStack.pushPose();

            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            VertexConsumer builder = pBuffer.getBuffer(SLEEPING_RENDER_TYPE);

            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            vertex(builder, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
            vertex(builder, matrix4f, matrix3f, light, 1.0F, 0, 1, 1);
            vertex(builder, matrix4f, matrix3f, light, 1.0F, 1, 1, 0);
            vertex(builder, matrix4f, matrix3f, light, 0.0F, 1, 0, 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer arg, Matrix4f arg2, Matrix3f arg3, int i, float f, int j, int k, int l)
    {
        arg.vertex(arg2, f - 0.5F, (float)j - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)k, (float)l).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(arg3, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void renderIcon(ItemStack icon, PoseStack poseStack, MultiBufferSource buffers)
    {
        if (!icon.isEmpty())
        {
            final float iconScale = 0.4F;

            poseStack.pushPose();

            poseStack.translate(0, 0.2F, 0);

            poseStack.scale(iconScale, iconScale, iconScale);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            Minecraft.getInstance().getItemRenderer().renderStatic(icon, ItemTransforms.TransformType.NONE, 0xF000F0,
                    OverlayTexture.NO_OVERLAY, poseStack, buffers, 0);

            poseStack.popPose();
        }
    }
}
