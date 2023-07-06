package net.heyimamethyst.fairyfactions.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//Code from https://github.com/VazkiiMods/Neat/blob/master/Xplat/src/main/java/vazkii/neat/mixin/AccessorRenderType.java
@Mixin(RenderType.class)
public interface AccessorRenderType
{
    @Invoker("create")
    static RenderType.CompositeRenderType fairyFactionsCreate(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize, boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState glState)
    {
        throw new IllegalStateException("");
    }
}