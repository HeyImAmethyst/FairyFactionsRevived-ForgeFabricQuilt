package net.heyimamethyst.fairyfactions.items;

import com.google.common.collect.Maps;
import dev.architectury.registry.registries.RegistrySupplier;
import net.heyimamethyst.fairyfactions.ModExpectPlatform;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModSpawnEggItem extends SpawnEggItem
{

	protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
	private final RegistrySupplier<? extends EntityType<?>> entityTypeSupplier;

	public ModSpawnEggItem(final RegistrySupplier<? extends EntityType<?>> entityTypeSupplier, int p_i48465_2_, int p_i48465_3_, Properties p_i48465_4_)
	{
		super(null, p_i48465_2_, p_i48465_3_, p_i48465_4_);
		this.entityTypeSupplier = entityTypeSupplier;
		UNADDED_EGGS.add(this);
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundTag pNbt)
	{
		return this.entityTypeSupplier.get();
	}
	
	public static void InitSpawnEggs() 
	{
		final Map<EntityType<?>, SpawnEggItem> EGGS = ModExpectPlatform.getSpawnEggMap();

		DefaultDispenseItemBehavior dispenseBehaviour = new DefaultDispenseItemBehavior()
		{
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack)
			{
				Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
				EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
				type.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
				stack.shrink(1);
				return stack;
			}
		};

		for (final SpawnEggItem spawnEgg : UNADDED_EGGS)
		{
			if(EGGS != null)
				EGGS.put(spawnEgg.getType(null), spawnEgg);
			DispenserBlock.registerBehavior(spawnEgg, dispenseBehaviour);
		}

		UNADDED_EGGS.clear();
	}
}
