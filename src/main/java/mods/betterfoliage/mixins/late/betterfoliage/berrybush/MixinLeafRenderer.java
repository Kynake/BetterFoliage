package mods.betterfoliage.mixins.late.betterfoliage.berrybush;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.integration.BerryBushIntegration;
import mods.betterfoliage.client.render.features.LeafRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LeafRenderer.class)
public abstract class MixinLeafRenderer {

    @ModifyVariable(
        method = "renderWorldBlock",
        name = "hOffset",
        at = @At(value = "STORE"),
        remap = false,
        require = 1)
    private static float betterfoliage$berryBushOverrideHOffset(float hOffset,
        @Local(argsOnly = true, name = "block") final Block block) {
        return BerryBushIntegration.applyScaleMultiplier(hOffset, block);
    }

    @ModifyVariable(
        method = "renderWorldBlock",
        name = "horizontalScale",
        at = @At(value = "STORE"),
        remap = false,
        require = 1)
    private static float betterfoliage$berryBushOverrideHorizontalScale(float horizontalScale,
        @Local(argsOnly = true, name = "block") final Block block) {
        return BerryBushIntegration.applyScaleMultiplier(horizontalScale, block);
    }

    @ModifyVariable(method = "renderWorldBlock", name = "vDelta", at = @At(value = "STORE"), remap = false, require = 1)
    private static float betterfoliage$berryBushOverrideVDelta(float vDelta,
        @Local(argsOnly = true, name = "block") final Block block) {
        return BerryBushIntegration.applyScaleMultiplier(vDelta, block);
    }

    @ModifyVariable(
        method = "renderWorldBlock",
        name = "verticalScale",
        at = @At(value = "LOAD", ordinal = 1), // Only override after it's use in vDelta
        remap = false,
        require = 1)
    private static float betterfoliage$berryBushOverrideVerticalScale(float verticalScale,
        @Local(argsOnly = true, name = "block") final Block block) {
        return BerryBushIntegration.applyScaleMultiplier(verticalScale, block);
    }

    // Not using @Local because it generates a LocalRef variable, whereas using the method vars doesn't.
    @ModifyConstant(
        method = "renderWorldBlock",
        constant = @Constant(doubleValue = 0.5, ordinal = 1),
        remap = false,
        require = 1)
    private static double betterfoliage$berryBushOverrideVerticalCenter(double vertCenter, IBlockAccess world, int x,
        int y, int z, Block block) {
        return BerryBushIntegration.getVerticalCenter(vertCenter, block);
    }
}
