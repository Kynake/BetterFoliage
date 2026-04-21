package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_CrossedSquares implements ICrossedSquaresRenderer {

    @Unique
    private boolean betterfoliage$isRenderingCrossedSquares;

    @Unique
    private float betterfoliage$crossedSquaresVerticalScale;

    @Override
    public void betterfoliage$setIsRenderingCrossedSquares(boolean isCrossedSquares) {
        betterfoliage$isRenderingCrossedSquares = isCrossedSquares;
    }

    @Override
    public void betterfoliage$setVerticalScale(float verticalScale) {
        betterfoliage$crossedSquaresVerticalScale = verticalScale;
    }

    /// ================================
    /// Crossed Squares render mixins
    /// ================================
    @Definition(id = "scale", local = @Local(argsOnly = true, type = float.class, ordinal = 0))
    @Definition(id = "d7", local = @Local(type = double.class, ordinal = 7))
    @Expression("d7 = 0.45 * (double) scale")
    @ModifyVariable(
        method = "drawCrossedSquares",
        ordinal = 0,
        argsOnly = true,
        at = @At(shift = At.Shift.AFTER, value = "MIXINEXTRAS:EXPRESSION"))
    private float betterfoliage$overrideShortGrassVerticalScale(float original) {
        return betterfoliage$isRenderingCrossedSquares ? betterfoliage$crossedSquaresVerticalScale : original;
    }
}
