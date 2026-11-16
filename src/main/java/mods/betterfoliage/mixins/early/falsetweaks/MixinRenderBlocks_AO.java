package mods.betterfoliage.mixins.early.falsetweaks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.falsepattern.falsetweaks.modules.ao.AORenderer;
import com.falsepattern.falsetweaks.modules.ao.AmbientOcclusionRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_AO implements IGrassBlockRenderer, ICustomSideSpritesRenderer {

    @Unique
    private boolean betterfoliage$isRenderingGrassNoAO = false;

    @Unique
    ISpriteProvider betterfoliage$customSpriteProviderNoAO = null;

    /// ===============================
    /// AmbientOcclusionRenderer bridge
    /// ===============================

    @Dynamic // Added via mixin by FalseTweaks
    @Shadow(remap = false)
    private AORenderer ft$ao$renderer;

    @Override
    public void betterfoliage$setSpriteProvider(ISpriteProvider provider) {
        betterfoliage$customSpriteProviderNoAO = provider;

        if (this.ft$ao$renderer == null) {
            this.ft$ao$renderer = new AmbientOcclusionRenderer();
        }

        ICustomSideSpritesRenderer bridge = (ICustomSideSpritesRenderer) ft$ao$renderer;
        bridge.betterfoliage$setSpriteProvider(provider);
    }

    @Override
    public void betterfoliage$resetSpriteProvider() {
        betterfoliage$customSpriteProviderNoAO = null;

        if (this.ft$ao$renderer == null) {
            this.ft$ao$renderer = new AmbientOcclusionRenderer();
        }

        ICustomSideSpritesRenderer bridge = (ICustomSideSpritesRenderer) ft$ao$renderer;
        bridge.betterfoliage$resetSpriteProvider();
    }

    @Override
    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        betterfoliage$isRenderingGrassNoAO = isRenderingGrass;

        if (this.ft$ao$renderer == null) {
            this.ft$ao$renderer = new AmbientOcclusionRenderer();
        }

        IGrassBlockRenderer bridge = (IGrassBlockRenderer) ft$ao$renderer;
        bridge.betterfoliage$setGrassRender(isRenderingGrass);
    }

    /// ================================
    /// Grass render mixins (Without AO)
    /// ================================
    @Unique
    private boolean betterfoliage$ftOverrideTessellatorColor(Tessellator tessellator, float rBase, float gBase,
        float bBase, float r, float g, float b) {
        if (!betterfoliage$isRenderingGrassNoAO) {
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
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"),
        require = 1)
    private boolean betterfoliage$applyMultColorNorth(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$ftOverrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 4,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"),
        require = 1)
    private boolean betterfoliage$applyMultColorSouth(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$ftOverrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 6,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"),
        require = 1)
    private boolean betterfoliage$applyMultColorWest(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$ftOverrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    @WrapWithCondition(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 8,
            target = "Lnet/minecraft/client/renderer/Tessellator;setColorOpaque_F(FFF)V"),
        require = 1)
    private boolean betterfoliage$applyMultColorEast(Tessellator tessellator, float rBase, float gBase, float bBase,
        @Local(argsOnly = true, ordinal = 0) float r, @Local(argsOnly = true, ordinal = 1) float g,
        @Local(argsOnly = true, ordinal = 2) float b) {
        return betterfoliage$ftOverrideTessellatorColor(tessellator, rBase, gBase, bBase, r, g, b);
    }

    /// ===================================
    /// Sprite replacer mixins (Without AO)
    /// ===================================

    // Bottom
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteBottom(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 0);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    // Top
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteTop(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 1);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    // North
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteNorth(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 2);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    // South
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteSouth(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 3);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    // West
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteWest(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 4);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    // East
    @WrapOperation(
        method = "renderStandardBlockWithColorMultiplier",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"),
        require = 1)
    private void betterfoliage$OverrideSpriteEast(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProviderNoAO == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProviderNoAO.getSpriteForCoord(x, y, z, 5);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }
}
