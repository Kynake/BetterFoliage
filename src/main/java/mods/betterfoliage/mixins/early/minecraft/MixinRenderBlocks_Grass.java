package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_Grass implements IGrassBlockRenderer {

    // spotless:off
    @Shadow() public float colorRedTopLeft;
    @Shadow() public float colorRedBottomLeft;
    @Shadow() public float colorRedTopRight;
    @Shadow() public float colorRedBottomRight;

    @Shadow() public float colorGreenTopLeft;
    @Shadow() public float colorGreenBottomLeft;
    @Shadow() public float colorGreenTopRight;
    @Shadow() public float colorGreenBottomRight;

    @Shadow() public float colorBlueTopLeft;
    @Shadow() public float colorBlueBottomLeft;
    @Shadow() public float colorBlueTopRight;
    @Shadow() public float colorBlueBottomRight;
    // spotless:on

    @Unique
    private boolean betterfoliage$isRenderingGrass = false;

    @Unique
    private float betterfoliage$shortGrassVerticalScale;

    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        betterfoliage$isRenderingGrass = isRenderingGrass;
    }

    public void betterfoliage$setShortVerticalGrassScale(float shortGrassScale) {
        betterfoliage$shortGrassVerticalScale = shortGrassScale;
    }

    /// =============================
    /// Grass render mixins (With AO)
    /// =============================
    @Unique
    private void betterfoliage$applyAOBlockColor(float r, float g, float b) {
        if (betterfoliage$isRenderingGrass) {
            colorRedTopLeft *= r;
            colorGreenTopLeft *= g;
            colorBlueTopLeft *= b;

            colorRedBottomLeft *= r;
            colorGreenBottomLeft *= g;
            colorBlueBottomLeft *= b;

            colorRedTopRight *= r;
            colorGreenTopRight *= g;
            colorBlueTopRight *= b;

            colorRedBottomRight *= r;
            colorGreenBottomRight *= g;
            colorBlueBottomRight *= b;
        }
    }

    @ModifyVariable(
        method = "renderStandardBlockWithAmbientOcclusion",
        ordinal = 0,
        at = @At(
            value = "INVOKE_ASSIGN",
            ordinal = 2,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;"))
    private IIcon betterfoliage$applyAOColorNorth(IIcon icon, @Local(argsOnly = true, ordinal = 0) float r,
        @Local(argsOnly = true, ordinal = 1) float g, @Local(argsOnly = true, ordinal = 2) float b) {
        betterfoliage$applyAOBlockColor(r, g, b);
        return icon;
    }

    @ModifyVariable(
        method = "renderStandardBlockWithAmbientOcclusion",
        ordinal = 0,
        at = @At(
            value = "INVOKE_ASSIGN",
            ordinal = 3,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;"))
    private IIcon betterfoliage$applyAOColorSouth(IIcon icon, @Local(argsOnly = true, ordinal = 0) float r,
        @Local(argsOnly = true, ordinal = 1) float g, @Local(argsOnly = true, ordinal = 2) float b) {
        betterfoliage$applyAOBlockColor(r, g, b);
        return icon;
    }

    @ModifyVariable(
        method = "renderStandardBlockWithAmbientOcclusion",
        ordinal = 0,
        at = @At(
            value = "INVOKE_ASSIGN",
            ordinal = 5,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;"))
    private IIcon betterfoliage$applyAOColorWest(IIcon icon, @Local(argsOnly = true, ordinal = 0) float r,
        @Local(argsOnly = true, ordinal = 1) float g, @Local(argsOnly = true, ordinal = 2) float b) {
        betterfoliage$applyAOBlockColor(r, g, b);
        return icon;
    }

    @ModifyVariable(
        method = "renderStandardBlockWithAmbientOcclusion",
        ordinal = 0,
        at = @At(
            value = "INVOKE_ASSIGN",
            ordinal = 6,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;"))
    private IIcon betterfoliage$applyAOColorEast(IIcon icon, @Local(argsOnly = true, ordinal = 0) float r,
        @Local(argsOnly = true, ordinal = 1) float g, @Local(argsOnly = true, ordinal = 2) float b) {
        betterfoliage$applyAOBlockColor(r, g, b);
        return icon;
    }

    /// ================================
    /// Grass render mixins (Without AO)
    /// ================================
    @Unique
    private boolean betterfoliage$overrideTessellatorColor(Tessellator tessellator, float rBase, float gBase,
        float bBase, float r, float g, float b) {
        if (!betterfoliage$isRenderingGrass) {
            return true;
        }

        tessellator.setColorOpaque_F(r * rBase, g * gBase, b * bBase);
        return false;
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 2,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"))
    private boolean betterfoliage$applyMultColorNorth(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$overrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 4,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"))
    private boolean betterfoliage$applyMultColorSouth(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$overrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 6,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"))
    private boolean betterfoliage$applyMultColorWest(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$overrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 8,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"))
    private boolean betterfoliage$applyMultColorEast(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$overrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    /// ================================
    /// Tall Grass render mixins
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
        return betterfoliage$isRenderingGrass ? betterfoliage$shortGrassVerticalScale : original;
    }
}
