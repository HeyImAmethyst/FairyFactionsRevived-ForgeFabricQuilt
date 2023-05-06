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

public class FairyPropsModel<T extends LivingEntity> extends HumanoidModel<T>
{
    public ModelPart scoutHead;
    public ModelPart scoutBody;
    public ModelPart scoutLeftArm;
    public ModelPart scoutRightArm;
    public ModelPart scoutLeftLeg;
    public ModelPart scoutRightLeg;
    public ModelPart wingLeft;
    public ModelPart wingRight;

    public ModelPart medicHead;
    public ModelPart medicBody;

    public boolean flymode = true;
    public int jobType;
    public float sinage;

    static float f;
    static float f1;

    public FairyPropsModel(ModelPart root, final float f, final float f1)
    {
        super(root);

        this.hat.visible = false;
        jobType = 0;

        scoutHead = root.getChild("scoutHead");
        scoutBody = root.getChild("scoutBody");
        scoutLeftArm = root.getChild("scoutLeftArm");
        scoutRightArm = root.getChild("scoutRightArm");
        scoutLeftLeg = root.getChild("scoutLeftLeg");
        scoutRightLeg = root.getChild("scoutRightLeg");

        wingLeft = root.getChild("wingLeft");
        wingRight = root.getChild("wingRight");

        medicHead = root.getChild("medicHead");
        medicBody = root.getChild("medicBody");

        this.f = f;
        this.f1 = f1;
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        //MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition partdefinition = meshdefinition.getRoot();

        //Guard Type

        partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3F, -6.25F, -3F, 6, 2, 6, new CubeDeformation(f + 0.2F)), PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(8, 8).addBox(-2F, 0.0F, -1F, 4, 5, 2, new CubeDeformation(f + 0.25F))
                        .texOffs(0, 16).addBox(-2F, 1.0F, -2F, 4, 2, 1, new CubeDeformation(f + 0.25F)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1F, -1F, -1F, 2, 2, 2, new CubeDeformation(f + 0.375F)), PartPose.offset(-5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(0, 8).mirror().addBox(-1F, -1F, -1F, 2, 2, 2, new CubeDeformation(f + 0.375F)), PartPose.offset(5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, 4.0F, -1F, 2, 2, 2, new CubeDeformation(f + 0.25F)), PartPose.offset(-1F, 18F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 12).mirror().addBox(-1F, 4.0F, -1F, 2, 2, 2, new CubeDeformation(f + 0.25F)), PartPose.offset(1.0F, 18F + f1, 0.0F));

        //Scout Type

        partdefinition.addOrReplaceChild(
                "scoutHead",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0, 0, 0, 1, 1, 1, new CubeDeformation(f + 0.2F)), PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "scoutBody",
                CubeListBuilder.create()
                        .texOffs(28, 7).addBox(-2F, 0.0F, -1F, 4, 6, 2, new CubeDeformation(f + 0.125F))
                        .texOffs(28, 15).addBox(-2F, 1.0F, -2F, 4, 2, 1, new CubeDeformation(f + 0.125F)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "scoutRightArm",
                CubeListBuilder.create().texOffs(20, 8).addBox(-1F, -1F, -1F, 2, 2, 2, new CubeDeformation(f + 0.125F)), PartPose.offset(-5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "scoutLeftArm",
                CubeListBuilder.create().texOffs(20, 8).mirror().addBox(-1F, -1F, -1F, 2, 2, 2, new CubeDeformation(f + 0.125F)), PartPose.offset(5F, 1.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "scoutRightLeg",
                CubeListBuilder.create().texOffs(20, 13).addBox(-1F, 0.0F, -1F, 2, 3, 2, new CubeDeformation(f + 0.125F)), PartPose.offset(-1F, 18F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "scoutLeftLeg",
                CubeListBuilder.create().texOffs(20, 13).mirror().addBox(-1F, 0.0F, -1F, 2, 3, 2, new CubeDeformation(f + 0.125F)), PartPose.offset(1.0F, 18F + f1, 0.0F));

        PartDefinition wingRight = partdefinition.addOrReplaceChild(
                "wingRight",
                CubeListBuilder.create().texOffs(43, 15).addBox(0F, -1.75F, -1.0F, 6, 6, 1, new CubeDeformation(f + 0.25F)), PartPose.offset(0.5F, 0.0F + f1, 1.0F));

        PartDefinition wingLeft = partdefinition.addOrReplaceChild(
                "wingLeft",
                CubeListBuilder.create().texOffs(43, 15).mirror().addBox(-6F, -1.75F, -1.0F, 6, 6, 1, new CubeDeformation(f + 0.25F)).mirror(false), PartPose.offset(-0.5F, 0.0F + f1, 1.0F));

        //Medic Type

        partdefinition.addOrReplaceChild(
                "medicHead",
                CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-3F, -5.5F, -3F, 6, 1, 6, new CubeDeformation(f + 0.2F))
                        .texOffs(10, 15).addBox(-1.5F, -7.0F, -3.125F, 3, 3, 1, new CubeDeformation(f + 0.2F))
                        .texOffs(24, 0).addBox(-3F, -3.5F, -3F, 6, 1, 6, new CubeDeformation(f + 0.2F))
                        .texOffs(42, 3).addBox(-2.5F, -4F, -3.5F, 2, 2, 1, new CubeDeformation(f + 0.2F))
                        .texOffs(42, 3).addBox(0.5F, -4F, -3.5F, 2, 2, 1, new CubeDeformation(f + 0.2F)),
                PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        partdefinition.addOrReplaceChild(
                "medicBody",
                CubeListBuilder.create()
                        .texOffs(28, 18).addBox(-2F, 0.0F, -1F, 4, 6, 2, new CubeDeformation(f + 0.125F))
                        .texOffs(18, 18).addBox(-2F, 1.0F, -2F, 4, 2, 1, new CubeDeformation(f + 0.125F)), PartPose.offset(0.0F, 0.0F + f1, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha)
    {

        pPoseStack.pushPose();

        if(jobType == 0)
        {
            this.head.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.body.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.rightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.leftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.rightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.leftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
        else if(jobType == 1)
        {
            this.scoutHead.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.scoutBody.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.scoutRightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.scoutLeftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.scoutRightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.scoutLeftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            wingLeft.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            wingRight.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
        else
        {
            this.medicHead.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

            this.medicBody.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        pPoseStack.popPose();
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        if (jobType == 0)
        {
            setupGuardAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }
        else if (jobType == 1)
        {
            setupScoutAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }
        else if (jobType == 2)
        {
            setupMedicAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }
    }

    public void setupGuardAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {

        float attackTime = pEntity.attackAnim;

        head.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        head.xRot = pHeadPitch / (180F / (float)Math.PI);

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
        }
        else
        {
            body.xRot = 0.0F;
            body.y = 12F;

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
    }

    public void setupScoutAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        float attackTime = pEntity.attackAnim;
        sinage = ((FairyEntity)pEntity).sinage;

        scoutHead.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        scoutHead.xRot = pHeadPitch / (180F / (float)Math.PI);

        if (!flymode)
        {
            scoutRightArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
            scoutLeftArm.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
            scoutRightLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
            scoutLeftLeg.xRot = (float)Math.cos(pLimbSwing * 0.6662F + (float)Math.PI) * 1.4F * pLimbSwingAmount;
        }
        else
        {
            scoutRightArm.xRot = 0.0F;
            scoutLeftArm.xRot = 0.0F;
            scoutRightLeg.xRot = 0.0F;
            scoutLeftLeg.xRot = 0.0F;
        }

        scoutRightArm.zRot = 0.05F;
        scoutLeftArm.zRot = -0.05F;
        scoutRightLeg.yRot = 0.0F;
        scoutLeftLeg.yRot = 0.0F;
        scoutRightLeg.zRot = 0.0F;
        scoutLeftLeg.zRot = 0.0F;

        if ((this.riding || this.crouching) && !flymode)
        {
            scoutRightArm.xRot += -((float)Math.PI / 5F);
            scoutLeftArm.xRot += -((float)Math.PI / 5F);
            scoutRightLeg.xRot = -((float)Math.PI * 2F / 5F);
            scoutLeftLeg.xRot = -((float)Math.PI * 2F / 5F);
            scoutRightLeg.yRot = ((float)Math.PI / 10F);
            scoutLeftLeg.yRot = -((float)Math.PI / 10F);

            if (this.crouching)
            {
                scoutRightLeg.xRot = -((float)Math.PI / 2F);
                scoutLeftLeg.xRot = -((float)Math.PI / 2F);
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

        scoutRightArm.yRot = 0.0F;
        scoutLeftArm.yRot = 0.0F;

        if(attackTime > -9990F)
        {
            float f6 = attackTime;

            scoutBody.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            wingLeft.yRot = (float)Math.sin(Math.sqrt(f6) * (float)Math.PI * 2.0F) * 0.2F;

            scoutRightArm.z = (float)Math.sin(scoutBody.yRot) * 5F;
            scoutRightArm.x = (float)-Math.cos(scoutBody.yRot) * 5F + 2.0F;

            scoutLeftArm.z = (float)-Math.sin(scoutBody.yRot) * 5F;
            scoutLeftArm.x = (float)Math.cos(scoutBody.yRot) * 5F - 2.0F;

            scoutRightArm.yRot += scoutBody.yRot;

            scoutLeftArm.yRot += scoutBody.yRot;
            scoutLeftArm.xRot += scoutBody.yRot;

            f6 = 1.0F - attackTime;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;

            final float f8 = (float)Math.sin(f6 * (float)Math.PI);
            final float f9 = (float)Math.sin(attackTime * (float)Math.PI) * -(scoutHead.xRot - 0.7F) * 0.75F;

            scoutRightArm.xRot -= f8 * 1.2D + f9;
            scoutRightArm.yRot += scoutBody.yRot * 2.0F;
            scoutRightArm.zRot = (float)Math.sin(attackTime * (float)Math.PI) * -0.4F;
        }

        if (flymode)
        {
            final float f7 = (float)Math.PI;

            scoutBody.xRot = f7 / 2.0F;
            scoutBody.y = 19F;

            wingLeft.xRot = f7 / 2.0F;
            wingRight.xRot = f7 / 2.0F;

            wingLeft.y = 17.5F;
            wingRight.y = 17.5F;

            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            scoutRightLeg.z = 0.0F;
            scoutLeftLeg.z = 0.0F;

            scoutRightArm.y = 19F;
            scoutLeftArm.y = 19F;

            scoutRightLeg.y = 18F;
            scoutLeftLeg.y = 18F;

            scoutRightLeg.z = 6F;
            scoutLeftLeg.z = 6F;

            scoutHead.z = -3F;
            scoutHead.y = 19.75F;
        }
        else
        {
            scoutBody.xRot = 0.0F;
            scoutBody.y = 12F;

            wingLeft.xRot = 0.0F;
            wingRight.xRot = 0.0F;

            wingLeft.y = 12.5F;
            wingRight.y = 12.5F;
            wingLeft.z = 1.0F;
            wingRight.z = 1.0F;

            scoutRightLeg.z = 0.0F;
            scoutRightLeg.z = 0.0F;

            if (this.riding)
            {
                scoutRightArm.y = 13F;
                scoutLeftArm.y = 13F;
            }
            else
            {
                scoutRightArm.y = 13F;
                scoutLeftArm.y = 13F;
            }

            scoutRightLeg.y = 18F;
            scoutLeftLeg.y = 18F;
            scoutRightLeg.z = 0.0F;
            scoutLeftLeg.z = 0.0F;
            scoutHead.z = 0.0F;
            scoutHead.y = 12F;
        }

        if (flymode)
        {
            //arms bob
            scoutRightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            scoutLeftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            scoutRightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.1F;
            scoutLeftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.1F;

            //legs bob
            scoutRightLeg.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;
            scoutLeftLeg.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.1F + 0.05F;

            scoutRightLeg.xRot = 0.1F;
            scoutLeftLeg.xRot = 0.1F;
        }
        else
        {
            //arms bob
            scoutRightArm.zRot += Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;
            scoutLeftArm.zRot -= Math.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F;

            scoutRightArm.xRot += Math.sin(pAgeInTicks * 0.067F) * 0.05F;
            scoutLeftArm.xRot -= Math.sin(pAgeInTicks * 0.067F) * 0.05F;
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

    public void setupMedicAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        float attackTime = pEntity.attackAnim;

        medicHead.yRot = pNetHeadYaw / (180F / (float)Math.PI);
        medicHead.xRot = pHeadPitch / (180F / (float)Math.PI);

        if (flymode)
        {
            final float f7 = (float)Math.PI;

            medicBody.xRot = f7 / 2.0F;
            medicBody.y = 19F;

            medicHead.z = -3F;
            medicHead.y = 19.75F;
        }
        else
        {
            medicBody.xRot = 0.0F;
            medicBody.y = 12F;

            medicHead.z = 0.0F;
            medicHead.y = 12F;
        }
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
