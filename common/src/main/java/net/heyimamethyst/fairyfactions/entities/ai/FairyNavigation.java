package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

public class FairyNavigation extends GroundPathNavigation
{
    FairyEntity fairy;

    public FairyNavigation(FairyEntity fairy, Level level)
    {
        super(fairy, level);
        this.fairy = fairy;
    }

//    public void tick()
//    {
//        ++this.tick;
//    }

//    protected PathFinder createPathFinder(int pMaxVisitedNodes)
//    {
//        this.nodeEvaluator = new WalkNodeEvaluator();
//        this.nodeEvaluator.setCanPassDoors(true);
//        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
//    }
//
//    private int getSurfaceY()
//    {
//        if ((fairy.isInWater() && this.canFloat()) || fairy.flymode())
//        {
//            int i = fairy.getBlockY();
//            BlockState blockstate = this.level.getBlockState(new BlockPos(fairy.getX(), (double)i, fairy.getZ()));
//            int j = 0;
//
//            while(blockstate.is(Blocks.WATER) || blockstate.is(Blocks.AIR))
//            {
//                ++i;
//                blockstate = this.level.getBlockState(new BlockPos(fairy.getX(), (double)i, fairy.getZ()));
//                ++j;
//                if (j > 16)
//                {
//                    return fairy.getBlockY();
//                }
//            }
//
//            return i;
//        }
//        else
//        {
//            return Mth.floor(fairy.getY() + 0.5D);
//        }
//    }
//
//    @Override
//    protected Vec3 getTempMobPos()
//    {
//        return fairy.position();
//        //return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
//    }
//
//    @Override
//    protected boolean canUpdatePath()
//    {
//        //return fairy.isOnGround() || fairy.flymode() || this.canFloat() || this.isInLiquid() || fairy.isPassenger();
//        return fairy.isOnGround()|| fairy.flymode() || this.isInLiquid() || fairy.isPassenger();
//    }

    @Override
    public boolean moveTo(double x, double y, double z, double speedIn)
    {
        mob.getMoveControl().setWantedPosition(x, y, z, speedIn);
        return true;
    }

    @Override
    public boolean moveTo(Entity entityIn, double speedIn)
    {
        mob.getMoveControl().setWantedPosition(entityIn.getX(), entityIn.getY(), entityIn.getZ(), speedIn);
        return true;
    }
}
