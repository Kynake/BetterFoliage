package mods.betterfoliage.client.integration

import com.shinoow.abyssalcraft.common.blocks.BlockDLTLog
import com.shinoow.abyssalcraft.common.blocks.BlockDreadLog
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import ic2.core.block.BlockRubWood
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.config.SimpleBlockMatcher
import mods.octarinecore.client.render.Axis
import net.minecraft.block.Block
import org.apache.logging.log4j.Level

/** Integration for AbyssalCraft */
@SideOnly(Side.CLIENT)
object AbyssalCraftIntegration {

    val abyssalLogs by lazy {
        object : SimpleBlockMatcher() {
            override fun matchesClass(block: Block) = (
                block.javaClass.name.equals(BlockDLTLog::class.java.name) ||
                    block.javaClass.name.equals(BlockDreadLog::class.java.name)
                ) && Config.blocks.logs.matchesClass(block)
        }
    }

    init {
        if (Mod.ABYSSALCRAFT.isLoaded) {
            Client.log(Level.INFO, "${Mod.ABYSSALCRAFT.modName} found - setting up compatibility")

            val originalFunc = Client.logRenderer.axisFunc

            // patch axis detection for log blocks to support AbyssalCraft logs
            Client.logRenderer.axisFunc = { block: Block, meta: Int ->
                if (abyssalLogs.matchesID(block)) Axis.Y else originalFunc(block, meta)
            }
        }
    }
}
