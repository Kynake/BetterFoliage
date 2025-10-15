package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(Block.class)
public abstract class MixinBlock {

    // What: Invoke BF code to overrule AO transparency value
    // Why: Allows us to have light behave properly on non-solid log blocks without
    // messing with isOpaqueBlock(), which could have gameplay effects
    @ModifyReturnValue(method = "getAmbientOcclusionLightValue", at = @At("RETURN"))
    private float betterfoliage$getAmbientOcclusionLightValueOverride(float original) {
        return Hooks.getAmbientOcclusionLightValueOverride(original, (Block) (Object) this);
    }

    // What: Invoke BF code to override block.useNeighborBrightness
    // Why: Allows us to have light behave properly on non-solid log blocks
    @ModifyReturnValue(method = "getUseNeighborBrightness", at = @At("RETURN"))
    private boolean betterfoliage$getUseNeighborBrightnessOverride(boolean original) {
        return Hooks.getUseNeighborBrightnessOverride(original, (Block) (Object) this);
    }

    // What: Invoke BF code to overrule condition
    // Why: Allows us to make log blocks non-solid without
    // messing with isOpaqueBlock(), which could have gameplay effects
    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"))
    private boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x, int y,
        int z, int side) {
        return Hooks.shouldRenderBlockSideOverride(original, world, x, y, z, side);
    }
}
