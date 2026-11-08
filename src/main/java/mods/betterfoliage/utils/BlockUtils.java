package mods.betterfoliage.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

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
        return block.colorMultiplier(world, x, y, z);
    }
}
