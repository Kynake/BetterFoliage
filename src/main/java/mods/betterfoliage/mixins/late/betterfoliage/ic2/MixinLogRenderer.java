package mods.betterfoliage.mixins.late.betterfoliage.ic2;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.client.integration.IC2Integration;
import mods.betterfoliage.client.render.features.LogRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LogRenderer.class)
public class MixinLogRenderer {

    @WrapMethod(method = "determineLogAxis", remap = false)
    private static ForgeDirection betterfoliage$ic2OverrideLogAxis(IBlockAccess world, int x, int y, int z,
        Operation<ForgeDirection> original) {
        if (IC2Integration.rubberWoodFilter.matches(world, x, y, z)) {
            return ForgeDirection.UP;
        }

        return original.call(world, x, y, z);
    }
}
