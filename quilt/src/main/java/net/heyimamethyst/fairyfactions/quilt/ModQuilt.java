package net.heyimamethyst.fairyfactions.quilt;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.fabriclike.ModFabricLike;
import net.heyimamethyst.fairyfactions.registry.ModEntities;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ModQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        ModFabricLike.init();
    }
}
