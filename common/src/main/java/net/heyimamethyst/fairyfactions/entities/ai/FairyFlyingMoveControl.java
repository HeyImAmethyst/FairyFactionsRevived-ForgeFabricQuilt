package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FairyFlyingMoveControl extends MoveControl
{

    public FairyFlyingMoveControl(Mob pMob)
    {
        super(pMob);
    }

    @Override
    public void tick()
    {
        if (this.operation == Operation.MOVE_TO)
        {
//            this.operation = Operation.WAIT;
//
//            double d0 = this.wantedX - this.mob.getX();
//            double d1 = this.wantedZ - this.mob.getZ();
//            double d2 = this.wantedY - this.mob.getY();
//
//            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
//
//            if (d3 < (double)2.5000003E-7F)
//            {
//                this.mob.setZza(0.0F);
//                return;
//            }
//
//            FairyEntity fairy = (FairyEntity)mob;
//
////            Vec3 vec3 = new Vec3(d0, d2, d1);
////            vec3 = vec3.normalize();
////
////            if(vec3 != Vec3.ZERO)
////            {
////                float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
////                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
////            }
//
//            float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
//            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
//
//            float f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
//            this.mob.setSpeed(f1);
//
//            BlockPos blockpos = this.mob.blockPosition();
//            BlockState blockstate = this.mob.level.getBlockState(blockpos);
//            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
//
//            if (d2 > (double)this.mob.maxUpStep && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES))
//            {
//                this.mob.getJumpControl().jump();
//                this.operation = Operation.JUMPING;
//            }
//
////            if(fairy.flymode())
////            {
////                double d4 = Math.sqrt(d0 * d0 + d1 * d1);
////
////                if (Math.abs(d2) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F)
////                {
////                    float f2 = (float)(-(Mth.atan2(d2, d4) * (double)(180F / (float)Math.PI)));
////                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, 10F));
////                    this.mob.setYya(d2 > 0.0D ? f1 : -f1);
////                }
////            }

            this.operation = Operation.WAIT;

            double d = this.wantedX - this.mob.getX();
            double e = this.wantedZ - this.mob.getZ();
            double o = this.wantedY - this.mob.getY();

            double p = d * d + o * o + e * e;

            if (p < 2.500000277905201E-7)
            {
                this.mob.setZza(0.0f);
                return;
            }

            float n = (float)(Mth.atan2(e, d) * 57.2957763671875) - 90.0f;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), n, 90.0f));
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

            BlockPos blockPos = this.mob.blockPosition();
            BlockState blockState = this.mob.level().getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level(), blockPos);

            if (o > (double)this.mob.maxUpStep() && d * d + e * e < (double)Math.max(1.0f, this.mob.getBbWidth()) || !voxelShape.isEmpty() && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + (double)blockPos.getY() && !blockState.is(BlockTags.DOORS) && !blockState.is(BlockTags.FENCES))
            {
                this.mob.getJumpControl().jump();
                this.operation = Operation.JUMPING;
            }
        }
        else if (this.operation == Operation.JUMPING)
        {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

            if (this.mob.onGround())
            {
                this.operation = Operation.WAIT;
            }
        }
        else if (this.operation == Operation.STRAFE)
        {
            this.operation = Operation.WAIT;
        }
    }
}
