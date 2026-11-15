package mods.betterfoliage.mixins.late.betterfoliage.gt5u;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.utils.BlockUtils;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockUtils.class)
public abstract class MixinBlockUtils {

    @ModifyReturnValue(
        method = "getStandardColorMultiplier(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;III)I",
        at = @At("RETURN"),
        remap = false,
        require = 1)
    private static int betterfoliage$gt5uOverridePollutionTintStandard(int baseColor, IBlockAccess world, Block block,
        int x, int y, int z) {
        return GT5UIntegration.applyPollutionTintStandard(baseColor, block, x, z);
    }
}
