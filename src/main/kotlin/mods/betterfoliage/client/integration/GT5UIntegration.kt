package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.common.pollution.Pollution
import gregtech.common.pollution.PollutionConfig
import net.minecraft.block.Block

@SideOnly(Side.CLIENT)
object GT5UIntegration {
    private const val STANDARD_TYPE = 0
    private const val CROSSED_SQUARES_TYPE = 1
    private const val LIQUID_TYPE = 4
    private const val VINE_TYPE = 20
    private const val DOUBLE_PLANT_TYPE = 40

    @JvmStatic fun tryTintWithPollution(originalColor: Int, block: Block, x: Int, z: Int): Int {
        if (!(Mod.GT5U.isLoaded && PollutionConfig.pollution)) return originalColor
        return when (block.renderType) {
            STANDARD_TYPE -> Pollution.standardBlocks
            CROSSED_SQUARES_TYPE -> Pollution.crossedSquares
            LIQUID_TYPE -> Pollution.liquidBlocks
            VINE_TYPE -> Pollution.blockVine
            DOUBLE_PLANT_TYPE -> Pollution.doublePlants
            else -> return originalColor
        }?.matchesID(block)?.getColor(originalColor, x, z) ?: originalColor
    }
}
