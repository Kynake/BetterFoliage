package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {

    @Shadow()
    public IBlockAccess blockAccess;

    // What: Invoke BF code to overrule the return value of Block.getRenderType()
    // Why: Allows us to use custom block renderers for any block, without touching block code
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
}
