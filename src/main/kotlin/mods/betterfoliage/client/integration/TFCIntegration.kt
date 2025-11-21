package mods.betterfoliage.client.integration

import com.bioxx.tfc.Blocks.Terrain.BlockDirt
import com.bioxx.tfc.Blocks.Terrain.BlockGrass
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

    @JvmStatic fun isTFCDirtOrGrass(block: Block) = Mod.TFC.isLoaded && (block is BlockGrass || block is BlockDirt)

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

    val grass =
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) = Mod.TFC.isLoaded && block is BlockGrass && Config.blocks.grass.matchesClass(block)
        }

    init {
        if (Mod.TFC.isLoaded) {
            Client.log(Level.INFO, "${Mod.TFC.modName} found - setting up compatibility")

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
