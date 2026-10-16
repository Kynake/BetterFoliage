package mods.betterfoliage.mixins.late.angelica;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizons.angelica.rendering.celeritas.light.LightDataCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(LightDataCache.class)
public abstract class MixinLightDataCache {

    @ModifyExpressionValue(
        method = "compute",
        at = { @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isOpaqueCube()Z"),
            @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isOpaque()Z") })
    private boolean betterfoliage$overrideIsOpaqueCube(boolean isOpaque, @Local(name = "block") Block block) {
        return Hooks.overrideIsOpaqueCube(block, isOpaque);
    }
}
