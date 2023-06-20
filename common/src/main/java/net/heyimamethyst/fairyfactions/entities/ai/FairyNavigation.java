package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class FairyNavigation extends GroundPathNavigation
{
    FairyEntity fairy;

    public FairyNavigation(FairyEntity fairy, Level level)
    {
        super(fairy, level);
        this.fairy = fairy;
    }

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

    /*
        Not having the tick metheod and overriding the methods below it causes the fairy spinning bug.
        Having the tick method below and not having the overriding methods below it causes
        faires to not follow thier ruler when flying. How do I make it so that the fairies follow
        their ruler while flying without having the spinning bug?
    */

//    @Override
//    public void tick()
//    {
//        ++this.tick;
//    }

    @Override
    protected Vec3 getTempMobPos()
    {
        return this.mob.position();
    }

    @Override
    protected boolean canUpdatePath()
    {
        return fairy.flymode() || this.isInLiquid() || !this.mob.isPassenger();
    }

    @Override
    public boolean isStableDestination(BlockPos blockPos)
    {
        return !this.level.getBlockState(blockPos.below()).isAir() || this.level.getBlockState(blockPos.below()).isAir();
    }
}
