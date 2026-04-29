package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_CustomSideSprites implements ICustomSideSpritesRenderer {

    @Unique
    ISpriteProvider betterfoliage$customSpriteProvider = null;

    @Override
    public void betterfoliage$setSpriteProvider(ISpriteProvider provider) {
        betterfoliage$customSpriteProvider = provider;
    }

    @Override
    public void betterfoliage$resetSpriteProvider() {
        betterfoliage$customSpriteProvider = null;
    }

    /// Bottom
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteBottom(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 0);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    /// Top
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteTop(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 1);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    /// North
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteNorth(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 2);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    /// South
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteSouth(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 3);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    /// West
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteWest(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 4);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }

    /// East
    @WrapOperation(
        method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithColorMultiplier" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V"))
    private void betterfoliage$OverrideSpriteEast(RenderBlocks instance, Block block, double xPos, double yPos,
        double zPos, IIcon baseSprite, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        if (betterfoliage$customSpriteProvider == null) {
            original.call(instance, block, xPos, yPos, zPos, baseSprite);
            return;
        }

        IIcon sprite = betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, 5);
        original.call(instance, block, xPos, yPos, zPos, sprite);
    }
}
