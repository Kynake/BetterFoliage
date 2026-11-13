package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import mods.betterfoliage.mixins.interfaces.minecraft.IOffsetSpriteRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_OffsetSprite implements IOffsetSpriteRenderer {

    @Unique
    private int betterFoliage$x;

    @Unique
    private int betterFoliage$y;

    @Unique
    private int betterFoliage$z;

    @Override
    public void betterfoliage$setSpriteOffset(int xOffset, int yOffset, int zOffset) {
        betterFoliage$x = xOffset;
        betterFoliage$y = yOffset;
        betterFoliage$z = zOffset;
    }

    /// Allows overriding the block's position when getting its textures.
    /// Useful when rendering, for example, snowed grass in place of the dirt block below it.
    @ModifyVariable(
        method = "getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;",
        argsOnly = true,
        ordinal = 0,
        at = @At("HEAD"),
        require = 1)
    private int betterfoliage$addBlockOffsetX(int x) {
        return x + betterFoliage$x;
    }

    @ModifyVariable(
        method = "getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;",
        argsOnly = true,
        ordinal = 1,
        at = @At("HEAD"),
        require = 1)
    private int betterfoliage$addBlockOffsetY(int y) {
        return y + betterFoliage$y;
    }

    @ModifyVariable(
        method = "getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;",
        argsOnly = true,
        ordinal = 2,
        at = @At("HEAD"),
        require = 1)
    private int betterfoliage$addBlockOffsetZ(int z) {
        return z + betterFoliage$z;
    }
}
