package net.heyimamethyst.fairyfactions.event;

import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.ClientMethods;
import net.minecraft.world.entity.Entity;

public class RenderNameEvent
{
    public static boolean onRenderName(Entity entity)
    {

        if(entity instanceof FairyEntity)
        {
            FairyEntity fairy = (FairyEntity) entity;

            if (fairy.getFaction() != 0)
            {
                return true;
            }
            else if (fairy.tamed())
            {

                if (fairy.isRuler(ClientMethods.getCurrentPlayer()))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }

        return false;
    }
}
