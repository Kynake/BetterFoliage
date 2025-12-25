package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import lotr.common.block.LOTRBlockLeaves
import lotr.common.block.LOTRBlockLeaves7
import net.minecraft.block.Block

/** Integration for The Lord of the Rings Mod: Legacy */
@SideOnly(Side.CLIENT)
object LOTRIntegration {
    @JvmStatic fun isLOTRLeafWithVFX(block: Block, meta: Int) = Mod.LOTR.isLoaded && ((meta != 0 && block is LOTRBlockLeaves) || (meta and 3 == 1 && block is LOTRBlockLeaves7))
}
