package mods.betterfoliage.client.integration;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockModernLeaves;

public final class EFRIntegration {

    // TODO: ported to Java, but might not be needed after configs refactor
    public static boolean isEFRCherryLeaves(Block block, int meta) {
        return Mod.EFR.isLoaded() && block instanceof BlockModernLeaves && meta % 4 == 1;
    }

    public static boolean isEFRSoulSoil(Block block) {
        return Mod.EFR.isLoaded() && ModBlocks.SOUL_SOIL.isEnabled() && block == ModBlocks.SOUL_SOIL.get();
    }
}
