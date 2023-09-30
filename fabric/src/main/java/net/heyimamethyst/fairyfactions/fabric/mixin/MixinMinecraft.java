package net.heyimamethyst.fairyfactions.fabric.mixin;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture.FairyBedTextureGenerator;
import net.heyimamethyst.fairyfactions.fabriclike.ReloadListenerWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{

    //@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;", shift = At.Shift.AFTER), method = "apply(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    @Inject(method = "<init>", at = @At( value = "INVOKE",
            target = "Lnet/minecraft/client/resources/PaintingTextureManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
    public void insertFairyBedTextureGenerator(GameConfig gameConfig, CallbackInfo callbackInfo)
    {
        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        resourceManagerHelper.registerReloadListener(new ReloadListenerWrapper(
                new ResourceLocation("fairyfactions", "fairy_bed_texture_generator"),
                FairyBedTextureGenerator.INSTANCE
        ));
    }

}
