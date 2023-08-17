package net.heyimamethyst.fairyfactions.client.model.entity.fairy_bed;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class FairyBedModel extends EntityModel<FairyBedEntity>
{
    private final ModelPart fairyBed;

    public FairyBedModel(ModelPart modelPart)
    {
        this.fairyBed = modelPart.getChild("fairyBed");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

//        PartDefinition group = partdefinition.addOrReplaceChild("fairyBed", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
//
//        PartDefinition stump = group.addOrReplaceChild("stump", CubeListBuilder.create().texOffs(23, 10).addBox(-4.0F, -1.0F, -7.0F, 9.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 3).addBox(-6.0F, -16.0F, -7.0F, 2.0F, 16.0F, 13.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 3).addBox(5.0F, -16.0F, -7.0F, 2.0F, 16.0F, 13.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 14).addBox(-4.0F, -16.0F, 4.0F, 9.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 23).addBox(-4.0F, -16.0F, -7.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(34, 11).addBox(-4.0F, -16.0F, -5.0F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//        PartDefinition cube_r1 = stump.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.1F, -2.0F, -1.5F, 4.2F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8696F, -8.1531F, -1.5F, 0.0F, 0.0F, 0.3927F));
//
//        PartDefinition bed = group.addOrReplaceChild("bed", CubeListBuilder.create().texOffs(86, 0).addBox(-4.0F, -3.0F, -6.0F, 9.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//        PartDefinition pillows = bed.addOrReplaceChild("pillows", CubeListBuilder.create().texOffs(73, 16).addBox(-0.9F, -8.0F, 3.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
//
//        PartDefinition cube_r2 = pillows.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(73, 16).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.6452F, 2.0903F, -0.48F, 0.0F, 0.0F));
//
//        PartDefinition cube_r3 = pillows.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(73, 16).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7437F, -4.3722F, 1.7881F, -0.3491F, 0.5236F, 0.0F));
//
//        PartDefinition cube_r4 = pillows.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(73, 16).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.944F, -4.5381F, 3.0511F, -0.3927F, -0.1745F, 0.0F));
//
//        PartDefinition light = group.addOrReplaceChild("light", CubeListBuilder.create().texOffs(2, 38).addBox(0.0F, -14.0F, -1.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
//                .texOffs(41, 51).addBox(0.0F, -11.3F, -1.5F, 1.0F, 1.3F, 1.0F, new CubeDeformation(0.0F))
//                .texOffs(79, 32).addBox(-0.4F, -12.0F, -1.9F, 1.7F, 0.7F, 1.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition fairyBed = partdefinition.addOrReplaceChild("fairyBed", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition stump = fairyBed.addOrReplaceChild("stump", CubeListBuilder.create().texOffs(23, 10).addBox(-4.0F, -1.0F, -7.0F, 9.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-6.0F, -16.0F, -7.0F, 2.0F, 16.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(5.0F, -16.0F, -7.0F, 2.0F, 16.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-4.0F, -16.0F, 4.0F, 9.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-4.0F, -16.0F, -7.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 11).addBox(-4.0F, -16.0F, -5.0F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition cube_r1 = stump.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.1F, -2.0F, -1.5F, 4.2F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8696F, -8.1531F, -1.5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition bed = fairyBed.addOrReplaceChild("bed", CubeListBuilder.create().texOffs(86, 0).addBox(-4.0F, -3.0F, -6.0F, 9.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition pillows = bed.addOrReplaceChild("pillows", CubeListBuilder.create().texOffs(73, 16).addBox(-0.9F, -8.0F, 3.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = pillows.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(73, 16).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.6452F, 2.0903F, -0.48F, 0.0F, 0.0F));

        PartDefinition cube_r3 = pillows.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(73, 16).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7437F, -4.3722F, 1.7881F, -0.3491F, 0.5236F, 0.0F));

        PartDefinition cube_r4 = pillows.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(73, 16).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.944F, -4.5381F, 3.0511F, -0.3927F, -0.1745F, 0.0F));

        PartDefinition light = fairyBed.addOrReplaceChild("light", CubeListBuilder.create().texOffs(2, 38).addBox(0.0F, -14.0F, -1.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(41, 51).addBox(0.0F, -11.3F, -1.5F, 1.0F, 1.3F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(79, 32).addBox(-0.4F, -12.0F, -1.9F, 1.7F, 0.7F, 1.7F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));


        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(FairyBedEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        //poseStack.pushPose();

        fairyBed.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        //poseStack.popPose();
    }
}