@file:JvmName("Utils")

package mods.betterfoliage.client.render

import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.Quad
import mods.octarinecore.client.render.Rotation
import net.minecraftforge.common.util.ForgeDirection

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
