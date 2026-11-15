package mods.betterfoliage.mixins.early.minecraftforge;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockFluidBase.class)
public class MixinBlockFluidBase {

    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"), require = 1)
    private boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x, int y,
        int z, int side) {
        return Hooks.overrideIsPartialBlock(world, x, y, z, original);
    }
}
