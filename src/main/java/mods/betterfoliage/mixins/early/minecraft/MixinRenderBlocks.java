package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassColorOverride;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks implements IGrassColorOverride {

    @Shadow()
    public IBlockAccess blockAccess;

    @Shadow()
    public float colorRedTopLeft;
    @Shadow()
    public float colorRedBottomLeft;
    @Shadow()
    public float colorRedTopRight;
    @Shadow()
    public float colorRedBottomRight;

    @Shadow()
    public float colorGreenTopLeft;
    @Shadow()
    public float colorGreenBottomLeft;
    @Shadow()
    public float colorGreenTopRight;
    @Shadow()
    public float colorGreenBottomRight;

    @Shadow()
    public float colorBlueTopLeft;
    @Shadow()
    public float colorBlueBottomLeft;
    @Shadow()
    public float colorBlueTopRight;
    @Shadow()
    public float colorBlueBottomRight;

    @Unique
    private boolean betterfoliage$isRenderingGrass = false;

    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        betterfoliage$isRenderingGrass = isRenderingGrass;
    }

    @ModifyVariable(
        method = "renderBlockByRenderType",
        ordinal = 3,
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;setRenderBoundsFromBlock(Lnet/minecraft/block/Block;)V"))
    private int betterfoliage$getRenderTypeOverride(int originalRenderType, @Local(ordinal = 0, argsOnly = true) int x,
        @Local(ordinal = 1, argsOnly = true) int y, @Local(ordinal = 2, argsOnly = true) int z) {
        return Hooks.getRenderTypeOverride(blockAccess, x, y, z, originalRenderType);
    }

    // TODO Move to separate Grass mixin class
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
    /// Grass render mixins (Tall Grass)
    /// ================================
    @ModifyExpressionValue(
        method = "renderCrossedSquares",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;colorMultiplier(Lnet/minecraft/world/IBlockAccess;III)I"))
    private int betterfoliage$overrideShortGrassColor(int original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        // TODO: turn this into a hook like: Hooks.getGrassColor(IBlockAccess world, x, y, z); that checks custom grass color list
        return betterfoliage$isRenderingGrass ? blockAccess.getBiomeGenForCoords(x, z)
            .getBiomeGrassColor(x, y - 1, z) : original;
    }
}
