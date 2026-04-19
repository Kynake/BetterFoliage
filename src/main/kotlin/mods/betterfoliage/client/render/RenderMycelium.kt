package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.Axis
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Double3
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
import net.minecraft.init.Blocks
import net.minecraftforge.common.util.ForgeDirection.UP
import org.apache.logging.log4j.Level.INFO

class RenderMycelium : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    // Based on the old short grass crossed square renderer
    companion object {
        @JvmStatic
        val myceliumTopQuads: Model.(Int) -> Unit = { modelIdx ->
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

    val myceliumIcon = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_mycel_%d")
    val myceliumModel = modelSet(64, myceliumTopQuads)

    override fun afterStitch() {
        Client.log(INFO, "Registered ${myceliumIcon.num} mycelium textures")
    }

    override fun isEligible(ctx: BlockContext): Boolean {
        if (!Config.enabled || !Config.shortGrass.myceliumEnabled) return false
        return ctx.block == Blocks.mycelium && ctx.cameraDistance < Config.shortGrass.distance
    }

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        val isSnowed = ctx.block(up1).material.let { it == Material.snow || it == Material.craftedSnow }

        if (renderWorldBlockBase(parent, face = alwaysRender)) return true
        if (isSnowed && !Config.shortGrass.snowEnabled) return true
        if (ctx.block(up1).isOpaqueCube) return true

        val rand = ctx.semiRandomArray(2)
        modelRenderer.render(
            myceliumModel[rand[0]],
            Rotation.identity,
            ctx.blockCenter + (if (isSnowed) snowOffset else Double3.zero),
            icon = { _, qi, _ -> myceliumIcon[rand[qi and 1]]!! },
            rotateUV = { 0 },
            postProcess = if (isSnowed) whitewash else noPost,
        )

        return true
    }
}
