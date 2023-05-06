package net.heyimamethyst.fairyfactions.entities.ai;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class FairyAI extends Goal
{
    private FairyEntity theFairy;

    public FairyAI(FairyEntity fairy)
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
                theFairy.fairyTasks.handleAnger();
            }
            else if (theFairy.crying())
            {
                theFairy.fairyTasks.handleFear();
            }
            else
            {
                theFairy.fairyTasks.handleRuler();

                if (theFairy.medic())
                {
                    theFairy.fairyTasks.handleHealing();
                }
                else if (theFairy.rogue())
                {
                    theFairy.fairyTasks.handleRogue();
                }
                else
                {
                    theFairy.fairyTasks.handleSocial();
                }

                theFairy.fairyTasks.handlePosted(theFairy.level,true);
            }

        }
    }
}
