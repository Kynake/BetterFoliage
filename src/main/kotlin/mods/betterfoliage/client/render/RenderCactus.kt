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
import net.minecraft.block.BlockCactus
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection
import org.apache.logging.log4j.Level

class RenderCactus : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val cactusBlockExtents = 0.4375 // 0.4375 = 0.5 * 14/16, to account for cacti being 2 pixels thinner
    val cactusArmRotation = listOf(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST).map { Rotation.rot90[it.ordinal] }

    val iconCross = iconStatic(BetterFoliageMod.LEGACY_DOMAIN, "better_cactus")
    val iconArm = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_cactus_arm_%d")

    val aoShader = faceOrientedAuto(corner = cornerAo(Axis.Y), edge = null)

    val modelTop = model {
        horizontalRectangle(x1 = -0.5, x2 = 0.5, z1 = -0.5, z2 = 0.5, y = 0.5).setAoShader(aoShader).add()
    }

    val modelBottom = model {
        horizontalRectangle(x1 = -0.5, x2 = 0.5, z1 = -0.5, z2 = 0.5, y = 0.5).flipped.setAoShader(aoShader).add()
    }

    val modelSides = model {
        // In order: North, South, East, West
        verticalRectangle(x1 = 0.5, z1 = -cactusBlockExtents, x2 = -0.5, z2 = -cactusBlockExtents, yBottom = -0.5, yTop = 0.5)
            .setAoShader(aoShader).add()
        verticalRectangle(x1 = -0.5, z1 = cactusBlockExtents, x2 = 0.5, z2 = cactusBlockExtents, yBottom = -0.5, yTop = 0.5)
            .setAoShader(aoShader).add()
        verticalRectangle(x1 = cactusBlockExtents, z1 = 0.5, x2 = cactusBlockExtents, z2 = -0.5, yBottom = -0.5, yTop = 0.5)
            .setAoShader(aoShader).add()
        verticalRectangle(x1 = -cactusBlockExtents, z1 = -0.5, x2 = -cactusBlockExtents, z2 = 0.5, yBottom = -0.5, yTop = 0.5)
            .setAoShader(aoShader).add()
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
        // Dummy render pass to capture AO data
        if (renderWorldBlockBase(parent, face = neverRender)) return true

        // We render the cactus model ourselves because minecraft does not render it
        // with AO by default, which looks out of place when paired with
        // the other cactus additions that _do_ render with AO.
        if (ctx.block(down1) !is BlockCactus &&
            ctx.shouldRenderSide(down1, ForgeDirection.DOWN)
        ) {
            modelRenderer.render(
                modelBottom.model,
                Rotation.identity,
                icon = { ctx, _, _ -> ctx.icon(forgeDirs[0]) },
                rotateUV = { 0 },
                postProcess = noPost,
            )
        }

        if (ctx.block(up1) !is BlockCactus &&
            ctx.shouldRenderSide(up1, ForgeDirection.UP)
        ) {
            modelRenderer.render(
                modelTop.model,
                Rotation.identity,
                icon = { ctx, _, _ -> ctx.icon(forgeDirs[1]) },
                rotateUV = { 0 },
                postProcess = noPost,
            )
        }

        modelRenderer.render(
            modelSides.model,
            Rotation.identity,
            icon = { ctx, qi, _ -> ctx.icon(forgeDirs[qi + 2]) },
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
