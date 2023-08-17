package net.heyimamethyst.fairyfactions.client.model.entity.fairy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class FairyProps2Model<T extends LivingEntity> extends HumanoidModel<T>
{
    public ModelPart rogueHead;
    public ModelPart rogueBody;
    public ModelPart rogueLeftArm;
    public ModelPart rogueRightArm;
    public ModelPart rogueLeftLeg;
    public ModelPart rogueRightLeg;
    public ModelPart wingLeft;
    public ModelPart wingRight;

    public boolean flymode = true;
    public float retract;
    public boolean venom;

    public float sinage;

    static float f;
    static float f1;

    public FairyProps2Model(ModelPart root, final float f, final float f1)
    {
        super(root);
        this.hat.visible = false;
        flymode = false;
        retract = 0F;

        rogueHead = root.getChild("rogueHead");
        rogueBody = root.getChild("rogueBody");
        rogueLeftArm = root.getChild("rogueLeftArm");
        rogueRightArm = root.getChild("rogueRightArm");
        rogueLeftLeg = root.getChild("rogueLeftLeg");
        rogueRightLeg = root.getChild("rogueRightLeg");

        wingLeft = root.getChild("wingLeft");
        wingRight = root.getChild("wingRight");

        this.f = f;
        this.f1 = f1;
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        //MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition partdefinition = meshdefinition.getRoot();

        //Rogue Type
        partdefinition.addOrReplaceChild(
                "rogueHead",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3F, -5.7F, -3F, 6, 6, 6, new CubeDeformation(f + 0.4F))
                        .texOffs(0, 0).mirror().addBox(-3F, -6.0F, -3F, 6, 6, 6, new CubeDeformation(f + 0.125F)), 
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "rogueBody",
                CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-2F, 0.0F, -1F, 4, 6, 2, new CubeDeformation(f + 0.375F))
                        .texOffs(12, 19).addBox(-2F, 1.0F, -2F, 4, 2, 1, new CubeDeformation(f + 0.375F)), 
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "rogueRightArm",
                CubeListBuilder.create()
                        .texOffs(0, 12).addBox(-1F, -1F, -1F, 2, 5, 2, new CubeDeformation(f + 0.375F)), 
                PartPose.offset(-5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "rogueLeftArm",
                CubeListBuilder.create()
                        .texOffs(0, 12).mirror().addBox(-1F, -1F, -1F, 2, 5, 2, new CubeDeformation(f + 0.375F)), 
                PartPose.offset(5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "rogueRightLeg",
                CubeListBuilder.create()
                        .texOffs(12, 12).addBox(-1F, 0.0F, -1F, 2, 5, 2, new CubeDeformation(f + 0.375F)), 
                PartPose.offset(-1F, 18F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "rogueLeftLeg",
                CubeListBuilder.create()
                        .texOffs(12, 12).mirror().addBox(-1F, 0.0F, -1F, 2, 5, 2, new CubeDeformation(f + 0.375F)), 
                PartPose.offset(1.0F, 18F + f1, 0.0F));

        PartDefinition wingRight = partdefinition.addOrReplaceChild(
                "wingRight",
                CubeListBuilder.create()
                        .texOffs(12, 25).addBox(0F, -1.75F, -1.0F, 6, 6, 1, new CubeDeformation(f + 0.25F)), 
                PartPose.offset(0.5F, 0.0F + f1, 1.0F));

        PartDefinition wingLeft = partdefinition.addOrReplaceChild(
                "wingLeft",
                CubeListBuilder.create()
                        .texOffs(12, 25).mirror().addBox(-6F, -1.75F, -1.0F, 6, 6, 1, new CubeDeformation(f + 0.25F)).mirror(false), 
                PartPose.offset(-0.5F, 0.0F + f1, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {

        pPoseStack.pushPose();

        this.rogueHead.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rogueBody.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rogueRightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rogueLeftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rogueRightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rogueLeftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.wingLeft.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.wingRight.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        pPoseStack.popPose();
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        float attackTime = pEntity.attackAnim;
        sinage = ((FairyEntity)pEntity).sinage;

        rogueHead.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        rogueHead.xRot = pHeadPitch / (180F / (float)Math.PI);

        if (!flymode)
        {
            rogueRightArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
            rogueLeftArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
            rogueRightLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
            rogueLeftLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount;
        }
        else
        {
            rogueRightArm.xRot = 0.0F;
            rogueLeftArm.xRot = 0.0F;
            rogueRightLeg.xRot = 0.0F;
            rogueLeftLeg.xRot = 0.0F;
        }

        rogueRightArm.zRot = 0.05F;
        rogueLeftArm.zRot = -0.05F;
        rogueRightLeg.yRot = 0.0F;
        rogueLeftLeg.yRot = 0.0F;
        rogueRightLeg.zRot = 0.0F;
        rogueLeftLeg.zRot = 0.0F;

        if ((this.riding || this.crouching) && !flymode)
        {
            rogueRightArm.xRot += -((float)Math.PI / 5F);
            rogueLeftArm.xRot += -((float)Math.PI / 5F);
            rogueRightLeg.xRot = -((float)Math.PI * 2F / 5F);
            rogueLeftLeg.xRot = -((float)Math.PI * 2F / 5F);
            rogueRightLeg.yRot = ((float)Math.PI / 10F);
            rogueLeftLeg.yRot = -((float)Math.PI / 10F);

            if (this.crouching)
            {
                rogueRightLeg.xRot = -((float)Math.PI / 2F);
                rogueLeftLeg.xRot = -((float)Math.PI / 2F);
            }
        }

        boolean flag2 = pEntity.getMainArm() == HumanoidArm.RIGHT;
        if (pEntity.isUsingItem())
        {
            boolean flag3 = pEntity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (flag3 == flag2)
            {
                this.poseRightArm(pEntity);
            }
            else
            {
                this.poseLeftArm(pEntity);
            }
        }
        else
        {
            boolean flag4 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if (flag2 != flag4)
            {
                this.poseLeftArm(pEntity);
                this.poseRightArm(pEntity);
            }
            else
            {
                this.poseRightArm(pEntity);
                this.poseLeftArm(pEntity);
            }
        }

        rogueRightArm.yRot = 0.0F;
        rogueLeftArm.yRot = 0.0F;

        if(attackTime > -9990F)
        {
            float f6 = attackTime;

            rogueBody.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            wingLeft.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            rogueRightArm.z = (float)Math.sin(rogueBody.yRot) * 5F;
            rogueRightArm.x = (float)-Math.cos(rogueBody.yRot) * 5F + 2.0F;

            rogueLeftArm.z = (float)-Math.sin(rogueBody.yRot) * 5F;
            rogueLeftArm.x = (float)Math.cos(rogueBody.yRot) * 5F - 2.0F;

            rogueRightArm.yRot += rogueBody.yRot;

            rogueLeftArm.yRot += rogueBody.yRot;
            rogueLeftArm.xRot += rogueBody.yRot;

            f6 = 1.0F - attackTime;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;

            final float f8 = (float)Math.sin(f6 * (float)Math.PI);
            final float f9 = (float)Math.sin(attackTime * (float)Math.PI) * -(rogueHead.xRot - 0.7F) * 0.75F;

            rogueRightArm.xRot -= f8 * 1.2D + f9;
            rogueRightArm.yRot += rogueBody.yRot * 2.0F;
            rogueRightArm.zRot = (float)Math.sin(attackTime * (float)Math.PI) * -0.4F;
        }

        if (flymode)
        {
            final float f7 = (float)Math.PI;

            rogueBody.xRot = f7 / 2.0F;
            rogueBody.y = 19F;

            wingLeft.xRot = f7 / 2.0F;
            wingRight.xRot = f7 / 2.0F;

            wingLeft.y = 17.5F;
            wingRight.y = 17.5F;

            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            rogueRightLeg.z = 0.0F;
            rogueLeftLeg.z = 0.0F;

            rogueRightArm.y = 19F;
            rogueLeftArm.y = 19F;

            rogueRightLeg.y = 18F;
            rogueLeftLeg.y = 18F;

            rogueRightLeg.z = 6F;
            rogueLeftLeg.z = 6F;

            rogueHead.z = -3F;
            rogueHead.y = 19.75F;
        }
        else
        {
            rogueBody.xRot = 0.0F;
            rogueBody.y = 12F;

            wingLeft.xRot = 0.0F;
            wingRight.xRot = 0.0F;

            wingLeft.y = 12.5F;
            wingRight.y = 12.5F;
            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            rogueRightLeg.z = 0.0F;
            rogueRightLeg.z = 0.0F;

            if (this.riding)
            {
                rogueRightArm.y = 13F;
                rogueLeftArm.y = 13F;
            }
            else
            {
                rogueRightArm.y = 13F;
                rogueLeftArm.y = 13F;
            }

            rogueRightLeg.y = 18F;
            rogueLeftLeg.y = 18F;
            rogueRightLeg.z = 0.0F;
            rogueLeftLeg.z = 0.0F;
            rogueHead.z = 0.0F;
            rogueHead.y = 12F;
        }

        if (flymode)
        {
            //arms bob
            rogueRightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            rogueLeftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            rogueRightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.1F;
            rogueLeftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.1F;

            //legs bob
            rogueRightLeg.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            rogueLeftLeg.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            rogueRightLeg.xRot = 0.1F;
            rogueLeftLeg.xRot = 0.1F;
        }
        else
        {
            //arms bob
            rogueRightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;
            rogueLeftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;

            rogueRightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.05F;
            rogueLeftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.05F;
        }

        if (flymode)
        {
            wingLeft.yRot = 0.1F;
            wingRight.yRot = -0.1F;

            wingLeft.yRot += Math.sin(sinage) / 6F;
            wingRight.yRot -= Math.sin(sinage) / 6F;

            wingLeft.zRot = 0.5F;
            wingRight.zRot = -0.5F;
        }
        else
        {
            wingLeft.yRot = 0.6F;
            wingRight.yRot = -0.6F;

            wingLeft.yRot += Math.sin(sinage) / 3F;
            wingRight.yRot -= Math.sin(sinage) / 3F;

            wingLeft.zRot = 0.125F;
            wingRight.zRot = -0.125F;
        }

        wingLeft.zRot += Math.cos(sinage) / (flymode ? 3F : 8F);
        wingRight.zRot -= Math.cos(sinage) / (flymode ? 3F : 8F);
    }
    
    private void poseRightArm(T pEntity) {
        switch(this.rightArmPose) {
            case EMPTY:
                this.rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
                this.rightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.rightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
                this.rightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
                this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pEntity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case SPYGLASS:
                this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (pEntity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.rightArm.yRot = this.head.yRot - 0.2617994F;
        }

    }

    private void poseLeftArm(T pEntity) {
        switch(this.leftArmPose) {
            case EMPTY:
                this.leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
                this.leftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                this.leftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
                this.leftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
                this.leftArm.yRot = 0.1F + this.head.yRot;
                this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pEntity, false);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
                break;
            case SPYGLASS:
                this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (pEntity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.leftArm.yRot = this.head.yRot + 0.2617994F;
        }
    }
    
}
