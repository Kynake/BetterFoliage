package mods.betterfoliage.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.client.integration.GT5UIntegration;

public final class BlockUtils {

    public static boolean isSnow(Block block) {
        final Material material = block.getMaterial();
        return material == Material.snow || material == Material.craftedSnow;
    }

    public static boolean isWater(Block block) {
        return block.getMaterial() == Material.water;
    }

    public static int getStandardColorMultiplier(IBlockAccess world, int x, int y, int z) {
        return getStandardColorMultiplier(world, world.getBlock(x, y, z), x, y, z);
    }

    public static int getStandardColorMultiplier(IBlockAccess world, Block block, int x, int y, int z) {
        final int baseColor = block.colorMultiplier(world, x, y, z);
        return GT5UIntegration.applyPollutionTintStandard(baseColor, block, x, z);
    }
}
