package mods.betterfoliage.mixins.late.betterfoliage.gt5u;

import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.client.render.features.AlgaeRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(AlgaeRenderer.class)
public abstract class MixinAlgaeRenderer {

    @ModifyVariable(method = "renderWorldBlock", name = "color", at = @At(value = "STORE"), remap = false, require = 1)
    private int betterfoliage$applyGT5UPollutionTintAlgae(int original, IBlockAccess world, int x, int y, int z) {
        return GT5UIntegration.applyPollutionTintLeaf(original, x, z);
    }
}
