package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_CrossedSquares implements ICrossedSquaresRenderer {

    @Unique
    private boolean betterfoliage$isRenderingCrossedSquares;

    @Unique
    private float betterfoliage$crossedSquaresVerticalScale;

    @Unique
    private IIcon betterfoliage$crossedSquareSecondSprite = null;

    @Unique
    private ForgeDirection betterfoliage$rotationAxis = ForgeDirection.UNKNOWN;

    @Unique
    private final double[] betterfoliage$rotationPoint = new double[3];

    @Unique
    private double betterfoliage$centerX;

    @Unique
    private double betterfoliage$centerY;

    @Unique
    private double betterfoliage$centerZ;

    @Override
    public void betterfoliage$setIsRenderingCrossedSquares(boolean isCrossedSquares) {
        betterfoliage$isRenderingCrossedSquares = isCrossedSquares;
    }

    @Override
    public void betterfoliage$setVerticalScale(float verticalScale) {
        betterfoliage$crossedSquaresVerticalScale = verticalScale;
    }

    @Override
    public void betterfoliage$setSecondSprite(IIcon secondSprite) {
        betterfoliage$crossedSquareSecondSprite = secondSprite;
    }

    @Override
    public void betterfoliage$setRotation(double x, double y, double z, ForgeDirection axis) {
        betterfoliage$centerX = x;
        betterfoliage$centerY = y;
        betterfoliage$centerZ = z;
        betterfoliage$rotationAxis = axis;
    }

    @Override
    public void betterfoliage$resetRotation() {
        betterfoliage$rotationAxis = ForgeDirection.UNKNOWN;
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
    private float betterfoliage$overrideVerticalScale(float original) {
        return betterfoliage$isRenderingCrossedSquares ? betterfoliage$crossedSquaresVerticalScale : original;
    }

    /// Second Sprite
    @ModifyVariable(
        method = "drawCrossedSquares",
        ordinal = 3,
        at = @At(
            value = "INVOKE",
            ordinal = 7,
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private double betterfoliage$secondSpriteMinU(double minU) {
        return betterfoliage$crossedSquareSecondSprite != null
            ? betterfoliage$crossedSquareSecondSprite.getMinU()
            : minU;
    }

    @ModifyVariable(
        method = "drawCrossedSquares",
        ordinal = 4,
        at = @At(
            value = "INVOKE",
            ordinal = 7,
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private double betterfoliage$secondSpriteMinV(double minV) {
        return betterfoliage$crossedSquareSecondSprite != null
            ? betterfoliage$crossedSquareSecondSprite.getMinV()
            : minV;
    }

    @ModifyVariable(
        method = "drawCrossedSquares",
        ordinal = 5,
        at = @At(
            value = "INVOKE",
            ordinal = 7,
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private double betterfoliage$secondSpriteMaxU(double maxU) {
        return betterfoliage$crossedSquareSecondSprite != null
            ? betterfoliage$crossedSquareSecondSprite.getMaxU()
            : maxU;
    }

    @ModifyVariable(
        method = "drawCrossedSquares",
        ordinal = 6,
        at = @At(
            value = "INVOKE",
            ordinal = 7,
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private double betterfoliage$secondSpriteMaxV(double maxV) {
        return betterfoliage$crossedSquareSecondSprite != null
            ? betterfoliage$crossedSquareSecondSprite.getMaxV()
            : maxV;
    }

    /// Rotation
    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$rotateCrossedSquare(Tessellator instance, double x, double y, double z, double u,
        double v, Operation<Void> original) {
        if (betterfoliage$rotationAxis == ForgeDirection.UNKNOWN) {
            original.call(instance, x, y, z, u, v);
            return;
        }

        betterfoliage$rotationPoint[0] = x;
        betterfoliage$rotationPoint[1] = y;
        betterfoliage$rotationPoint[2] = z;

        MathUtils.rotateCounterclock(
            betterfoliage$rotationAxis,
            betterfoliage$centerX,
            betterfoliage$centerY,
            betterfoliage$centerZ,
            betterfoliage$rotationPoint);

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }
}
