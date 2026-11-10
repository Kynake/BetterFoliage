package mods.betterfoliage.mixins.late.betterfoliage.tfc;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.integration.TFCIntegration;
import mods.betterfoliage.client.render.features.GrassRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(GrassRenderer.class)
public abstract class MixinGrassRenderer {

    @WrapMethod(method = "isEligibleAsGrassBase", remap = false)
    private static boolean betterfoliage$tfcOverrideGrassTest(IBlockAccess world, int x, int y, int z,
        Operation<Boolean> original) {
        return !TFCIntegration.grass.matches(world, x, y, z) && original.call(world, x, y, z);
    }

    @ModifyVariable(
        method = "renderBaseBlockWithOverrides",
        name = "isConnected",
        at = @At("HEAD"),
        argsOnly = true,
        remap = false)
    private static boolean betterfoliage$tfcOverrideBaseRenderer(boolean isConnected,
        @Local(argsOnly = true, name = "block") Block block) {
        return isConnected && !TFCIntegration.grass.matchesNoMeta(block);
    }
}
