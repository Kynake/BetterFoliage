package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.Client
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.ModelData
import mods.octarinecore.client.render.center
import mods.octarinecore.client.render.scale
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import tconstruct.world.blocks.OreberryBush
import kotlin.math.min

@SideOnly(Side.CLIENT)
object TinkersIntegration {
    private const val BUSH_GROWTH_STAGES = 3
    private const val BUSH_METAS_PER_STAGE = 4

    @JvmStatic val tinkersLeavesModel: Array<Model?> by lazy { arrayOfNulls(BUSH_GROWTH_STAGES - 1) }

    fun isTinkersBush(block: Block) = Mod.TCON.isLoaded && block is OreberryBush

    // TODO: join this with the method in [NaturaIntegration]
    fun getTinkersBushData(ctx: BlockContext): ModelData {
        val world = Minecraft.getMinecraft().theWorld
        val aabb = ctx.block.getCollisionBoundingBoxFromPool(world, ctx.x, ctx.y, ctx.z)

        val stage = getBushGrowth(ctx)
        val scale = aabb.scale

        // Tinkers bush is fully grown, use normal leaf model
        if (stage == BUSH_GROWTH_STAGES - 1) {
            return ModelData(scale, aabb.center, Client.leafRenderer.leavesModel.model)
        }

        var model = tinkersLeavesModel[stage]
        if (model == null) {
            // On first render: Clone default leaf model and adjust scale to fit this bush's growth stage
            model = Client.leafRenderer.leavesModel.model.clone()
            for (i in model.quads.indices) {
                model.quads[i] = model.quads[i].scale(scale)
            }
            tinkersLeavesModel[stage] = model
        }

        return ModelData(scale, aabb.center, model)
    }

    private fun getBushGrowth(ctx: BlockContext) = min(ctx.meta / BUSH_METAS_PER_STAGE, BUSH_GROWTH_STAGES - 1)
}
