package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import gregapi.render.ITexture;
import mods.octarinecore.client.render.ExtendedRenderBlocks;

@SuppressWarnings("UnusedMixin")
@Mixin(ITexture.Util.class)
public abstract class MixinITextureUtil {

    @WrapMethod(method = "renderFixedNegativeYFacing", remap = false)
    private static void betterfoliage$renderFixedNegativeYFacing(IIcon icon, RenderBlocks renderer, Block block, int x,
        int y, int z, Operation<Void> original) {
        if (renderer instanceof ExtendedRenderBlocks) {
            renderer.renderFaceYNeg(block, x, y, z, icon);
            return;
        }

        original.call(icon, renderer, block, x, y, z);
    }
}
