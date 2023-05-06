package net.heyimamethyst.fairyfactions.registry;


import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.entities.FairyFishHookEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(FairyFactions.MOD_ID, Registry.ENTITY_TYPE_REGISTRY);

	public static void Init()
	{
		ENTITY_TYPES.register();
	}

	public static final RegistrySupplier<EntityType<FairyEntity>> FAIRY_ENTITY = ENTITY_TYPES.register("fairy_entity",
            () -> EntityType.Builder.<FairyEntity>of(FairyEntity::new, MobCategory.AMBIENT)
            .sized(0.4F, 1.0F) // Hitbox Size //sized(0.4F, 0.8F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(FairyFactions.MOD_ID, "fairy_entity").toString()));

	public static final RegistrySupplier<EntityType<FairyFishHookEntity>> FAIRY_FISHING_BOBBER_ENTITY = ENTITY_TYPES.register("fairy_fishing_bobber_entity",
			() -> EntityType.Builder.<FairyFishHookEntity>of(FairyFishHookEntity::new, MobCategory.MISC)
					.noSave()
					.noSummon()
					.sized(0.25F, 0.25F) // Hitbox Size
					.clientTrackingRange(4)
					.updateInterval(5)
					.build(new ResourceLocation(FairyFactions.MOD_ID, "fairy_fishing_bobber_entity").toString()));

}
