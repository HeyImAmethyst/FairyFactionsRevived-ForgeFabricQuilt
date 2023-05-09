package net.heyimamethyst.fairyfactions.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FairyItemInHandLayer <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M>
{
    private final ItemInHandRenderer itemInHandRenderer;

    public FairyItemInHandLayer(RenderLayerParent<T, M> renderLayerParent, ItemInHandRenderer p_234847_)
    {
        super(renderLayerParent);
        this.itemInHandRenderer = p_234847_;
    }

    public void render(PoseStack arg, MultiBufferSource arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        boolean bl = arg3.getMainArm() == HumanoidArm.RIGHT;
        ItemStack itemStack = bl ? arg3.getOffhandItem() : arg3.getMainHandItem();
        ItemStack itemStack2 = bl ? arg3.getMainHandItem() : arg3.getOffhandItem();
        if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
            arg.pushPose();
            if (this.getParentModel().young) {
                float m = 0.5F;
                arg.translate(0.0F, 0.75F, 0.0F);
                arg.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderArmWithItem(arg3, itemStack2, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, arg, arg2, i);
            this.renderArmWithItem(arg3, itemStack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, arg, arg2, i);
            arg.popPose();
        }
    }

    protected void renderArmWithItem(LivingEntity arg, ItemStack arg2, ItemDisplayContext arg3, HumanoidArm arg4, PoseStack arg5, MultiBufferSource arg6, int i) {
        if (!arg2.isEmpty()) {
            arg5.pushPose();
            ((ArmedModel)this.getParentModel()).translateToHand(arg4, arg5);
            arg5.mulPose(Axis.XP.rotationDegrees(-90.0F));
            arg5.mulPose(Axis.YP.rotationDegrees(180.0F));
            boolean bl = arg4 == HumanoidArm.LEFT;
            arg5.translate((float)(bl ? -1 : 1) / 200.0F, 0.105F, -0.325F);
            this.itemInHandRenderer.renderItem(arg, arg2, arg3, bl, arg5, arg6, i);
            arg5.popPose();
        }
    }
}
