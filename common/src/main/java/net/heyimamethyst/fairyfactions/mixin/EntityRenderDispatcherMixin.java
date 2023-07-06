package net.heyimamethyst.fairyfactions.mixin;


import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Quaternion;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.client.render.entity.EmoticonRenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Code from https://github.com/VazkiiMods/Neat/blob/master/Xplat/src/main/java/vazkii/neat/mixin/EntityRenderDispatcherMixin.java
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
    @Shadow
    public abstract Quaternion cameraOrientation();

    @Inject(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            shift = At.Shift.AFTER
    )
    )
    private void renderFairyEmoticon(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci)
    {

        if(entity instanceof FairyEntity)
        {
            FairyEntity fairy = (FairyEntity) entity;

            EmoticonRenderHelper.renderEmoticon(fairy, cameraOrientation(), poseStack, buffers , worldX, worldY, worldZ, entityYRot, partialTicks, light);
        }
    }
}