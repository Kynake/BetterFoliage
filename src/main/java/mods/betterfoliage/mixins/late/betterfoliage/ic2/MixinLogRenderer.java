package mods.betterfoliage.mixins.late.betterfoliage.ic2;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.integration.IC2Integration;
import mods.betterfoliage.client.render.features.LogRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LogRenderer.class)
public abstract class MixinLogRenderer {

    @WrapMethod(method = "determineLogAxis", remap = false)
    private static ForgeDirection betterfoliage$ic2OverrideLogAxis(IBlockAccess world, int x, int y, int z,
        Operation<ForgeDirection> original) {
        if (IC2Integration.rubberWoodFilter.matches(world, x, y, z)) {
            return ForgeDirection.UP;
        }

        return original.call(world, x, y, z);
    }

    @ModifyVariable(method = "getConnectorSprite", name = "meta", at = @At(value = "STORE"), remap = false)
    private static int betterfoliage$ic2OverrideConnectorMeta(int meta,
        @Local(argsOnly = true, name = "block") Block block) {
        return IC2Integration.rubberWoodFilter.matchesNoMeta(block) ? 0 : meta;
    }
}
