package mods.betterfoliage.client

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.integration.AbyssalCraftIntegration
import mods.betterfoliage.client.integration.IC2Integration
import mods.betterfoliage.client.integration.TFCIntegration
import mods.betterfoliage.client.render.LeafWindTracker
import mods.betterfoliage.client.render.RenderCactus
import mods.betterfoliage.client.render.RenderLeaves
import mods.betterfoliage.client.render.RenderLog
import mods.betterfoliage.client.render.RisingSoulTextures
import mods.betterfoliage.client.texture.GrassRegistry
import mods.betterfoliage.client.texture.LeafGenerator
import mods.betterfoliage.client.texture.LeafRegistry
import mods.octarinecore.client.resource.GeneratorPack
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

    val genLeaves = LeafGenerator("bf_gen_leaves") // TODO revise extra leaves masks. Any texture other than _default is never used

    @Suppress("unused")
    val generatorPack = GeneratorPack("Better Foliage generated", genLeaves)

    val logRenderer = RenderLog()

    val leafRenderer = RenderLeaves()

    val renderers =
        listOf(
            leafRenderer,
            RenderCactus(),
            logRenderer,
        )

    @Suppress("unused")
    val singletons =
        listOf(
            LeafRegistry,
            GrassRegistry,
            LeafWindTracker,
            RisingSoulTextures,
            TFCIntegration,
            IC2Integration,
            AbyssalCraftIntegration,
        )

    fun log(level: Level, msg: String) = BetterFoliageMod.log!!.log(level, msg)
}
