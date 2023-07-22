package net.heyimamethyst.fairyfactions.client.model;

import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyBedEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.HashMap;
import java.util.Map;

public class ModModelLayers
{
	public static final ModelLayerLocation FAIRY_LAYER_LOCATION = createLocation("fairy");
	public static final ModelLayerLocation FAIRY_EYES_LAYER_LOCATION = createLocation("fairy_eyes");
	public static final ModelLayerLocation FAIRY_PROPS_LAYER_LOCATION = createLocation("fairy_props");
	public static final ModelLayerLocation FAIRY_PROPS2_LAYER_LOCATION = createLocation("fairy_props2");

	public static final ModelLayerLocation FAIRY_WITHERED_LAYER_LOCATION = createLocation("fairy_withered");

	public static final Map<FairyBedEntity.Type, ModelLayerLocation> FAIRY_BED_LAYER_LOCATION = creatFairyBedMap();

	private static Map<FairyBedEntity.Type, ModelLayerLocation> creatFairyBedMap()
	{
		Map<FairyBedEntity.Type, ModelLayerLocation> map = new HashMap<>();

		for (FairyBedEntity.Type type : FairyBedEntity.Type.values())
		{
			map.put(type, ModModelLayers.createFairyBedModelName(type));
		}

		return map;
	}

	private static ModelLayerLocation createLocation(String string)
	{
		return new ModelLayerLocation(new ResourceLocation(FairyFactions.MOD_ID, string), "main");
	}

	public static ModelLayerLocation createFairyBedModelName(FairyBedEntity.Type type)
	{
		return ModModelLayers.createLocation("fairy_bed/" + type.getName());
	}
}
