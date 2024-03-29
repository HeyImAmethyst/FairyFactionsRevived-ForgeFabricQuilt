package net.heyimamethyst.fairyfactions.client.render.entity.layer.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FairyItemInHandLayer <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M>
{
    public FairyItemInHandLayer(RenderLayerParent<T, M> renderLayerParent)
    {
        super(renderLayerParent);
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        //ItemStack itemstack = p_117207_.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack itemstack = pLivingEntity.getMainHandItem();

        if (!itemstack.isEmpty() /*|| !itemstack1.isEmpty()*/)
        {
            pPoseStack.pushPose();
            if (this.getParentModel().young)
            {
                float f = 0.5F;
                pPoseStack.translate(0.0D, 0.75D, 0.0D);
                pPoseStack.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderArmWithItem(pLivingEntity, itemstack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }
    }

    protected void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_117191_)
    {

        if (!itemStack.isEmpty())
        {
            poseStack.pushPose();
            this.getParentModel().translateToHand(humanoidArm, poseStack);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = humanoidArm == HumanoidArm.LEFT;
            poseStack.translate((double)((float)(flag ? -1 : 1) / 200.0F), 0.105D, -0.325D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, itemStack, transformType, flag, poseStack, multiBufferSource, p_117191_);
            poseStack.popPose();
        }
    }
}
