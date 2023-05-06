package net.heyimamethyst.fairyfactions.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class FairyEyesModel <T extends LivingEntity> extends HumanoidModel<T>
{
    public boolean flymode = true;

    public FairyEyesModel(ModelPart root)
    {
        super(root);
        this.hat.visible = false;
        this.crouching = false;
        this.flymode = false;
    }

    public static LayerDefinition createBodyLayer()
    {

        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        //MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition partdefinition = meshdefinition.getRoot();

        float f = 0.0f;
        float f1 = 0.0f;

        partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(40, 0).addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6, new CubeDeformation(f + 0.01F)), PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {
        pPoseStack.pushPose();

        this.head.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        pPoseStack.popPose();
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        head.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        head.xRot = pHeadPitch / (180F / (float)Math.PI);

        if (flymode)
        {
            head.z = -3F;
            head.y = 19.75F;
        }
        else
        {
            head.z = 0.0F;
            head.y = 12F;
        }
    }
}
