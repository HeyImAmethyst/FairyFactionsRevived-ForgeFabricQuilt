package net.heyimamethyst.fairyfactions.forge.events.forge;


import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.util.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ForgeClientEvents
{
	private static final Logger LOGGER = LogManager.getLogger();
	public static double rand;
	public static Random random = new Random();

	@SubscribeEvent
	public static void onKeyPress(InputEvent.MouseButton event)
	{
		Minecraft mc = Minecraft.getInstance();

		if(mc.level == null)
			return;

		onInput(mc, event.getButton(), event.getAction());
	}

	private static void onInput(Minecraft mc, int key, int action)
	{
		Player player = mc.player;

		//Code from https://github.com/V0idWa1k3r/ExPetrum/blob/e6a31dff9bd1b55cf65119bba6537c74c5da55fd/src/main/java/v0id/exp/combat/impl/ShieldSlam.java#L45

		Vec3 look = player.getViewVector(0).scale(100);
		Vec3 pos = player.getPosition(0);

		List<LivingEntity> targets = EntityHelper.rayTraceEntities(player.level, pos.add(0, player.getEyeHeight(), 0), look, Optional.of(e -> e != player), LivingEntity.class);

		LivingEntity assumedToBeLookedAt = EntityHelper.getClosest(targets, player);

		if (assumedToBeLookedAt != null)
		{
			//FairyFactions.LOGGER.debug("found closet entity " + assumedToBeLookedAt);

			if(assumedToBeLookedAt instanceof FairyEntity)
			{
				FairyEntity fairy = (FairyEntity) assumedToBeLookedAt;

				if(fairy.isPassenger() && player.getItemInHand(InteractionHand.MAIN_HAND) == ItemStack.EMPTY)
				{
					if(key == 1 && action == 1 && !player.isShiftKeyDown())
					{
						FairyFactions.LOGGER.warn("Unmounting fairy "+fairy);
						CommonMethods.sendFairyMount(fairy, player);
					}
				}
			}
		}
	}
}
