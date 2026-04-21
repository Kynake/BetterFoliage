package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.mixins.interfaces.minecraft.IOffsetSpriteRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_OffsetSprite implements IOffsetSpriteRenderer {

    @Unique
    private int betterFoliage$x;

    @Unique
    private int betterFoliage$y;

    @Unique
    private int betterFoliage$z;

    @Override
    public void betterfoliage$setSpriteOffset(int xOffset, int yOffset, int zOffset) {
        betterFoliage$x = xOffset;
        betterFoliage$y = yOffset;
        betterFoliage$z = zOffset;
    }

    /// Allows overriding the block's position when getting its textures.
    /// Useful when rendering, for example, snowed grass in place of the dirt block below it.
    @WrapMethod(
        method = "getBlockIcon(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;")
    private IIcon betterfoliage$getBlockIconWithOffset(Block block, IBlockAccess world, int x, int y, int z, int side,
        Operation<IIcon> original) {
        return original.call(block, world, x + betterFoliage$x, y + betterFoliage$y, z + betterFoliage$z, side);
    }

}
