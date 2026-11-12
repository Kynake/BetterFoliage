package mods.betterfoliage.mixins.late.betterfoliage.ic2;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

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

    @WrapMethod(method = "getConnectorSprite", remap = false)
    private static IIcon betterfoliage$ic2OverrideConnectorSprite(RenderBlocks renderer, IBlockAccess world,
        Block block, int x, int y, int z, ForgeDirection side, Operation<IIcon> original) {
        return IC2Integration.rubberWoodFilter.matchesNoMeta(block)
            ? renderer.getBlockIconFromSide(block, side.ordinal())
            : original.call(renderer, world, block, x, y, z, side);
    }
}
