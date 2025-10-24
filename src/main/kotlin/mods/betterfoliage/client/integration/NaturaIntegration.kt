package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.Client
import mods.natura.blocks.crops.BerryBush
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Double3
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.resource.center
import mods.octarinecore.client.resource.scale
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import kotlin.math.min

@SideOnly(Side.CLIENT)
object NaturaIntegration {

    data class BerryBushData(val scale: Double3, val center: Double3, val model: Model)

    private const val BUSH_GROWTH_STAGES = 3
    private const val BUSH_METAS_PER_STAGE = 4

    @JvmStatic val naturaLeavesModel: Array<Model?> by lazy { arrayOfNulls(BUSH_GROWTH_STAGES - 1) }

    fun isBerryBush(block: Block) = Mod.NATURA.isLoaded && block is BerryBush

    fun BerryBush.getBerryBushData(ctx: BlockContext): BerryBushData {
        val world = Minecraft.getMinecraft().theWorld
        val aabb = getCollisionBoundingBoxFromPool(world, ctx.x, ctx.y, ctx.z)

        val stage = getBushGrowth(ctx)
        val scale = aabb.scale

        // Berry bush is fully grown, use normal leaf model
        if (stage == BUSH_GROWTH_STAGES - 1) {
            return BerryBushData(scale, aabb.center, Client.leafRenderer.leavesModel.model)
        }

        var model = naturaLeavesModel[stage]
        if (model == null) {
            // On first render: Clone default leaf model and adjust scale to fit this bush's growth stage
            model = Client.leafRenderer.leavesModel.model.clone()
            for (i in model.quads.indices) {
                model.quads[i] = model.quads[i].scale(scale)
            }
            naturaLeavesModel[stage] = model
        }

        return BerryBushData(scale, aabb.center, model)
    }

    private fun getBushGrowth(ctx: BlockContext) = min(ctx.meta / BUSH_METAS_PER_STAGE, BUSH_GROWTH_STAGES - 1)
}
