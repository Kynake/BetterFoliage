package mods.betterfoliage.mixins.late.betterfoliage.gt5u;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.client.render.features.LilypadRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LilypadRenderer.class)
public abstract class MixinLilypadRenderer {

    @ModifyVariable(
        method = "renderWorldBlock",
        name = "flowerColor",
        at = @At(value = "STORE"),
        remap = false,
        require = 1)
    private int betterfoliage$applyGT5UPollutionTintFlower(int original, @Local(name = "x", argsOnly = true) int x,
        @Local(name = "z", argsOnly = true) int z) {
        return GT5UIntegration.applyPollutionTintFlower(original, x, z);
    }
}
