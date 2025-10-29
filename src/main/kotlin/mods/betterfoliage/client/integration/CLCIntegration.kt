package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.Client
import mods.betterfoliage.loader.Refs
import mods.octarinecore.client.render.brightnessComponents
import org.apache.logging.log4j.Level

/** Integration for Colored Lights Core. */
@SideOnly(Side.CLIENT)
object CLCIntegration {

    init {
        if (Refs.CLCLoadingPlugin.element != null) {
            Client.log(Level.INFO, "Colored Lights Core integration enabled")
            brightnessComponents = listOf(4, 8, 12, 16, 20)
        }
    }
}
