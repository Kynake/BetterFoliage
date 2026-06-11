package mods.betterfoliage.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public final class BlockUtils {

    public static boolean isSnow(Block block) {
        final Material material = block.getMaterial();
        return material == Material.snow || material == Material.craftedSnow;
    }

    public static boolean isWater(Block block) {
        return block.getMaterial() == Material.water;
    }
}
