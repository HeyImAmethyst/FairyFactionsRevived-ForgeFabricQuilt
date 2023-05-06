package net.heyimamethyst.fairyfactions.proxy;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.gui.GuiName;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientMethods
{
    public static void openRenameGUI(FairyEntity fairy)
    {
        FairyFactions.LOGGER.info("ClientProxy.openRenameGUI");

        if( fairy.isRuler(getCurrentPlayer()) )
        {
            FairyFactions.LOGGER.info("ClientProxy.openRenameGUI: current player is ruler, displaying gui");
            //fairy.setNameEnabled(true);
            Minecraft.getInstance().setScreen(new GuiName(fairy));
        }
    }

    public static Player getCurrentPlayer()
    {
        return (Player)Minecraft.getInstance().player;
    }
}
