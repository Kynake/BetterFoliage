package mods.betterfoliage.mixins.late.betterfoliage.gt6;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.client.integration.GT6Integration;
import mods.betterfoliage.client.render.features.LogRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LogRenderer.class)
public abstract class MixinLogRenderer {

    @WrapMethod(method = "getConnectorSprite", remap = false)
    private static IIcon betterfoliage$gt6OverrideConnectorSprite(RenderBlocks renderer, IBlockAccess world,
        Block block, int x, int y, int z, ForgeDirection side, Operation<IIcon> original) {
        if (!GT6Integration.treeHole.matches(world, x, y, z)) {
            return original.call(renderer, world, block, x, y, z, side);
        }

        return GT6Integration.getTreeHoleDefaultSprite(world, x, y, z);
    }
}
