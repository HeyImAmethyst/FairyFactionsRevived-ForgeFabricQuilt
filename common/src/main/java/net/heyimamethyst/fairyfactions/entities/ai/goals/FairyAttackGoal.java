package net.heyimamethyst.fairyfactions.entities.ai.goals;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class FairyAttackGoal extends MeleeAttackGoal
{
    FairyEntity theFairy;

    public FairyAttackGoal(FairyEntity fairy, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen)
    {
        super(fairy, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.theFairy = fairy;
    }

    public boolean canUse()
    {
        return super.canUse() && !theFairy.isSitting() && theFairy.angry() && !theFairy.crying();
    }

    public boolean canContinueToUse()
    {
        return super.canContinueToUse() && !theFairy.isSitting() && theFairy.angry() && !theFairy.crying();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget)
    {
        //return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
        return (double)(( theFairy.tamed() ? 4.5F : 4.0F ) + pAttackTarget.getBbWidth());
        //return (double)(( theFairy.tamed() ? 2.5F : 2.0F ) + pAttackTarget.getBbWidth());
        //return (theFairy.tamed() ? 2.5F : 2.0F);
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity entity, double pDistToEnemySqr)
    {
        double d0 = this.getAttackReachSqr(entity);

        if (theFairy.attackAnim <= 0 && pDistToEnemySqr < d0
                && ( ( entity.getBoundingBox().maxY > theFairy.getBoundingBox().minY
                && entity.getBoundingBox().minY < theFairy.getBoundingBox().maxY )
                || pDistToEnemySqr == 0F ))
        {
            theFairy.attackAnim = 20;

            if (theFairy.flymode() && theFairy.canFlap() && theFairy.scout()
                    && theFairy.getVehicle() == null
                    && theFairy.getPassengers().size() == 0 && entity.getVehicle() == null
                    && entity.getPassengers().size() == 0
                    && !( entity instanceof FairyEntity
                    || entity instanceof FlyingMob))
            {
                // Scout's Totally Leet Air Attack.

                CommonMethods.sendFairyMount(theFairy, entity);

                entity.setJumping(true);

                theFairy.setFlymode(true);
                theFairy.setFlyTime(200);
                theFairy.setCanFlap(true);
                theFairy.attackAnim = 35;
            }
            else
            {
                if (theFairy.scout() && theFairy.getVehicle() != null
                        && entity == theFairy.getVehicle())
                {
                    // The finish of its air attack.

                    CommonMethods.sendFairyMount(theFairy, entity);

                    theFairy.attackAnim = 35;
                }

                // normal boring strike.
                theFairy.doAttack(entity);
            }
        }
    }

//    protected void func_70785_a(Entity entity, float f)
//    {
//        if (this.field_70724_aR <= 0 && f < (tamed() ? 2.5F : 2.0F) && ((entity.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b && entity.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) || f == 0.0F))
//        {
//            this.field_70724_aR = 20;
//            if (flymode() && canFlap() && scout()
//                    && entity instanceof EntityLiving && this.field_70154_o == null
//                    && this.field_70153_n == null && entity.field_70154_o == null
//                    && entity.field_70153_n == null && !(entity instanceof EntityFairy) && !(entity instanceof net.minecraft.entity.EntityFlying))
//            {
//                if (!this.field_70170_p.field_72995_K)
//                    FairyMod.sendFairyMount((Entity)this, entity);
//
//                setFlymode(true);
//                this.flyTime = 200;
//                setCanFlap(true);
//                this.field_70724_aR = 35;
//            }
//            else
//            {
//                if (scout() && this.field_70154_o != null
//                        && entity != null && entity == this.field_70154_o)
//                {
//                    if (!this.field_70170_p.field_72995_K)
//                        FairyMod.sendFairyMount((Entity)this, entity);
//                    this.field_70724_aR = 35;
//                }
//
//                hitTheMob(entity);
//            }
//        }
//    }
}
