package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_CrossedSquares implements ICrossedSquaresRenderer {

    /// Scale
    @Unique
    private boolean betterfoliage$isUsingCustomVerticalScale;

    @Unique
    private float betterfoliage$crossedSquaresVerticalScale;

    /// Sprite
    @Unique
    private IIcon betterfoliage$crossedSquareSecondSprite = null;

    /// Rotation
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

    /// AO
    @Unique
    private boolean betterfoliage$useAO;

    @Unique
    private int betterfoliage$AOx;

    @Unique
    private int betterfoliage$AOy;

    @Unique
    private int betterfoliage$AOz;

    @Shadow
    public abstract int getAoBrightness(int a, int b, int c, int d);

    @Override
    public void betterfoliage$setVerticalScale(float verticalScale) {
        betterfoliage$isUsingCustomVerticalScale = true;
        betterfoliage$crossedSquaresVerticalScale = verticalScale;
    }

    @Override
    public void betterfoliage$resetVerticalScale() {
        betterfoliage$isUsingCustomVerticalScale = false;
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

    @Override
    public void betterfoliage$setAORender(Block block, int x, int y, int z) {
        betterfoliage$AOx = x;
        betterfoliage$AOy = y;
        betterfoliage$AOz = z;
        betterfoliage$useAO = true;
    }

    @Override
    public void betterfoliage$resetAORender() {
        betterfoliage$useAO = false;
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
        return betterfoliage$isUsingCustomVerticalScale ? betterfoliage$crossedSquaresVerticalScale : original;
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
        return betterfoliage$crossedSquareSecondSprite != null ? betterfoliage$crossedSquareSecondSprite.getMinU()
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
        return betterfoliage$crossedSquareSecondSprite != null ? betterfoliage$crossedSquareSecondSprite.getMinV()
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
        return betterfoliage$crossedSquareSecondSprite != null ? betterfoliage$crossedSquareSecondSprite.getMaxU()
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
        return betterfoliage$crossedSquareSecondSprite != null ? betterfoliage$crossedSquareSecondSprite.getMaxV()
            : maxV;
    }

    /// QUAD 1
    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert0(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.WEST,
                ForgeDirection.NORTH,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 1,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert1(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.WEST,
                ForgeDirection.NORTH,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 2,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert2(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                ForgeDirection.EAST,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 3,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert3(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                ForgeDirection.EAST,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    /// QUAD 2
    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 4,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert4(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.EAST,
                ForgeDirection.SOUTH,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 5,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert5(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.EAST,
                ForgeDirection.SOUTH,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 6,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert6(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                ForgeDirection.WEST,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 7,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert7(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                ForgeDirection.WEST,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    /// QUAD 3
    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 8,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert8(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                ForgeDirection.WEST,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 9,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert9(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                ForgeDirection.WEST,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 10,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert10(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.EAST,
                ForgeDirection.NORTH,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 11,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert11(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.EAST,
                ForgeDirection.NORTH,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    /// QUAD 4
    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 12,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert12(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                ForgeDirection.EAST,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 13,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert13(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                ForgeDirection.EAST,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 14,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert14(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.WEST,
                ForgeDirection.SOUTH,
                ForgeDirection.DOWN);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @WrapOperation(
        method = "drawCrossedSquares",
        at = @At(
            value = "INVOKE",
            ordinal = 15,
            target = "Lnet/minecraft/client/renderer/Tessellator;addVertexWithUV(DDDDD)V"))
    private void betterfoliage$wrapVert15(Tessellator instance, double x, double y, double z, double u, double v,
        Operation<Void> original) {
        betterfoliage$applyRotation(x, y, z);

        if (betterfoliage$useAO) {
            RenderUtils.setAOForCrossedSquareVertex(
                (RenderBlocks) ((Object) this),
                betterfoliage$AOx,
                betterfoliage$AOy,
                betterfoliage$AOz,
                ForgeDirection.WEST,
                ForgeDirection.SOUTH,
                ForgeDirection.UP);
        }

        original.call(
            instance,
            betterfoliage$rotationPoint[0],
            betterfoliage$rotationPoint[1],
            betterfoliage$rotationPoint[2],
            u,
            v);
    }

    @Unique
    private void betterfoliage$applyRotation(double x, double y, double z) {
        betterfoliage$rotationPoint[0] = x;
        betterfoliage$rotationPoint[1] = y;
        betterfoliage$rotationPoint[2] = z;

        if (betterfoliage$rotationAxis.ordinal() > 5) return;

        // TODO handle AO rotation

        RenderUtils.rotateCounterclock(
            betterfoliage$rotationAxis,
            betterfoliage$centerX,
            betterfoliage$centerY,
            betterfoliage$centerZ,
            betterfoliage$rotationPoint);
    }
}
