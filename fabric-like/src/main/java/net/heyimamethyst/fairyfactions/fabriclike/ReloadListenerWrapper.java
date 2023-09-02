package net.heyimamethyst.fairyfactions.fabriclike;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListenerWrapper<T> implements IdentifiableResourceReloadListener
{

    private final ResourceLocation id;
    private final SimplePreparableReloadListener<T> listener;

    public ReloadListenerWrapper(ResourceLocation id, SimplePreparableReloadListener<T> listener)
    {
        this.id = id;
        this.listener = listener;
    }

    @Override
    public ResourceLocation getFabricId()
    {
        return this.id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return this.listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }
}
