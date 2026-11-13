package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import gregtech.blocks.fluids.BlockWaterlike;
import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockWaterlike.class)
public class MixinBlockWaterlike {

    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"), require = 1)
    private boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x, int y,
        int z, int side) {
        return Hooks.overrideIsPartialBlock(world, x, y, z, original);
    }
}
