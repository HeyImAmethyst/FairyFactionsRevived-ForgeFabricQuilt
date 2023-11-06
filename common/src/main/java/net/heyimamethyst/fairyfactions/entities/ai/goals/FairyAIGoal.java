package net.heyimamethyst.fairyfactions.entities.ai.goals;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FairyAIGoal extends Goal
{
    private FairyEntity theFairy;

    public FairyAIGoal(FairyEntity fairy)
    {
        this.theFairy = fairy;
    }

    @Override
    public boolean canUse()
    {
        return theFairy.isAlive() ? true : false;
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.canUse();
    }

    @Override
    public void start()
    {
        super.start();
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    @Override
    public void tick()
    {

        ++theFairy.listActions;
        if (theFairy.listActions >= 8)
        {
            theFairy.listActions = theFairy.getRandom().nextInt(3);

            if(theFairy.isSitting())
            {
                return;
            }

            if (theFairy.angry())
            {
                theFairy.fairyBehavior.handleAnger();
            }
            else if (theFairy.crying())
            {
                theFairy.fairyBehavior.handleFear();
            }
            else
            {
                theFairy.fairyBehavior.handleRuler();

                if (theFairy.medic())
                {
                    theFairy.fairyBehavior.handleHealing();
                }
                else if (theFairy.rogue())
                {
                    theFairy.fairyBehavior.handleRogue();
                }
                else
                {
                    theFairy.fairyBehavior.handleSocial();
                }

                theFairy.fairyBehavior.handlePosted(theFairy.level,true);
                theFairy.fairyBehavior.handleBed(theFairy.level);
            }

        }
    }
}
