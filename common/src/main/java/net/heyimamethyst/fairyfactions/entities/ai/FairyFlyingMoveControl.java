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
            BlockState blockState = this.mob.level.getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level, blockPos);

            if (o > (double)this.mob.maxUpStep && d * d + e * e < (double)Math.max(1.0f, this.mob.getBbWidth()) || !voxelShape.isEmpty() && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + (double)blockPos.getY() && !blockState.is(BlockTags.DOORS) && !blockState.is(BlockTags.FENCES))
            {
                this.mob.getJumpControl().jump();
                this.operation = Operation.JUMPING;
            }
        }
        else if (this.operation == Operation.JUMPING)
        {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

            if (this.mob.isOnGround())
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
