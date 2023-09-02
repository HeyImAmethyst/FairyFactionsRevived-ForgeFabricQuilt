package net.heyimamethyst.fairyfactions.forge.events.forge;


import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.event.RenderNameEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{

	@SubscribeEvent
	public void onRenderName(RenderNameplateEvent event)
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
