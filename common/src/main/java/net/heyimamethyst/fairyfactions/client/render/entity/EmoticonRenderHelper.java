package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EmoticonRenderHelper
{
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/emoticon.png");
    private static final ResourceLocation SLEEPING_TEXTURE_LOCATION = new ResourceLocation(FairyFactions.MOD_ID,"textures/entity/sleeping_emoticon.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final RenderType SLEEPING_RENDER_TYPE = RenderType.entityCutout(SLEEPING_TEXTURE_LOCATION);

    public static void renderEmoticon(FairyEntity fairy, Font font, Quaternionf cameraOrientation, PoseStack poseStack, MultiBufferSource pBuffer,
                                      double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, int light)
    {
        float f = fairy.getBbHeight() + 0.5F;

        poseStack.pushPose();

        poseStack.translate(0.0D, fairy.isSleeping() ? (double)(f + 0.5F) : (double)f + 0.3F, 0.0D);

        poseStack.mulPose(cameraOrientation);

        if(fairy.isEmotional() && !fairy.isSleeping())
        {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
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
            {
                renderIcon(new ItemStack(fairy.getWantedFoodItem()), poseStack, pBuffer, fairy.level(), light);
                renderText(fairy, new ItemStack(fairy.getWantedFoodItem()), font, cameraOrientation, poseStack, pBuffer, light);
            }

            poseStack.popPose();

        }

        if(fairy.isSleeping())
        {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
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

    private static void renderIcon(ItemStack icon, PoseStack poseStack, MultiBufferSource buffers, Level level, int combinedLightIn)
    {
        if (!icon.isEmpty())
        {
            final float iconScale = 0.4F;

            poseStack.pushPose();

            poseStack.translate(0, 0.2F, 0);

            poseStack.scale(iconScale, iconScale, iconScale);
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
            Minecraft.getInstance().getItemRenderer().renderStatic(icon, ItemDisplayContext.NONE, 0xF000F0,
                    OverlayTexture.NO_OVERLAY, poseStack, buffers, level, 0);

            poseStack.popPose();
        }
    }

    private static void renderText(FairyEntity fairy, ItemStack icon, Font font, Quaternionf cameraOrientation, PoseStack poseStack, MultiBufferSource buffers, int combinedLightIn)
    {

        final float textScale = 0.005f;

        Component component = icon.getHoverName();
        MutableComponent mutableComponent = Component.literal(component.getString()).withStyle(ChatFormatting.BLACK);
        String text = mutableComponent.getString();

        boolean bl = !fairy.isDiscrete();
        //float f = fairy.getBbHeight() + 0.5F;
        int j = "deadmau5".equals(text) ? -10 : 0;

        poseStack.pushPose();

        poseStack.scale(-textScale, -textScale, textScale);

        poseStack.translate(0, 0.9F, 0);

        Matrix4f matrix4f = poseStack.last().pose();
        float opacity  = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int alpha  = (int)(opacity  * 255.0f) << 24;
        float width  = (float)(-font.width(component) / 2);

        //font.drawInBatch(mutableComponent, width , (float)j, 0x20FFFFFF, false, matrix4f, buffers, bl, alpha , combinedLightIn);
        //font.drawInBatch(mutableComponent, width , (float)j, -1, false, matrix4f, buffers, false, 0, combinedLightIn);

        font.drawInBatch(mutableComponent, width, 0f, -1, false, matrix4f, buffers, Font.DisplayMode.NORMAL, 0, combinedLightIn);
        //font.drawInBatch8xOutline(mutableComponent.getVisualOrderText(), width, 0f, -1, 0, matrix4f, buffers, combinedLightIn);

        poseStack.popPose();
    }
}
