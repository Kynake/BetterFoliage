package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.integration.TFCIntegration
import mods.betterfoliage.client.texture.GrassRegistry
import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.Axis
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Double3
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.Quad
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.ShadingContext
import mods.octarinecore.client.render.applyContextToRender
import mods.octarinecore.client.render.cornerAo
import mods.octarinecore.client.render.cornerFlat
import mods.octarinecore.client.render.faceOrientedAuto
import mods.octarinecore.client.render.forgeDirs
import mods.octarinecore.client.render.fullCube
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.neverRender
import mods.octarinecore.client.render.noPost
import mods.octarinecore.client.render.offset
import mods.octarinecore.random
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection.UP
import org.apache.logging.log4j.Level.INFO

class RenderGrass : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    companion object {
        @JvmStatic
        val grassTopQuads: Model.(Int) -> Unit = { modelIdx ->
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

    val normalIcons = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_grass_long_%d")
    val snowedIcons = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "better_grass_snowed_%d")
    val normalGenIcon =
        iconStatic(Client.genGrass.generatedResource("minecraft:tallgrass", "snowed" to false))
    val snowedGenIcon =
        iconStatic(Client.genGrass.generatedResource("minecraft:tallgrass", "snowed" to true))

    val grassModels = modelSet(64, grassTopQuads)

    override fun afterStitch() {
        Client.log(INFO, "Registered ${normalIcons.num} grass textures")
        Client.log(INFO, "Registered ${snowedIcons.num} snowed grass textures")
    }

    override fun isEligible(ctx: BlockContext) = Config.enabled &&
        ctx.cameraDistance < Config.shortGrass.distance &&
        (Config.shortGrass.grassEnabled || Config.connectedGrass.enabled) &&
        Config.blocks.grass.matchesID(ctx.block)

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        val grassInfo = GrassRegistry.grass[ctx.icon(UP)]
        if (grassInfo == null) {
            renderWorldBlockBase(parent, face = applyContextToRender(ctx))
            return true
        }
        val isConnected =
            ctx.block(down1).let {
                Config.blocks.dirt.matchesID(it) || Config.blocks.grass.matchesID(it)
            }
        val isSnowed = ctx.block(up1).isSnow
        val connectedGrass =
            isConnected &&
                Config.connectedGrass.enabled &&
                (!isSnowed || Config.connectedGrass.snowEnabled) &&
                allowConnectedGrassSpecialCases(ctx)

        val cubeTexture = if (isSnowed) ctx.icon(UP, up1) else grassInfo.grassTopTexture
        val blockColor = ctx.blockColor

        if (connectedGrass) {
            // get AO data
            if (renderWorldBlockBase(parent, face = neverRender)) return true

            val isVisible = forgeDirs.map { ctx.shouldRenderSide(it.offset, it) }

            // render full grass block
            modelRenderer.render(
                model = fullCube,
                quadFilter = { qi, _ -> isVisible[qi] },
                icon = { _, _, _ -> cubeTexture },
                rotateUV = { 2 },
                postProcess = { ctx, qi, _, _, _ ->
                    if (isSnowed) {
                        if (!ctx.aoEnabled) setGrey(1.4f)
                    } else if (qi != UP.ordinal && ctx.aoEnabled) {
                        multiplyColor(blockColor)
                    }
                },
            )
        } else {
            // render normally
            if (renderWorldBlockBase(parent, face = applyContextToRender(ctx))) return true
        }

        if (!Config.shortGrass.grassEnabled) return true
        if (isSnowed && !Config.shortGrass.snowEnabled) return true
        if (ctx.block(up1).isOpaqueCube) return true

        // render grass quads
        val iconset = if (isSnowed) snowedIcons else normalIcons
        val iconGen = if (isSnowed) snowedGenIcon else normalGenIcon
        val rand = ctx.semiRandomArray(2)

        modelRenderer.render(
            grassModels[rand[0]],
            Rotation.identity,
            ctx.blockCenter + (if (isSnowed) snowOffset else Double3.zero),
            icon =
            if (Config.shortGrass.useGenerated) {
                { ctx: ShadingContext, qi: Int, q: Quad -> iconGen.icon!! }
            } else {
                { ctx: ShadingContext, qi: Int, q: Quad -> iconset[rand[qi and 1]]!! }
            },
            rotateUV = { 0 },
            postProcess =
            if (isSnowed) {
                whitewash
            } else if (grassInfo.overrideColor != null) {
                { _, _, _, _, _ -> multiplyColor(grassInfo.overrideColor) }
            } else if (TFCIntegration.grass.matchesID(ctx.block)) {
                { _, _, _, _, _ -> multiplyColor(blockColor) }
            } else {
                noPost
            },
        )

        return true
    }

    private fun allowConnectedGrassSpecialCases(ctx: BlockContext) = !TFCIntegration.isTFCDirtOrGrass(ctx.block)
}
