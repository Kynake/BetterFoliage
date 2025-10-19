package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.config.SimpleBlockMatcher
import mods.octarinecore.client.render.Axis
import net.minecraft.block.Block
import org.apache.logging.log4j.Level

/** Integration for TerraFirmaCraft */
@SideOnly(Side.CLIENT)
object TFCIntegration {

    val horizontalLogs =
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) = Config.blocks.logs.matchesClass(block) &&
                block.javaClass.name.let { it.startsWith("com.bioxx.tfc") && it.contains("Horiz") }
        }
    val verticalLogs =
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) = Config.blocks.logs.matchesClass(block) &&
                block.javaClass.name.let { it.startsWith("com.bioxx.tfc") && !it.contains("Horiz") }
        }

    // TODO TFC Grass rendering is currently bugged
    val grass =
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) = Config.blocks.grass.matchesClass(block) &&
                block.javaClass.name.startsWith("com.bioxx.tfc")
        }

    init {
        if (CompatibleMod.TFC.isModLoaded()) {
            Client.log(Level.INFO, "${CompatibleMod.TFC.modName} found - setting up compatibility")

            val originalFunc = Client.logRenderer.axisFunc

            // patch axis detection for log blocks to support TFC logs
            Client.logRenderer.axisFunc = { block: Block, meta: Int ->
                if (horizontalLogs.matchesID(block)) {
                    if (meta shr 3 == 0) Axis.Z else Axis.X
                } else if (verticalLogs.matchesID(block)) {
                    Axis.Y
                } else {
                    originalFunc(block, meta)
                }
            }
        }
    }
}
