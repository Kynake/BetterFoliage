package mods.betterfoliage.client

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.integration.AbyssalCraftIntegration
import mods.betterfoliage.client.integration.IC2Integration
import mods.betterfoliage.client.render.RenderLog
import mods.betterfoliage.client.texture.GrassRegistry
import org.apache.logging.log4j.Level

/**
 * Object responsible for initializing (and holding a reference to) all the infrastructure of the
 * mod except for the call hooks.
 *
 * This and all other singletons are annotated [SideOnly] to avoid someone accidentally partially
 * initializing the mod on a server environment.
 */
@SideOnly(Side.CLIENT)
object Client {

    @Suppress("unused")
    val logRenderer = RenderLog()

    val renderers =
        listOf(
            logRenderer,
        )

    @Suppress("unused")
    val singletons =
        listOf(
            IC2Integration,
            AbyssalCraftIntegration,
        )

    fun log(level: Level, msg: String) = BetterFoliageMod.log!!.log(level, msg)
}
