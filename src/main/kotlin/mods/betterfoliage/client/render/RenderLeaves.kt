package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.integration.NaturaIntegration
import mods.betterfoliage.client.integration.TinkersIntegration
import mods.betterfoliage.client.texture.LeafInfo
import mods.betterfoliage.client.texture.LeafRegistry
import mods.octarinecore.PI2
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Double3
import mods.octarinecore.client.render.FlatOffset
import mods.octarinecore.client.render.Int3
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.alwaysRender
import mods.octarinecore.client.render.cornerAoMaxGreen
import mods.octarinecore.client.render.edgeOrientedAuto
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.noPost
import mods.octarinecore.client.render.vec
import mods.octarinecore.random
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection.DOWN
import net.minecraftforge.common.util.ForgeDirection.UP
import kotlin.math.cos
import kotlin.math.sin

class RenderLeaves : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val leavesModel = model {
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yBottom = -0.5 * 1.41, yTop = 0.5 * 1.41)
            .setAoShader(edgeOrientedAuto(corner = cornerAoMaxGreen))
            .setFlatShader(FlatOffset(Int3.zero))
            .scale(Config.leaves.size)
            .toCross(UP)
            .addAll()
    }
    val snowedIcon = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_leaves_snowed_%d")

    val perturbs =
        vectorSet(64) { idx ->
            val angle = PI2 * idx / 64.0
            Double3(cos(angle), 0.0, sin(angle)) * Config.leaves.hOffset +
                UP.vec * random(-1.0, 1.0) * Config.leaves.vOffset
        }

    override fun isEligible(ctx: BlockContext) = Config.enabled &&
        Config.leaves.enabled &&
        ctx.cameraDistance < Config.leaves.distance &&
        Config.blocks.leaves.matchesID(ctx.block)

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        val isSnowed = ctx.block(up1).material.let { it == Material.snow || it == Material.craftedSnow }

        if (renderWorldBlockBase(parent, face = alwaysRender)) return true

        val leafInfo = LeafRegistry.leaves[ctx.icon(DOWN)]
        if (leafInfo != null) {
            val rand = ctx.semiRandomArray(2)
            val rotations = if (Config.leaves.dense) denseLeavesRot else normalLeavesRot

            if (!renderSpecialCaseLeaves(ctx, leafInfo, rotations, rand)) {
                rotations.forEach { rotation ->
                    modelRenderer.render(
                        leavesModel.model,
                        rotation,
                        ctx.blockCenter + perturbs[rand[0]],
                        icon = { _, _, _ -> leafInfo.roundLeafTexture },
                        rotateUV = { rand[1] },
                        postProcess = noPost,
                    )
                }
            }
            if (isSnowed && Config.leaves.snowEnabled) {
                modelRenderer.render(
                    leavesModel.model,
                    Rotation.identity,
                    ctx.blockCenter + perturbs[rand[0]],
                    icon = { _, _, _ -> snowedIcon[rand[1]]!! },
                    rotateUV = { 0 },
                    postProcess = whitewash,
                )
            }
        }

        return true
    }

    private fun renderSpecialCaseLeaves(ctx: BlockContext, leafInfo: LeafInfo, rotations: Array<Rotation>, rand: Array<Int>): Boolean {
        val modelData =
            if (NaturaIntegration.isBerryBush(ctx.block)) {
                NaturaIntegration.getBerryBushData(ctx)
            } else {
                return false
            }

        val position = modelData.center + (perturbs[rand[0]] * modelData.scale)
        rotations.forEach { rotation ->
            modelRenderer.render(
                modelData.model,
                rotation,
                trans = position,
                icon = { _, _, _ -> leafInfo.roundLeafTexture },
                rotateUV = { rand[1] },
                postProcess = noPost,
            )
        }
        return true
    }
}
