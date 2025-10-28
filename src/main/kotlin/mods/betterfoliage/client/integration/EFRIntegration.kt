package mods.betterfoliage.client.integration

import ganymedes01.etfuturum.blocks.BlockModernLeaves
import net.minecraft.block.Block

object EFRIntegration {
    @JvmStatic fun isETFCherryLeaves(block: Block, meta: Int) = Mod.EFR.isLoaded && block is BlockModernLeaves && meta % 4 == 1
}
