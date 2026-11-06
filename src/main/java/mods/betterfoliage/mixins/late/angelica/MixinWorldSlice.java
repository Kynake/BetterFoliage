package mods.betterfoliage.mixins.late.angelica;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(WorldSlice.class)
public abstract class MixinWorldSlice {

    @ModifyExpressionValue(
        method = "getSkyBlockTypeBrightness",
        remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getUseNeighborBrightness()Z"))
    private boolean betterfoliage$overrideUseNeighborBrightness(boolean original,
        @Local(argsOnly = true, name = "x") int x, @Local(argsOnly = true, name = "y") int y,
        @Local(argsOnly = true, name = "z") int z) {
        return Hooks.overrideIsPartialBlock((WorldSlice) (Object) this, x, y, z, original);
    }
}
