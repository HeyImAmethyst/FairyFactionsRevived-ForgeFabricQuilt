package net.heyimamethyst.fairyfactions.forge.events.mod;


import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.client.model.ModModelLayers;
import net.heyimamethyst.fairyfactions.client.model.entity.*;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyBedRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyFishHookEntityRenderer;
import net.heyimamethyst.fairyfactions.client.render.entity.FairyRenderer;
import net.heyimamethyst.fairyfactions.items.ModSpawnEggItem;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import net.heyimamethyst.fairyfactions.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = FairyFactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
public class ModClientEvents
{
//    @SubscribeEvent
//	public static void onRegisterEntities(final RegisterEvent event)
//	{
//		ModSpawnEggItem.InitSpawnEggs();
//	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void registerSpawnEggColors(RegisterColorHandlersEvent.Item event)
	{
		ModSpawnEggItem.MOD_EGGS.forEach(egg ->
				event.getItemColors().register((stack, layer) -> egg.getColor(layer), egg)
		);
	}

	@SubscribeEvent
	public static void addCreative(BuildCreativeModeTabContentsEvent event)
	{
		if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
		{
			event.accept(ModItems.FAIRY_SPAWN_EGG);
		}

		if(event.getTab() == CreativeModeTabs.COLORED_BLOCKS)
		{
			event.accept(ModItems.OAK_WHITE_FAIRY_BED);
			event.accept(ModItems.OAK_ORANGE_FAIRY_BED);
			event.accept(ModItems.OAK_MAGENTA_FAIRY_BED);
			event.accept(ModItems.OAK_LIGHT_BLUE_FAIRY_BED);
			event.accept(ModItems.OAK_YELLOW_FAIRY_BED);
			event.accept(ModItems.OAK_LIME_FAIRY_BED);
			event.accept(ModItems.OAK_PINK_FAIRY_BED);
			event.accept(ModItems.OAK_GRAY_FAIRY_BED);
			event.accept(ModItems.OAK_LIGHT_GRAY_FAIRY_BED);
			event.accept(ModItems.OAK_CYAN_FAIRY_BED);
			event.accept(ModItems.OAK_PURPLE_FAIRY_BED);
			event.accept(ModItems.OAK_BLUE_FAIRY_BED);
			event.accept(ModItems.OAK_BROWN_FAIRY_BED);
			event.accept(ModItems.OAK_GREEN_FAIRY_BED);
			event.accept(ModItems.OAK_RED_FAIRY_BED);
			event.accept(ModItems.OAK_BLACK_FAIRY_BED);
		}
	}

	@SubscribeEvent
	public static void doClientStuff(final FMLClientSetupEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		EntityRenderDispatcher manager = mc.getEntityRenderDispatcher();
		//ModKeyBinds.register(event);
	}
	
	@SubscribeEvent
	public static void RegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(ModModelLayers.FAIRY_LAYER_LOCATION, FairyModel::createBodyLayer);
		event.registerLayerDefinition(ModModelLayers.FAIRY_EYES_LAYER_LOCATION, FairyEyesModel::createBodyLayer);
		event.registerLayerDefinition(ModModelLayers.FAIRY_PROPS_LAYER_LOCATION, FairyPropsModel::createBodyLayer);
		event.registerLayerDefinition(ModModelLayers.FAIRY_PROPS2_LAYER_LOCATION, FairyProps2Model::createBodyLayer);
		event.registerLayerDefinition(ModModelLayers.FAIRY_WITHERED_LAYER_LOCATION, FairyModel::createBodyLayer);

		ModModelLayers.FAIRY_BED_LAYER_LOCATION.values().forEach(fairyBed ->
		{
			event.registerLayerDefinition(fairyBed, FairyBedModel::createBodyLayer);
		});
	}
	
	@SubscribeEvent
	public static void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(ModEntities.FAIRY_ENTITY.get(), FairyRenderer::new);
		event.registerEntityRenderer(ModEntities.FAIRY_FISHING_BOBBER_ENTITY.get(), FairyFishHookEntityRenderer::new);
		event.registerEntityRenderer(ModEntities.FAIRY_BED_ENTITY.get(), FairyBedRenderer::new);
	}

}
