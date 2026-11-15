package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import mods.betterfoliage.client.integration.GT6Integration;

@SuppressWarnings("UnusedMixin")
@Mixin(MultiTileEntityBlock.class)
public abstract class MixinMultiTileEntityBlock {

    @WrapMethod(method = "getIcon(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;", require = 1)
    private IIcon betterfoliage$overrideGetIconWithWorld(IBlockAccess world, int x, int y, int z, int side,
        Operation<IIcon> original) {
        if (!GT6Integration.treeHole.matches(world, x, y, z)) {
            return original.call(world, x, y, z, side);
        }

        return GT6Integration.getTreeHoleSpriteForSide(world, x, y, z, side);
    }
}
