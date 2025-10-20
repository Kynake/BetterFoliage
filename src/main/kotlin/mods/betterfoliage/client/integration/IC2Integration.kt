package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import ic2.core.block.BlockRubWood
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.config.SimpleBlockMatcher
import mods.octarinecore.client.render.Axis
import net.minecraft.block.Block
import org.apache.logging.log4j.Level

/** Integration for IC2 experimental */
@SideOnly(Side.CLIENT)
object IC2Integration {

    val ic2Logs by lazy {
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) =
                block.javaClass.name.equals(BlockRubWood::class.java.name) &&
                    Config.blocks.logs.matchesClass(block)

        }
    }

    init {
        if (CompatibleMod.IC2.isModLoaded()) {
            Client.log(Level.INFO, "${CompatibleMod.IC2.modName} found - setting up compatibility")

            val originalFunc = Client.logRenderer.axisFunc

            // patch axis detection for log blocks to support IC2 logs
            Client.logRenderer.axisFunc = { block: Block, meta: Int ->
                if (ic2Logs.matchesID(block)) Axis.Y else originalFunc(block, meta)
            }
        }
    }
}
