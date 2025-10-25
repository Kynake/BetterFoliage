@file:JvmName("Utils")

package mods.betterfoliage.client.render

import mods.octarinecore.PI2
import mods.octarinecore.client.render.Double3
import mods.octarinecore.client.render.Int3
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.Quad
import mods.octarinecore.client.render.RenderVertex
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.ShadingContext
import mods.octarinecore.client.render.Vertex
import mods.octarinecore.client.render.times
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.common.util.ForgeDirection

val up1 = Int3(1 to ForgeDirection.UP)
val up2 = Int3(2 to ForgeDirection.UP)
val down1 = Int3(1 to ForgeDirection.DOWN)
val snowOffset = ForgeDirection.UP * 0.0625

val normalLeavesRot = arrayOf(Rotation.identity)
val denseLeavesRot =
    arrayOf(Rotation.identity, Rotation.rot90[ForgeDirection.EAST.ordinal], Rotation.rot90[ForgeDirection.SOUTH.ordinal])

val whitewash: RenderVertex.(ShadingContext, Int, Quad, Int, Vertex) -> Unit =
    { ctx, qi, q, vi, v ->
        setGrey(1.4f)
    }
val greywash: RenderVertex.(ShadingContext, Int, Quad, Int, Vertex) -> Unit = { ctx, qi, q, vi, v ->
    setGrey(1.0f)
}

val Block.isSnow: Boolean
    get() = material.let { it == Material.snow || it == Material.craftedSnow }

fun Quad.toCross(rotAxis: ForgeDirection, trans: (Quad) -> Quad) = (0..3).map { rotIdx ->
    trans(rotate(Rotation.rot90[rotAxis.ordinal] * rotIdx).mirrorUV(rotIdx > 1, false))
}

fun Quad.toCross(rotAxis: ForgeDirection) = toCross(rotAxis) { it }

/** Extensions of [AxisAlignedBB] */

/** Get the center of the AABB */
val AxisAlignedBB.center: Double3
    get() = Double3(
        (minX + maxX) / 2.0,
        (minY + maxY) / 2.0,
        (minZ + maxZ) / 2.0,
    )

/** Get the scale for each axis of the AABB. Assumes a normal block is the default scale of (1, 1, 1) */
val AxisAlignedBB.scale: Double3
    get() = Double3(
        maxX - minX,
        maxY - minY,
        maxZ - minZ,
    )

fun xzDisk(modelIdx: Int) = (PI2 * modelIdx / 64.0).let { Double3(Math.cos(it), 0.0, Math.sin(it)) }

val rotationFromUp =
    arrayOf(
        Rotation.rot90[ForgeDirection.EAST.ordinal] * 2,
        Rotation.identity,
        Rotation.rot90[ForgeDirection.WEST.ordinal],
        Rotation.rot90[ForgeDirection.EAST.ordinal],
        Rotation.rot90[ForgeDirection.SOUTH.ordinal],
        Rotation.rot90[ForgeDirection.NORTH.ordinal],
    )

fun Model.mix(first: Model, second: Model, predicate: (Int) -> Boolean) {
    first.quads.forEachIndexed { qi, quad ->
        val otherQuad = second.quads[qi]
        Quad(
            if (predicate(0)) otherQuad.v1.copy() else quad.v1.copy(),
            if (predicate(1)) otherQuad.v2.copy() else quad.v2.copy(),
            if (predicate(2)) otherQuad.v3.copy() else quad.v3.copy(),
            if (predicate(3)) otherQuad.v4.copy() else quad.v4.copy(),
        )
            .add()
    }
}
