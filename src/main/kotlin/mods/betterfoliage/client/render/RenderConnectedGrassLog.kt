package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Int3
import mods.octarinecore.client.render.alwaysRender
import mods.octarinecore.client.render.offset
import mods.octarinecore.client.render.withOffset
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection

class RenderConnectedGrassLog : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val grassCheckDirs = listOf(ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH)

    override fun isEligible(ctx: BlockContext) = Config.enabled &&
        Config.roundLogs.enabled &&
        Config.roundLogs.connectGrass &&
        Config.blocks.dirt.matchesID(ctx.block) &&
        Config.blocks.logs.matchesID(ctx.block(up1))

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        val grassDir = grassCheckDirs.find { Config.blocks.grass.matchesID(ctx.block(it.offset)) }

        return if (grassDir != null) {
            ctx.withOffset(Int3.zero, grassDir.offset) {
                renderWorldBlockBase(parent, face = alwaysRender)
            }
        } else {
            renderWorldBlockBase(parent, face = alwaysRender)
        }
    }
}
