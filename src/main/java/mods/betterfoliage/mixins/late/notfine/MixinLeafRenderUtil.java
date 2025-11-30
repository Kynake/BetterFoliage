package mods.betterfoliage.mixins.late.notfine;

import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import jss.notfine.util.LeafRenderUtil;
import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(value = LeafRenderUtil.class, priority = 1500)
public abstract class MixinLeafRenderUtil {

    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"), remap = false)
    private static boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x,
        int y, int z, int side) {
        return Hooks.overrideIsPartialBlock(original, world, x, y, z);
    }
}
