package net.heyimamethyst.fairyfactions.mixin;

import net.heyimamethyst.fairyfactions.FairyConfigValues;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingSignBlockEntity.class)
public class HangingSignBlockEntityMixin
{
    @Inject(method = "getMaxTextLineWidth()I", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Integer> cir)
    {
        if(FairyConfigValues.EXTEND_HANGING_SIGN_TEXT_LIMIT)
            cir.setReturnValue(90);
        else
            cir.setReturnValue(60);
    }
}
