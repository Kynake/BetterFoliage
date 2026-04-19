package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.Axis
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.alwaysRender
import mods.octarinecore.client.render.cornerAo
import mods.octarinecore.client.render.cornerFlat
import mods.octarinecore.client.render.faceOrientedAuto
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.noPost
import mods.octarinecore.random
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection.UP
import org.apache.logging.log4j.Level.INFO

class RenderAlgae : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    // Based on the old short grass crossed square renderer
    companion object {
        @JvmStatic
        val algaeTopQuads: Model.(Int) -> Unit = { modelIdx ->
            verticalRectangle(
                x1 = -0.5,
                z1 = 0.5,
                x2 = 0.5,
                z2 = -0.5,
                yBottom = 0.5,
                yTop = 0.5 + random(Config.shortGrass.heightMin, Config.shortGrass.heightMax),
            )
                .setAoShader(faceOrientedAuto(overrideFace = UP, corner = cornerAo(Axis.Y)))
                .setFlatShader(faceOrientedAuto(overrideFace = UP, corner = cornerFlat))
                .toCross(UP) { it.move(xzDisk(modelIdx) * Config.shortGrass.hOffset) }
                .addAll()
        }
    }

    val noise = simplexNoise()

    val algaeIcons = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_algae_%d")
    val algaeModels = modelSet(64, algaeTopQuads)

    override fun afterStitch() {
        Client.log(INFO, "Registered ${algaeIcons.num} algae textures")
    }

    override fun isEligible(ctx: BlockContext) = Config.enabled &&
        Config.algae.enabled &&
        ctx.cameraDistance < Config.algae.distance &&
        ctx.block(up2).material == Material.water &&
        ctx.block(up1).material == Material.water &&
        Config.blocks.dirt.matchesID(ctx.block) &&
        ctx.biomeId in Config.algae.biomes &&
        noise[ctx.x, ctx.z] < Config.algae.population

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        if (renderWorldBlockBase(parent, face = alwaysRender)) return true

        val rand = ctx.semiRandomArray(3)

        modelRenderer.render(
            algaeModels[rand[2]],
            Rotation.identity,
            icon = { _, qi, _ -> algaeIcons[rand[qi and 1]]!! },
            rotateUV = { 0 },
            postProcess = noPost,
        )

        return true
    }
}
