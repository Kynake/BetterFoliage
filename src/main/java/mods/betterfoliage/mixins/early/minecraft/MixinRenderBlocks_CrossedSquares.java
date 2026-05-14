package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
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
    private Block betterfoliage$AOBlock;

    @Unique
    private int betterfoliage$AOx;

    @Unique
    private int betterfoliage$AOy;

    @Unique
    private int betterfoliage$AOz;

    @Unique
    private float betterfoliage$AOr;

    @Unique
    private float betterfoliage$AOg;

    @Unique
    private float betterfoliage$AOb;

    @Shadow
    public IBlockAccess blockAccess;

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
    public void betterfoliage$setAORender(Block block, int x, int y, int z, float r, float g, float b) {
        betterfoliage$AOBlock = block;
        betterfoliage$AOx = x;
        betterfoliage$AOy = y;
        betterfoliage$AOz = z;
        betterfoliage$AOr = r;
        betterfoliage$AOg = g;
        betterfoliage$AOb = b;
        betterfoliage$useAO = true;
    }

    @Override
    public void betterfoliage$resetAORender() {
        betterfoliage$AOBlock = null;
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy + 1,
                betterfoliage$AOz - 1,
                ForgeDirection.WEST,
                true,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy - 1,
                betterfoliage$AOz - 1,
                ForgeDirection.WEST,
                false,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx + 1,
                betterfoliage$AOy - 1,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                false,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx + 1,
                betterfoliage$AOy + 1,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                true,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy + 1,
                betterfoliage$AOz + 1,
                ForgeDirection.EAST,
                true,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy - 1,
                betterfoliage$AOz + 1,
                ForgeDirection.EAST,
                false,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx - 1,
                betterfoliage$AOy - 1,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                false,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx - 1,
                betterfoliage$AOy + 1,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                true,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx - 1,
                betterfoliage$AOy + 1,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                true,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx - 1,
                betterfoliage$AOy - 1,
                betterfoliage$AOz,
                ForgeDirection.SOUTH,
                false,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy - 1,
                betterfoliage$AOz - 1,
                ForgeDirection.EAST,
                false,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy + 1,
                betterfoliage$AOz - 1,
                ForgeDirection.EAST,
                true,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx + 1,
                betterfoliage$AOy + 1,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                true,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx + 1,
                betterfoliage$AOy - 1,
                betterfoliage$AOz,
                ForgeDirection.NORTH,
                false,
                true,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy - 1,
                betterfoliage$AOz + 1,
                ForgeDirection.WEST,
                false,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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
            betterfoliage$setVertexAO(
                betterfoliage$AOBlock,
                betterfoliage$AOx,
                betterfoliage$AOy + 1,
                betterfoliage$AOz + 1,
                ForgeDirection.WEST,
                true,
                false,
                betterfoliage$AOr,
                betterfoliage$AOg,
                betterfoliage$AOb);
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

    @Unique
    private void betterfoliage$setVertexAO(Block block, int x, int y, int z, ForgeDirection side, boolean top,
        boolean left, float r, float g, float b) {

        int sideX = x + side.offsetX;
        int sideY = y + side.offsetY;
        int sideZ = z + side.offsetZ;

        Block neighborBlock = blockAccess.getBlock(sideX, sideY, sideZ);
        float neighborAO = neighborBlock.getAmbientOcclusionLightValue();

        int brightness = block.getMixedBrightnessForBlock(blockAccess, x, y, z);
        if (!neighborBlock.isOpaqueCube()) {
            brightness = neighborBlock.getMixedBrightnessForBlock(blockAccess, sideX, sideY, sideZ);
        }

        int vertBrightness;

        float sideMult = RenderUtils.getColorMultiplierBySide(side.ordinal());

        switch (side) {
            case DOWN -> { // YNEG
                --y;

                // Edges
                float scratchXYNN = blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZNN = blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
                float scratchYZNP = blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
                float scratchXYPN = blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
                int aoXYNN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z);
                int aoYZNN = block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1);
                int aoYZNP = block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1);
                int aoXYPN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z);

                // Vertices
                float scratchXYZNNN = scratchXYNN;
                float scratchXYZNNP = scratchXYNN;
                float scratchXYZPNN = scratchXYPN;
                float scratchXYZPNP = scratchXYPN;
                int aoXYZNNN = aoXYNN;
                int aoXYZNNP = aoXYNN;
                int aoXYZPNN = aoXYPN;
                int aoXYZPNP = aoXYPN;

                boolean blocksGrassA = blockAccess.getBlock(x + 1, y - 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x - 1, y - 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x, y - 1, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x, y - 1, z - 1)
                    .getCanBlockGrass();

                if (blocksGrassD || blocksGrassB) {
                    scratchXYZNNN = blockAccess.getBlock(x - 1, y, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z - 1);
                }

                if (blocksGrassC || blocksGrassB) {
                    scratchXYZNNP = blockAccess.getBlock(x - 1, y, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z + 1);
                }

                if (blocksGrassD || blocksGrassA) {
                    scratchXYZPNN = blockAccess.getBlock(x + 1, y, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z - 1);
                }

                if (blocksGrassC || blocksGrassA) {
                    scratchXYZPNP = blockAccess.getBlock(x + 1, y, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z + 1);
                }

                ++y;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (scratchXYZNNP + scratchXYNN + scratchYZNP + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZNNP, aoXYNN, aoYZNP, brightness);
                    } else {
                        // Top Right
                        sideMult *= (scratchYZNP + neighborAO + scratchXYZPNP + scratchXYPN) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZNP, aoXYZPNP, aoXYPN, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (scratchXYNN + scratchXYZNNN + neighborAO + scratchYZNN) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYNN, aoXYZNNN, aoYZNN, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (neighborAO + scratchYZNN + scratchXYPN + scratchXYZPNN) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZNN, aoXYPN, aoXYZPNN, brightness);
                    }
                }
            }

            case UP -> { // YPOS
                ++y;

                // Edges
                float scratchXYNP = blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchXYPP = blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZPN = blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
                float scratchYZPP = blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
                int aoXYNP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z);
                int aoXYPP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z);
                int aoYZPN = block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1);
                int aoYZPP = block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1);

                // Vertices
                float scratchXYZNPN_TNE = scratchXYNP;
                float scratchXYZPPN_TSE = scratchXYPP;
                float scratchXYZNPP_TNW = scratchXYNP;
                float scratchXYZPPP_TSW = scratchXYPP;
                int aoXYZNPN = aoXYNP;
                int aoXYZPPN = aoXYPP;
                int aoXYZNPP = aoXYNP;
                int aoXYZPPP = aoXYPP;

                boolean blocksGrassA = blockAccess.getBlock(x + 1, y + 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x - 1, y + 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x, y + 1, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x, y + 1, z - 1)
                    .getCanBlockGrass();

                if (blocksGrassD || blocksGrassB) {
                    scratchXYZNPN_TNE = blockAccess.getBlock(x - 1, y, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z - 1);
                }

                if (blocksGrassD || blocksGrassA) {
                    scratchXYZPPN_TSE = blockAccess.getBlock(x + 1, y, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z - 1);
                }

                if (blocksGrassC || blocksGrassB) {
                    scratchXYZNPP_TNW = blockAccess.getBlock(x - 1, y, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z + 1);
                }

                if (blocksGrassC || blocksGrassA) {
                    scratchXYZPPP_TSW = blockAccess.getBlock(x + 1, y, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z + 1);
                }

                --y;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (scratchYZPP + neighborAO + scratchXYZPPP_TSW + scratchXYPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZPP, aoXYZPPP, aoXYPP, brightness);
                    } else {
                        // Top Right
                        sideMult *= (scratchXYZNPP_TNW + scratchXYNP + scratchYZPP + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZNPP, aoXYNP, aoYZPP, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (neighborAO + scratchYZPN + scratchXYPP + scratchXYZPPN_TSE) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZPN, aoXYPP, aoXYZPPN, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (scratchXYNP + scratchXYZNPN_TNE + neighborAO + scratchYZPN) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYNP, aoXYZNPN, aoYZPN, brightness);
                    }
                }
            }

            case NORTH -> { // ZNEG
                --z;

                // Edges
                float scratchXZNN = blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZNN = blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZPN = blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
                float scratchXZPN = blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
                int aoXZNN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z);
                int aoYZNN = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z);
                int aoYZPN = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z);
                int aoXZPN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z);

                // Vertices
                float scratchXYZNNN = scratchXZNN;
                float scratchXYZNPN = scratchXZNN;
                float scratchXYZPNN = scratchXZPN;
                float scratchXYZPPN = scratchXZPN;
                int aoXYZNNN = aoXZNN;
                int aoXYZNPN = aoXZNN;
                int aoXYZPNN = aoXZPN;
                int aoXYZPPN = aoXZPN;

                boolean blocksGrassA = blockAccess.getBlock(x + 1, y, z - 1)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x - 1, y, z - 1)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x, y + 1, z - 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x, y - 1, z - 1)
                    .getCanBlockGrass();

                if (blocksGrassB || blocksGrassD) {
                    scratchXYZNNN = blockAccess.getBlock(x - 1, y - 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y - 1, z);
                }

                if (blocksGrassB || blocksGrassC) {
                    scratchXYZNPN = blockAccess.getBlock(x - 1, y + 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPN = block.getMixedBrightnessForBlock(blockAccess, x - 1, y + 1, z);
                }

                if (blocksGrassA || blocksGrassD) {
                    scratchXYZPNN = blockAccess.getBlock(x + 1, y - 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y - 1, z);
                }

                if (blocksGrassA || blocksGrassC) {
                    scratchXYZPPN = blockAccess.getBlock(x + 1, y + 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPN = block.getMixedBrightnessForBlock(blockAccess, x + 1, y + 1, z);
                }

                ++z;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (scratchXZNN + scratchXYZNPN + neighborAO + scratchYZPN) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZNN, aoXYZNPN, aoYZPN, brightness);
                    } else {
                        // Top Right
                        sideMult *= (neighborAO + scratchYZPN + scratchXZPN + scratchXYZPPN) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZPN, aoXZPN, aoXYZPPN, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (scratchXYZNNN + scratchXZNN + scratchYZNN + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZNNN, aoXZNN, aoYZNN, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (scratchYZNN + neighborAO + scratchXYZPNN + scratchXZPN) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZNN, aoXYZPNN, aoXZPN, brightness);
                    }
                }
            }

            case SOUTH -> { // ZPOS
                ++z;

                // Edges
                float scratchXZNP = blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchXZPP = blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZNP = blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
                float scratchYZPP = blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
                int aoXZNP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z);
                int aoXZPP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z);
                int aoYZNP = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z);
                int aoYZPP = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z);

                // Vertices
                float scratchXYZNNP = scratchXZNP;
                float scratchXYZNPP = scratchXZNP;
                float scratchXYZPNP = scratchXZPP;
                float scratchXYZPPP = scratchXZPP;
                int aoXYZNNP = aoXZNP;
                int aoXYZNPP = aoXZNP;
                int aoXYZPNP = aoXZPP;
                int aoXYZPPP = aoXZPP;

                boolean blocksGrassA = blockAccess.getBlock(x + 1, y, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x - 1, y, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x, y + 1, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x, y - 1, z + 1)
                    .getCanBlockGrass();

                if (blocksGrassB || blocksGrassD) {
                    scratchXYZNNP = blockAccess.getBlock(x - 1, y - 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y - 1, z);
                }

                if (blocksGrassB || blocksGrassC) {
                    scratchXYZNPP = blockAccess.getBlock(x - 1, y + 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPP = block.getMixedBrightnessForBlock(blockAccess, x - 1, y + 1, z);
                }

                if (blocksGrassA || blocksGrassD) {
                    scratchXYZPNP = blockAccess.getBlock(x + 1, y - 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y - 1, z);
                }

                if (blocksGrassA || blocksGrassC) {
                    scratchXYZPPP = blockAccess.getBlock(x + 1, y + 1, z)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPP = block.getMixedBrightnessForBlock(blockAccess, x + 1, y + 1, z);
                }

                --z;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (scratchXZNP + scratchXYZNPP + neighborAO + scratchYZPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZNP, aoXYZNPP, aoYZPP, brightness);
                    } else {
                        // Top Right
                        sideMult *= (neighborAO + scratchYZPP + scratchXZPP + scratchXYZPPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZPP, aoXZPP, aoXYZPPP, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (scratchXYZNNP + scratchXZNP + scratchYZNP + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZNNP, aoXZNP, aoYZNP, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (scratchYZNP + neighborAO + scratchXYZPNP + scratchXZPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoYZNP, aoXYZPNP, aoXZPP, brightness);
                    }
                }
            }

            case WEST -> { // XNEG

                --x;

                // Edges
                float scratchXYNN = blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
                float scratchXZNN = blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
                float scratchXZNP = blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
                float scratchXYNP = blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
                int aoXYNN = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z);
                int aoXZNN = block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1);
                int aoXZNP = block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1);
                int aoXYNP = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z);

                // Vertices
                float scratchXYZNNN = scratchXZNN;
                float scratchXYZNNP = scratchXZNP;
                float scratchXYZNPN = scratchXZNN;
                float scratchXYZNPP = scratchXZNP;
                int aoXYZNNN = aoXZNN;
                int aoXYZNNP = aoXZNP;
                int aoXYZNPN = aoXZNN;
                int aoXYZNPP = aoXZNP;

                boolean blocksGrassA = blockAccess.getBlock(x - 1, y + 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x - 1, y - 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x - 1, y, z - 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x - 1, y, z + 1)
                    .getCanBlockGrass();

                if (blocksGrassC || blocksGrassB) {
                    scratchXYZNNN = blockAccess.getBlock(x, y - 1, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNN = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z - 1);
                }

                if (blocksGrassD || blocksGrassB) {
                    scratchXYZNNP = blockAccess.getBlock(x, y - 1, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNNP = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z + 1);
                }

                if (blocksGrassC || blocksGrassA) {
                    scratchXYZNPN = blockAccess.getBlock(x, y + 1, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPN = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z - 1);
                }

                if (blocksGrassD || blocksGrassA) {
                    scratchXYZNPP = blockAccess.getBlock(x, y + 1, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZNPP = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z + 1);
                }

                ++x;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (neighborAO + scratchXZNN + scratchXYNP + scratchXYZNPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZNP, aoXYNP, aoXYZNPP, brightness);
                    } else {
                        // Top Right
                        sideMult *= (scratchXYNN + scratchXYZNNP + neighborAO + scratchXZNN) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYNN, aoXYZNNP, aoXZNP, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (scratchXZNN + neighborAO + scratchXYZNPN + scratchXYNP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZNN, aoXYZNPN, aoXYNP, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (scratchXYZNNN + scratchXYNN + scratchXZNN + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZNNN, aoXYNN, aoXZNN, brightness);
                    }
                }
            }

            case EAST -> { // XPOS

                ++x;

                // Edges
                float scratchXYPN = blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
                float scratchXZPN = blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
                float scratchXZPP = blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
                float scratchXYPP = blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
                int aoXYPN = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z);
                int aoXZPN = block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1);
                int aoXZPP = block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1);
                int aoXYPP = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z);

                // Vertices
                float scratchXYZPNN = scratchXZPN;
                float scratchXYZPNP = scratchXZPP;
                float scratchXYZPPN = scratchXYPN;
                float scratchXYZPPP = scratchXZPP;
                int aoXYZPNN = aoXZPN;
                int aoXYZPNP = aoXZPP;
                int aoXYZPPN = aoXZPN;
                int aoXYZPPP = aoXZPP;

                boolean blocksGrassA = blockAccess.getBlock(x + 1, y + 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassB = blockAccess.getBlock(x + 1, y - 1, z)
                    .getCanBlockGrass();
                boolean blocksGrassC = blockAccess.getBlock(x + 1, y, z + 1)
                    .getCanBlockGrass();
                boolean blocksGrassD = blockAccess.getBlock(x + 1, y, z - 1)
                    .getCanBlockGrass();

                if (blocksGrassB || blocksGrassD) {
                    scratchXYZPNN = blockAccess.getBlock(x, y - 1, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNN = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z - 1);
                }

                if (blocksGrassB || blocksGrassC) {
                    scratchXYZPNP = blockAccess.getBlock(x, y - 1, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPNP = block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z + 1);
                }

                if (blocksGrassA || blocksGrassD) {
                    scratchXYZPPN = blockAccess.getBlock(x, y + 1, z - 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPN = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z - 1);
                }

                if (blocksGrassA || blocksGrassC) {
                    scratchXYZPPP = blockAccess.getBlock(x, y + 1, z + 1)
                        .getAmbientOcclusionLightValue();
                    aoXYZPPP = block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z + 1);
                }

                --x;

                if (top) {
                    if (left) {
                        // Top Left
                        sideMult *= (scratchXYPN + scratchXYZPNP + neighborAO + scratchXZPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYPN, aoXYZPNP, aoXZPP, brightness);

                    } else {
                        // Top Right
                        sideMult *= (neighborAO + scratchXZPP + scratchXYPP + scratchXYZPPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZPP, aoXYPP, aoXYZPPP, brightness);
                    }
                } else {
                    if (left) {
                        // Bottom Left
                        sideMult *= (scratchXYZPNN + scratchXYPN + scratchXZPN + neighborAO) / 4.0F;
                        vertBrightness = getAoBrightness(aoXYZPNN, aoXYPN, aoXZPN, brightness);
                    } else {
                        // Bottom Right
                        sideMult *= (scratchXZPN + neighborAO + scratchXYZPPN + scratchXYPP) / 4.0F;
                        vertBrightness = getAoBrightness(aoXZPN, aoXYZPPN, aoXYPP, brightness);
                    }
                }
            }

            default -> {
                return;
            }
        }

        float vertR = r * sideMult;
        float vertG = g * sideMult;
        float vertB = b * sideMult;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(vertR, vertG, vertB);
        tessellator.setBrightness(vertBrightness);
    }
}
