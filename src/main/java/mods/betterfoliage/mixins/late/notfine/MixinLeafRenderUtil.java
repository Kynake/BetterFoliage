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

    // What: Invoke BF code to overrule leaf render condition when NotFine / Angelica is present
    // Why: NotFine / Angelica has a helper class that overrides the default shouldRenderSide() method
    // for vanilla and modded leaf blocks. This inhibits the default mixin that fixes
    // rendering for blocks next to rounded logs, resulting in holes in leaf blocks.
    @ModifyReturnValue(method = "shouldSideBeRendered", at = @At("RETURN"), remap = false)
    private static boolean betterfoliage$shouldRenderBlockSideOverride(boolean original, IBlockAccess world, int x,
        int y, int z, int side) {
        return Hooks.overrideIsPartialBlock(original, world, x, y, z);
    }
}
