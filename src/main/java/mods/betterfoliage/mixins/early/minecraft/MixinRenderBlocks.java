package mods.betterfoliage.mixins.early.minecraft;

import mods.betterfoliage.mixins.interfaces.minecraft.IRendererByType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks implements IRendererByType {

    @Shadow()
    public abstract boolean renderBlockByRenderType(Block block, int x, int y, int z);

    @Shadow()
    public IBlockAccess blockAccess;

    @Unique
    private boolean betterfoliage$shouldOverrideRenderType = true;

    @ModifyVariable(
        method = "renderBlockByRenderType",
        name = "l",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/RenderBlocks;setRenderBoundsFromBlock(Lnet/minecraft/block/Block;)V"))
    private int betterfoliage$getRenderTypeOverride(int originalRenderType, @Local(ordinal = 0, argsOnly = true) int x,
        @Local(ordinal = 1, argsOnly = true) int y, @Local(ordinal = 2, argsOnly = true) int z) {
        return betterfoliage$shouldOverrideRenderType
            ? Hooks.getRenderTypeOverride(blockAccess, x, y, z, originalRenderType)
            : originalRenderType;
    }

    @Override
    public boolean betterfoliage$renderBaseBlock(Block block, int x, int y, int z) {
        betterfoliage$shouldOverrideRenderType = false;
        final boolean didRender = renderBlockByRenderType(block, x, y, z);
        betterfoliage$shouldOverrideRenderType = true;
        return didRender;
    }
}
