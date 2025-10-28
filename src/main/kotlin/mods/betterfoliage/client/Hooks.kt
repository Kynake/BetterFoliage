// TODO migrate these hooks directly into the corresponding Mixin classes
@file:JvmName("Hooks")
@file:SideOnly(Side.CLIENT)

package mods.betterfoliage.client

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.render.EntityFallingLeavesFX
import mods.betterfoliage.client.render.EntityRisingSoulFX
import mods.octarinecore.client.render.blockContext
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

fun getRenderTypeOverride(
    blockAccess: IBlockAccess,
    x: Int,
    y: Int,
    z: Int,
    original: Int,
): Int {
    if (!Config.enabled) return original
    return blockContext.let { ctx ->
        ctx.set(blockAccess, x, y, z)
        Client.renderers.find { it.isEligible(ctx) }?.renderId ?: original
    }
}

/** Should this block be Non-solid when the game otherwise considers it to be?
 *  Used by rounded logs */
fun overrideIsPartialBlock(
    originalIsPartial: Boolean,
    blockAccess: IBlockAccess,
    x: Int,
    y: Int,
    z: Int,
): Boolean = originalIsPartial || (
    Config.enabled &&
        Config.roundLogs.enabled &&
        Config.blocks.logs.matchesID(blockAccess.getBlock(x, y, z))
    )

fun getAmbientOcclusionLightValueOverride(original: Float, block: Block): Float {
    if (Config.enabled && Config.roundLogs.enabled && Config.blocks.logs.matchesID(block)) {
        return Config.roundLogs.dimming
    }
    return original
}

fun getUseNeighborBrightnessOverride(original: Boolean, block: Block): Boolean = original ||
    (Config.enabled && Config.roundLogs.enabled && Config.blocks.logs.matchesID(block))

fun onRandomDisplayTick(block: Block, world: World, x: Int, y: Int, z: Int) {
    if (!Config.enabled) return
    if (Config.risingSoul.enabled &&
        block == Blocks.soul_sand &&
        world.isAirBlock(x, y + 1, z) &&
        Math.random() < Config.risingSoul.chance
    ) {
        EntityRisingSoulFX(world, x, y, z).addIfValid()
    }

    if (Config.fallingLeaves.enabled &&
        Config.blocks.leaves.matchesID(block) &&
        world.isAirBlock(x, y - 1, z) &&
        Math.random() < Config.fallingLeaves.chance &&
        EntityFallingLeavesFX.checkModSpecialLeafParticles(block, world, x, y, z)
    ) {
        EntityFallingLeavesFX(world, x, y, z).addIfValid()
    }
}
