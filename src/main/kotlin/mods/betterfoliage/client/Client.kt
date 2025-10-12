package mods.betterfoliage.client

import cpw.mods.fml.client.FMLClientHandler
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.gui.ConfigGuiFactory
import mods.betterfoliage.client.integration.CLCIntegration
import mods.betterfoliage.client.integration.IC2Integration
import mods.betterfoliage.client.integration.ShadersModIntegration
import mods.betterfoliage.client.integration.TFCIntegration
import mods.betterfoliage.client.render.LeafWindTracker
import mods.betterfoliage.client.render.RenderAlgae
import mods.betterfoliage.client.render.RenderCactus
import mods.betterfoliage.client.render.RenderConnectedGrass
import mods.betterfoliage.client.render.RenderConnectedGrassLog
import mods.betterfoliage.client.render.RenderCoral
import mods.betterfoliage.client.render.RenderGrass
import mods.betterfoliage.client.render.RenderLeaves
import mods.betterfoliage.client.render.RenderLilypad
import mods.betterfoliage.client.render.RenderLog
import mods.betterfoliage.client.render.RenderMycelium
import mods.betterfoliage.client.render.RenderNetherrack
import mods.betterfoliage.client.render.RenderReeds
import mods.betterfoliage.client.render.RisingSoulTextures
import mods.betterfoliage.client.texture.GrassGenerator
import mods.betterfoliage.client.texture.GrassRegistry
import mods.betterfoliage.client.texture.LeafGenerator
import mods.betterfoliage.client.texture.LeafRegistry
import mods.octarinecore.client.KeyHandler
import mods.octarinecore.client.resource.CenteringTextureGenerator
import mods.octarinecore.client.resource.GeneratorPack
import net.minecraft.client.Minecraft
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

    val configKey =
        KeyHandler(BetterFoliageMod.MOD_NAME, 66, "key.betterfoliage.gui") {
            FMLClientHandler.instance()
                .showGuiScreen(ConfigGuiFactory.ConfigGuiBetterFoliage(Minecraft.getMinecraft().currentScreen))
        }

    val genGrass = GrassGenerator("bf_gen_grass")
    val genLeaves = LeafGenerator("bf_gen_leaves")
    val genReeds = CenteringTextureGenerator("bf_gen_reeds", 1, 2)

    val generatorPack = GeneratorPack("Better Foliage generated", genGrass, genLeaves, genReeds)

    val logRenderer = RenderLog()

    val renderers =
        listOf(
            RenderGrass(),
            RenderMycelium(),
            RenderLeaves(),
            RenderCactus(),
            RenderLilypad(),
            RenderReeds(),
            RenderAlgae(),
            RenderCoral(),
            logRenderer,
            RenderNetherrack(),
            RenderConnectedGrass(),
            RenderConnectedGrassLog(),
        )

    val singletons =
        listOf(
            LeafRegistry,
            GrassRegistry,
            LeafWindTracker,
            RisingSoulTextures,
            TFCIntegration,
            ShadersModIntegration,
            CLCIntegration,
            IC2Integration,
        )

    fun log(level: Level, msg: String) = BetterFoliageMod.log!!.log(level, msg)
}
