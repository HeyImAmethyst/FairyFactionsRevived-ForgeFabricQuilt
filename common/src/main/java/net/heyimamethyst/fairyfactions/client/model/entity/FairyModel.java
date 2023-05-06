package net.heyimamethyst.fairyfactions.client.model.entity;

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

public class FairyModel <T extends LivingEntity> extends HumanoidModel<T>
{
    public ModelPart strand, strand2, strand3, strand4;
    public ModelPart crown;
    public ModelPart wingLeft;
    public ModelPart wingRight;

    public boolean flymode;
    public boolean showCrown;
    public boolean scoutWings;
    public boolean rogueParts;
    public boolean hairType;

    public float sinage;

    static float f;
    static float f1;

    public FairyModel(ModelPart root, final float f, final float f1)
    {

        super(root);

        this.hat.visible = false;
        flymode = showCrown = false;

        strand = root.getChild("strand");
        strand2 = this.strand.getChild("strand2");
        strand3 = this.strand.getChild("strand3");
        strand4 = this.strand.getChild("strand4");

        crown = root.getChild("crown");

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

        partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6, new CubeDeformation(f)), PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        PartDefinition strand = partdefinition.addOrReplaceChild(
                "strand",
                CubeListBuilder.create()
                        .texOffs(0, 20).addBox(-3F, -5F, 3F, 6, 3, 1, new CubeDeformation(f))
                        .texOffs(24, 0).addBox(-4F, -5F, -3F, 1, 3, 6, new CubeDeformation(f))
                        .texOffs(24, 0).addBox(3F, -5F, -3F, 1, 3, 6, new CubeDeformation(f)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        strand.addOrReplaceChild(
                "strand2",
                CubeListBuilder.create()
                        .texOffs(13, 23).addBox(-5F, -2.5F, 1.5F, 10, 3, 3, new CubeDeformation(f)),
                PartPose.offset(0F, 0F + f1, 0F));

        PartDefinition strand3 = strand.addOrReplaceChild(
                "strand3", CubeListBuilder.create()
                        .texOffs(13, 23).addBox(-3F, -1.5F, -1.5F, 3, 3, 3, new CubeDeformation(f - 0.5F))
                        .texOffs(13, 23).addBox(-5.25F, -1.5F, -1.5F, 3, 3, 3, new CubeDeformation(f - 0.25F)),
                PartPose.offsetAndRotation(-2F, -1.75F + f1, 3F, 0, 0.5F, -1.0F));

        PartDefinition strand4 = strand.addOrReplaceChild(
                "strand4", CubeListBuilder.create()
                        .texOffs(13, 23).mirror().addBox(-0F, -1.5F, -1.5F, 3, 3, 3, new CubeDeformation(f - 0.5F))
                        .texOffs(13, 23).addBox(2.25F, -1.5F, -1.5F, 3, 3, 3, new CubeDeformation(f - 0.25F)),
                PartPose.offsetAndRotation(2F, -1.75F + f1, 3F, 0, -0.5F, 1.0F));

        partdefinition.addOrReplaceChild(
                "crown",
                CubeListBuilder.create()
                        .texOffs(37, 14).addBox(-3F, -6.75F, -3F, 6, 3, 6, new CubeDeformation(f + 0.25F)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(8, 12).addBox(-2F, 0.0F, -1F, 4, 6, 2, new CubeDeformation(f))
                        .texOffs(15, 20).addBox(-2F, 1.0F, -2F, 4, 2, 1, new CubeDeformation(f)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        PartDefinition wingRight = partdefinition.addOrReplaceChild(
                "wingRight",
                CubeListBuilder.create().texOffs(27, 9).addBox(0F, -0.75F, -1.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(f + 0.25F)), PartPose.offset(0.5F, 0.0F + f1, 1.0F));

        PartDefinition wingLeft = partdefinition.addOrReplaceChild(
                "wingLeft",
                CubeListBuilder.create().texOffs(27, 9).mirror().addBox(-5F, -0.75F, -1.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(f + 0.25F)).mirror(false), PartPose.offset(-0.5F, 0.0F + f1, 1.0F));

        partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(f)), PartPose.offset(-5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(0, 12).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(f)), PartPose.offset(5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(20, 12).addBox(-1F, 0.0F, -1F, 2, 6, 2, new CubeDeformation(f)), PartPose.offset(-1F, 18F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(20, 12).mirror().addBox(-1F, 0.0F, -1F, 2, 6, 2, new CubeDeformation(f)), PartPose.offset(1.0F, 18F + f1, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {
        pPoseStack.pushPose();

        this.head.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.body.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.leftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.rightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        this.leftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        if(!rogueParts)
        {
            strand2.visible = !hairType;
            strand3.visible = hairType;
            strand4.visible = hairType;

            strand.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        if (showCrown && !rogueParts)
        {
            crown.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        if (!scoutWings && !rogueParts)
        {
            wingLeft.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            wingRight.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        pPoseStack.popPose();

    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {

        float attackTime = pEntity.attackAnim;
        sinage = ((FairyEntity)pEntity).sinage;

        head.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        head.xRot = pHeadPitch / (180F / (float)Math.PI);
        strand.yRot = head.yRot;
        strand.xRot = head.xRot;
        crown.yRot = head.yRot;
        crown.xRot = head.xRot;

        if (!flymode)
        {
            rightArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
            leftArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
            rightLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
            leftLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount;
        }
        else
        {
            rightArm.xRot = 0.0F;
            leftArm.xRot = 0.0F;
            rightLeg.xRot = 0.0F;
            leftLeg.xRot = 0.0F;
        }

        rightArm.zRot = 0.05F;
        leftArm.zRot = -0.05F;
        rightLeg.yRot = 0.0F;
        leftLeg.yRot = 0.0F;
        rightLeg.zRot = 0.0F;
        leftLeg.zRot = 0.0F;

        if ((this.riding || this.crouching) && !flymode)
        {
            rightArm.xRot += -((float)Math.PI / 5F);
            leftArm.xRot += -((float)Math.PI / 5F);
            rightLeg.xRot = -((float)Math.PI * 2F / 5F);
            leftLeg.xRot = -((float)Math.PI * 2F / 5F);
            rightLeg.yRot = ((float)Math.PI / 10F);
            leftLeg.yRot = -((float)Math.PI / 10F);

            if (this.crouching)
            {
                rightLeg.xRot = -((float)Math.PI / 2F);
                leftLeg.xRot = -((float)Math.PI / 2F);
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

        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;

        //Swing animation

        if(attackTime > -9990F)
        {
            float f6 = attackTime;

            body.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            wingLeft.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            rightArm.z = (float)Math.sin(body.yRot) * 5F;
            rightArm.x = (float)-Math.cos(body.yRot) * 5F + 2.0F;

            leftArm.z = (float)-Math.sin(body.yRot) * 5F;
            leftArm.x = (float)Math.cos(body.yRot) * 5F - 2.0F;

            rightArm.yRot += body.yRot;

            leftArm.yRot += body.yRot;
            leftArm.xRot += body.yRot;

            f6 = 1.0F - attackTime;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;

            final float f8 = (float)Math.sin(f6 * (float)Math.PI);
            final float f9 = (float)Math.sin(attackTime * (float)Math.PI) * -(head.xRot - 0.7F) * 0.75F;

            rightArm.xRot -= f8 * 1.2D + f9;
            rightArm.yRot += body.yRot * 2.0F;
            rightArm.zRot = (float)Math.sin(attackTime * (float)Math.PI) * -0.4F;
        }

        if (flymode)
        {
            final float f7 = (float)Math.PI;

            body.xRot = f7 / 2.0F;
            body.y = 19F;

            wingLeft.xRot = f7 / 2.0F;
            wingRight.xRot = f7 / 2.0F;

            wingLeft.y = 17.5F;
            wingRight.y = 17.5F;

            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            rightLeg.z = 0.0F;
            leftLeg.z = 0.0F;

            rightArm.y = 19F;
            leftArm.y = 19F;

            rightLeg.y = 18F;
            leftLeg.y = 18F;

            rightLeg.z = 6F;
            leftLeg.z = 6F;

            head.z = -3F;
            head.y = 19.75F;

            strand.z = -3F;
            strand.y = 19.75F;

            crown.z = -3F;
            crown.y = 19.75F;
        }
        else
        {
            body.xRot = 0.0F;
            body.y = 12F;

            wingLeft.xRot = 0.0F;
            wingRight.xRot = 0.0F;

            wingLeft.y = 12.5F;
            wingRight.y = 12.5F;
            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            rightLeg.z = 0.0F;
            leftLeg.z = 0.0F;

            if (this.riding)
            {
                rightArm.y = 13F;
                leftArm.y = 13F;
            }
            else
            {
                rightArm.y = 13F;
                leftArm.y = 13F;
            }

            rightLeg.y = 18F;
            leftLeg.y = 18F;
            rightLeg.z = 0.0F;
            leftLeg.z = 0.0F;
            head.z = 0.0F;
            head.y = 12F;
            strand.z = 0.0F;
            strand.y = 12F;

            crown.z = 0.0F;
            crown.y = 12F;
        }

        if (flymode)
        {
            //arms bob
            rightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            leftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            rightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.1F;
            leftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.1F;

            //legs bob
            rightLeg.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            leftLeg.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            rightLeg.xRot = 0.1F;
            leftLeg.xRot = 0.1F;
        }
        else
        {
            //arms bob
            rightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;
            leftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;

            rightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.05F;
            leftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.05F;
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
