package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(ChunkCache.class)
public abstract class MixinChunkCache {

    @Shadow()
    private World worldObj;

    @ModifyExpressionValue(
        method = "getSkyBlockTypeBrightness",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getUseNeighborBrightness()Z"))
    private boolean betterfoliage$overrideUseNeighborBrightness(boolean original,
        @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y,
        @Local(argsOnly = true, ordinal = 2) int z) {
        return Hooks.overrideIsPartialBlock(worldObj, x, y, z, original);
    }
}
