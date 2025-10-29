package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import ganymedes01.etfuturum.blocks.BlockModernLeaves
import net.minecraft.block.Block

@SideOnly(Side.CLIENT)
object EFRIntegration {
    @JvmStatic fun isETFCherryLeaves(block: Block, meta: Int) = Mod.EFR.isLoaded && block is BlockModernLeaves && meta % 4 == 1
}
