package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.Axis
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.Vertex
import mods.octarinecore.client.render.cornerAo
import mods.octarinecore.client.render.cornerAoMaxGreen
import mods.octarinecore.client.render.edgeOrientedAuto
import mods.octarinecore.client.render.faceOrientedAuto
import mods.octarinecore.client.render.forgeDirs
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.neverRender
import mods.octarinecore.client.render.noPost
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection
import org.apache.logging.log4j.Level

class RenderCactus : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val cactusBlockExtents = 0.4375 // 0.4375 = 0.5 * 14/16, to account for cacti being 2 pixels thinner
    val cactusArmRotation = listOf(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST).map { Rotation.rot90[it.ordinal] }

    val iconCross = iconStatic(BetterFoliageMod.LEGACY_DOMAIN, "better_cactus")
    val iconArm = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_cactus_arm_%d")

    // Why do we have to recreate the cactus block here?
    // TODO consider removing this and using the base render, if possible
    // Add an explanation comment otherwise
    val modelBase = model {
        horizontalRectangle(
            x1 = -cactusBlockExtents,
            x2 = cactusBlockExtents,
            z1 = -cactusBlockExtents,
            z2 = cactusBlockExtents,
            y = 0.5,
        )
            .scaleUV(cactusBlockExtents * 2.0)
            .let { listOf(it.flipped.move(1.0 to ForgeDirection.DOWN), it) }
            .forEach { it.setAoShader(faceOrientedAuto(corner = cornerAo(Axis.Y), edge = null)).add() }

        verticalRectangle(
            x1 = -0.5,
            z1 = cactusBlockExtents,
            x2 = 0.5,
            z2 = cactusBlockExtents,
            yBottom = -0.5,
            yTop = 0.5,
        )
            .setAoShader(faceOrientedAuto(corner = cornerAo(Axis.Y), edge = null))
            .toCross(ForgeDirection.UP)
            .addAll()
    }

    val modelCross = modelSet(64) { modelIdx ->
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yBottom = -0.5 * 1.41, yTop = 0.5 * 1.41)
            .setAoShader(edgeOrientedAuto(corner = cornerAoMaxGreen))
            .scale(1.4)
            .transformV { v ->
                val perturb = xzDisk(modelIdx) * Config.cactus.sizeVariation
                Vertex(v.xyz + (if (v.uv.u < 0.0) perturb else -perturb), v.uv, v.aoShader)
            }
            .toCross(ForgeDirection.UP)
            .addAll()
    }

    val modelArm = modelSet(64) { modelIdx ->
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yBottom = 0.0, yTop = 1.0)
            .scale(Config.cactus.size)
            .move(cactusBlockExtents to ForgeDirection.UP)
            .setAoShader(faceOrientedAuto(overrideFace = ForgeDirection.UP, corner = cornerAo(Axis.Y), edge = null))
            .toCross(ForgeDirection.UP) { it.move(xzDisk(modelIdx) * Config.cactus.hOffset) }
            .addAll()
    }

    override fun afterStitch() {
        Client.log(Level.INFO, "Registered ${iconArm.num} cactus arm textures")
    }

    override fun isEligible(ctx: BlockContext) = Config.enabled &&
        (Config.cactus.stem || Config.cactus.arms) &&
        ctx.cameraDistance < Config.cactus.distance &&
        Config.blocks.cactus.matchesID(ctx.block)

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        // get AO data
        if (renderWorldBlockBase(parent, face = neverRender)) return true

        modelRenderer.render(
            modelBase.model,
            Rotation.identity,
            icon = { ctx, qi, _ -> ctx.icon(forgeDirs[qi]) },
            rotateUV = { 0 },
            postProcess = noPost,
        )

        if (Config.cactus.stem) {
            modelRenderer.render(
                modelCross[ctx.random(0)],
                Rotation.identity,
                icon = { _, _, _ -> iconCross.icon!! },
                rotateUV = { 0 },
                postProcess = noPost,
            )
        }

        if (Config.cactus.arms) {
            cactusArmRotation.forEachIndexed { idx, rotation ->
                if (ctx.random(idx + 3) < Config.cactus.armChance) {
                    modelRenderer.render(
                        model = modelArm[ctx.random(1)],
                        rot = rotation,
                        icon = { _, _, _ -> iconArm[ctx.random(2)]!! },
                        rotateUV = { 0 },
                        postProcess = noPost,
                    )
                }
            }
        }

        return true
    }
}
