package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import mods.betterfoliage.client.Hooks;

// Angelica / NotFine overwrite 'shouldSideBeRendered',
// so we make sure to mixin _after_ that happens
@SuppressWarnings("UnusedMixin")
@Mixin(value = Block.class, priority = 1500)
public abstract class MixinBlock {

    @ModifyReturnValue(method = "getAmbientOcclusionLightValue", at = @At("RETURN"))
    private float betterfoliage$getAmbientOcclusionLightValueOverride(float original) {
        return Hooks.getAmbientOcclusionLightValueOverride((Block) (Object) this, original);
    }

    @ModifyReturnValue(method = "getUseNeighborBrightness", at = @At("RETURN"))
    private boolean betterfoliage$getUseNeighborBrightnessOverride(boolean original) {
        return Hooks.getUseNeighborBrightnessOverride((Block) (Object) this, original);
    }

    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"))
    private boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x, int y,
        int z, int side) {
        return Hooks.overrideIsPartialBlock(world, x, y, z, original);
    }
}
