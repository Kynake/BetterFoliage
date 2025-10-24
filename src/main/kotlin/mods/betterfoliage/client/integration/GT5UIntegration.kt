package mods.betterfoliage.client.integration

import gregtech.common.pollution.Pollution
import net.minecraft.block.Block

object GT5UIntegration {
    private const val STANDARD_TYPE = 0
    private const val CROSSED_SQUARES_TYPE = 1
    private const val LIQUID_TYPE = 4
    private const val VINE_TYPE = 20
    private const val DOUBLE_PLANT_TYPE = 40

    @JvmStatic fun tryTintWithPollution(originalColor: Int, block: Block, x: Int, z: Int): Int {
        if (!Mod.GT5U.isLoaded) return originalColor
        return when (block.renderType) {
            STANDARD_TYPE -> Pollution.standardBlocks.matchesID(block)
            CROSSED_SQUARES_TYPE -> Pollution.crossedSquares.matchesID(block)
            LIQUID_TYPE -> Pollution.liquidBlocks.matchesID(block)
            VINE_TYPE -> Pollution.blockVine.matchesID(block)
            DOUBLE_PLANT_TYPE -> Pollution.doublePlants.matchesID(block)
            else -> return originalColor
        }?.getColor(originalColor, x, z) ?: originalColor
    }
}
