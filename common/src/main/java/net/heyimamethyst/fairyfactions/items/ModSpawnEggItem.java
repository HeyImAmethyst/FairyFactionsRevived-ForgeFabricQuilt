package net.heyimamethyst.fairyfactions.items;

import com.google.common.collect.Maps;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModSpawnEggItem extends SpawnEggItem
{

	public static final List<ModSpawnEggItem> MOD_EGGS = new ArrayList<>();
	public static final Map<EntityType<?>, ModSpawnEggItem> TYPE_MAP = new IdentityHashMap<>();

	private final RegistrySupplier<? extends EntityType<?>> entityTypeSupplier;

	public ModSpawnEggItem(final RegistrySupplier<? extends EntityType<?>> entityTypeSupplier, int p_i48465_2_, int p_i48465_3_, Properties p_i48465_4_)
	{
		super(null, p_i48465_2_, p_i48465_3_, p_i48465_4_);
		this.entityTypeSupplier = entityTypeSupplier;
		MOD_EGGS.add(this);
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundTag pNbt)
	{
		EntityType<?> type = super.getType(pNbt);
		return type != null ? type : entityTypeSupplier.get();
	}
	
	public static void InitSpawnEggs() 
	{
		MOD_EGGS.forEach(egg ->
		{
			DispenseItemBehavior dispenseBehavior = egg.createDispenseBehavior();
			if (dispenseBehavior != null)
			{
				DispenserBlock.registerBehavior(egg, dispenseBehavior);
			}

			TYPE_MAP.put(egg.entityTypeSupplier.get(), egg);
		});
	}

	@Nullable
	protected DispenseItemBehavior createDispenseBehavior()
	{
		return DEFAULT_DISPENSE_BEHAVIOR;
	}

	private static final DispenseItemBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) ->
	{
		Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
		EntityType<?> type = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());

		try
		{
			type.spawn(source.getLevel(), stack, null, source.getPos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
		}
		catch (Exception exception)
		{
			DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
			return ItemStack.EMPTY;
		}

		stack.shrink(1);
		source.getLevel().gameEvent(GameEvent.ENTITY_PLACE, source.getPos(), GameEvent.Context.of(source.getBlockState()));
		return stack;
	};
}
