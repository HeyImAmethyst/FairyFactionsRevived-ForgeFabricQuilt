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

    @Override
    public void tick()
    {
        ++this.tick;
    }
}
