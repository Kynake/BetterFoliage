package mods.betterfoliage.mixins.late.angelica;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizons.angelica.rendering.celeritas.light.LightDataCache;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(LightDataCache.class)
public abstract class MixinLightDataCache {

    @Definition(id = "block", local = @Local(type = Block.class))
    @Definition(id = "getLightOpacity", method = "Lnet/minecraft/block/Block;getLightOpacity()I")
    @Expression("block.getLightOpacity() > 0")
    @ModifyExpressionValue(method = "compute", remap = false, at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean betterfoliage$overrideIsOpaqueCube(boolean isOpaque, @Local(name = "block") Block block) {
        return Hooks.overrideIsOpaqueCube(block, isOpaque);
    }
}
