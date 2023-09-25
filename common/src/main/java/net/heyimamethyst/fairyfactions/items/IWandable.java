package net.heyimamethyst.fairyfactions.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

//Class from : https://github.com/baileyholl/Ars-Nouveau/blob/1.18.x/src/main/java/com/hollingsworth/arsnouveau/api/item/IWandable.java
public interface IWandable
{
    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
    }

    default void onFinishedConnectionFirst(@Nullable Entity storedEntityPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity)
    {
    }

    /**
     * Called on the time of wanding.
     */
    default void onWanded(Player playerEntity)
    {
    }
}
