package mods.betterfoliage.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockUtils {

    public static boolean isSnow(Block block) {
        final Material material = block.getMaterial();
        return material == Material.snow || material == Material.craftedSnow;
    }
}
