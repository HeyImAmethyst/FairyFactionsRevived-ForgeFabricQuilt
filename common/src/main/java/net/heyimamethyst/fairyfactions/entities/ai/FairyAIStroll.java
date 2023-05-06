package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FairyAIStroll extends WaterAvoidingRandomStrollGoal
{

    public FairyAIStroll(PathfinderMob pMob, double pSpeedModifier)
    {
        this(pMob, pSpeedModifier, 0.001F);
    }

    public FairyAIStroll(PathfinderMob pMob, double pSpeedModifier, float pProbability)
    {
        super(pMob, pSpeedModifier, pProbability);
    }

    @Override
    public void start()
    {
        super.start();
    }

    @Override
    public boolean canUse()
    {
        if(((FairyEntity) mob).isSitting())
        {
            return false;
        }
        else
        {
            return super.canUse();
            //return mob.getNavigation().isDone() && mob.getRandom().nextInt(10) == 0;
        }
    }

    @Nullable
    protected Vec3 getPosition()
    {

        FairyEntity fairy = (FairyEntity) mob;

        if(fairy.flymode())
        {
//            Vec3 vec3 = fairy.getViewVector(0.0F);
//
//            int i = 8;
//            Vec3 vec32 = HoverRandomPos.getPos(fairy, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
//            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(fairy, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));

            Vec3 landRandomPos = LandRandomPos.getPos(this.mob, 10, 7);
            Vec3 defaultRandomPos = DefaultRandomPos.getPos(this.mob, 10, 7);

            Vec3 landRandomPosXZ = Vec3.ZERO;
            Vec3 defaultRandomPosXZ = Vec3.ZERO;

            if(landRandomPos != null)
            {
                landRandomPosXZ = new Vec3(landRandomPos.x, fairy.position().y, landRandomPos.z);
            }

            if(defaultRandomPos != null)
            {
                defaultRandomPosXZ = new Vec3(defaultRandomPos.x, fairy.position().y, defaultRandomPos.z);
            }

            return this.mob.getRandom().nextFloat() >= this.probability ? landRandomPosXZ : defaultRandomPosXZ;
        }
        else
        {
            if (fairy.isInWaterOrBubble())
            {
                Vec3 vec3 = LandRandomPos.getPos(fairy, 15, 7);
                return vec3 == null ? DefaultRandomPos.getPos(this.mob, 10, 7) : vec3;
            }
            else
            {

                return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : DefaultRandomPos.getPos(this.mob, 10, 7);
            }
        }

//        if (fairy.isInWaterOrBubble())
//        {
//            Vec3 vec3 = LandRandomPos.getPos(fairy, 15, 7);
//            return vec3 == null ? super.getPosition() : vec3;
//        }
//        else
//        {
//            return fairy.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(fairy, 10, 7) : super.getPosition();
//        }
    }
}
