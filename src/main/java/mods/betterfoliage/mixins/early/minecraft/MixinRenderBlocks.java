package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {

    // What: Invoke BF code to overrule the return value of Block.getRenderType()
    // Why: Allows us to use custom block renderers for any block, without touching block code
    @ModifyExpressionValue(
        method = "renderBlockByRenderType",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderType()I"))
    private int betterfoliage$getRenderTypeOverride(int originalRenderType, Block block, int x, int y, int z) {
        return Hooks
            .getRenderTypeOverride(((RenderBlocks) (Object) this).blockAccess, x, y, z, block, originalRenderType);
    }
}
