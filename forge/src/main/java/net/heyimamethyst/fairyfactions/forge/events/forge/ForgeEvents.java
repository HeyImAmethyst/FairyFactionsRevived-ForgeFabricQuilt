package net.heyimamethyst.fairyfactions.forge.events.forge;


import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.event.RenderNameEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{
	@SubscribeEvent
	public void onRenderName(RenderNameTagEvent event)
	{
		Entity entity = event.getEntity();

		if(RenderNameEvent.onRenderName(entity))
		{
			event.setResult(Event.Result.ALLOW);
		}
		else
		{
			event.setResult(Event.Result.DEFAULT);
		}
	}
}
