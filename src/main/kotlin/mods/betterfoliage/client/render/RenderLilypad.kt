package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.FlatOffsetNoColor
import mods.octarinecore.client.render.Int3
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.alwaysRender
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.noPost
import mods.octarinecore.client.render.renderBlocks
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection
import org.apache.logging.log4j.Level

class RenderLilypad : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val lilypadHeight = 0.015625

    val rootModel = model {
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yBottom = -1.5, yTop = -0.5)
            .move(lilypadHeight to ForgeDirection.UP)
            .setFlatShader(FlatOffsetNoColor(Int3.zero))
            .toCross(ForgeDirection.UP)
            .addAll()
    }
    val flowerModel = model {
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yBottom = 0.0, yTop = 1.0)
            .scale(0.5)
            .move(0.5 - lilypadHeight to ForgeDirection.DOWN)
            .setFlatShader(FlatOffsetNoColor(Int3.zero))
            .toCross(ForgeDirection.UP)
            .addAll()
    }
    val rootIcon = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_lilypad_roots_%d")
    val flowerIcon = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_lilypad_flower_%d")
    val perturbs = vectorSet(64) { modelIdx -> xzDisk(modelIdx) * Config.lilypad.hOffset }

    override fun afterStitch() {
        Client.log(Level.INFO, "Registered ${rootIcon.num} lilypad root textures")
        Client.log(Level.INFO, "Registered ${flowerIcon.num} lilypad flower textures")
    }

    override fun isEligible(ctx: BlockContext): Boolean = Config.enabled &&
        Config.lilypad.enabled &&
        ctx.cameraDistance < Config.lilypad.distance &&
        Config.blocks.lilypad.matchesID(ctx.block)

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        // Render base block and capture AO data
        if (renderWorldBlockBase(
                parentRenderer = parent,
                face = alwaysRender,
                block = { renderBlocks.renderBlockLilyPad(ctx.block, ctx.x, ctx.y, ctx.z) },
            )
        ) {
            return true
        }

        val rand = ctx.semiRandomArray(5)
        modelRenderer.render(
            rootModel.model,
            Rotation.identity,
            ctx.blockCenter.add(perturbs[rand[2]]),
            forceFlat = true,
            icon = { ctx, qi, q -> rootIcon[rand[qi and 1]]!! },
            rotateUV = { 0 },
            postProcess = noPost,
        )

        if (rand[3] < Config.lilypad.flowerChance) {
            modelRenderer.render(
                flowerModel.model,
                Rotation.identity,
                ctx.blockCenter.add(perturbs[rand[4]]),
                forceFlat = true,
                icon = { _, _, _ -> flowerIcon[rand[0]]!! },
                rotateUV = { 0 },
                postProcess = noPost,
            )
        }

        return true
    }
}
