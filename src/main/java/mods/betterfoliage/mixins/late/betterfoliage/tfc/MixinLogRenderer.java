package mods.betterfoliage.mixins.late.betterfoliage.tfc;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.client.integration.TFCIntegration;
import mods.betterfoliage.client.render.features.LogRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LogRenderer.class)
public abstract class MixinLogRenderer {

    @WrapMethod(method = "determineLogAxis", remap = false)
    private static ForgeDirection betterfoliage$tfcOverrideLogAxis(IBlockAccess world, int x, int y, int z,
        Operation<ForgeDirection> original) {
        final Block log = world.getBlock(x, y, z);

        // Test horizontal logs first, since they're a subclass of the vertical logs
        if (TFCIntegration.horizontalLogs.matchesNoMeta(log)) {
            final int meta = world.getBlockMetadata(x, y, z);
            return TFCIntegration.isLogFacingNorthSouth(meta) ? ForgeDirection.SOUTH : ForgeDirection.EAST;
        }

        if (TFCIntegration.verticalLogs.matchesNoMeta(log)) {
            return ForgeDirection.UP;
        }

        return original.call(world, x, y, z);
    }
}
