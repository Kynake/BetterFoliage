package mods.octarinecore.client.render

import net.minecraftforge.common.util.ForgeDirection

const val DEFAULT_CORNER_DIMMING = 0.5f
const val DEFAULT_EDGE_DIMMING = 0.8f

// ================================
// Resolvers for automatic shading
// ================================
fun cornerAo(fallbackAxis: Axis): CornerShaderFactory = { face, dir1, dir2 ->
    val fallbackDir = listOf(face, dir1, dir2).find { it.axis == fallbackAxis }!!
    CornerSingleFallback(face, dir1, dir2, fallbackDir)
}

val cornerFlat = { face: ForgeDirection, dir1: ForgeDirection, dir2: ForgeDirection ->
    FaceFlat(face)
}

fun cornerInterpolate(edgeAxis: Axis, weight: Float, dimming: Float): CornerShaderFactory = { dir1, dir2, dir3 ->
    val edgeDir = listOf(dir1, dir2, dir3).find { it.axis == edgeAxis }!!
    val faceDirs = listOf(dir1, dir2, dir3).filter { it.axis != edgeAxis }
    CornerInterpolateDimming(faceDirs[0], faceDirs[1], edgeDir, weight, dimming)
}

// ================================
// Shaders
// ================================
object NoShader : Shader {
    override fun shade(context: ShadingContext, vertex: RenderVertex) = vertex.shade(AoData.black)
    override fun rotate(rot: Rotation) = this
}

class CornerSingleFallback(
    val face: ForgeDirection,
    val dir1: ForgeDirection,
    val dir2: ForgeDirection,
    val fallbackDir: ForgeDirection,
    val fallbackDimming: Float = DEFAULT_CORNER_DIMMING,
) : Shader {
    val offset = Int3(fallbackDir)
    override fun shade(context: ShadingContext, vertex: RenderVertex) {
        val shading = context.aoShading(face, dir1, dir2)
        if (shading.valid) {
            vertex.shade(shading)
        } else {
            vertex.shade(
                context.blockBrightness(offset) brMul fallbackDimming,
                context.blockColor(offset) colorMul fallbackDimming,
            )
        }
    }
    override fun rotate(rot: Rotation) = CornerSingleFallback(
        face.rotate(rot),
        dir1.rotate(rot),
        dir2.rotate(rot),
        fallbackDir.rotate(rot),
        fallbackDimming,
    )
}

class EdgeInterpolateFallback(
    val face: ForgeDirection,
    val edgeDir: ForgeDirection,
    val pos: Double,
    val fallbackDimming: Float = DEFAULT_EDGE_DIMMING,
) : Shader {
    val offset = Int3(edgeDir)
    val edgeAxis = axes.find { it != face.axis && it != edgeDir.axis }!!
    val weightN = (0.5 - pos).toFloat()
    val weightP = (0.5 + pos).toFloat()

    override fun shade(context: ShadingContext, vertex: RenderVertex) {
        val shadingP = context.aoShading(face, edgeDir, (edgeAxis to Dir.P).face)
        val shadingN = context.aoShading(face, edgeDir, (edgeAxis to Dir.N).face)
        if (!shadingP.valid && !shadingN.valid) {
            return vertex.shade(
                context.blockBrightness(offset) brMul fallbackDimming,
                context.blockColor(offset) colorMul fallbackDimming,
            )
        }
        if (!shadingP.valid) return vertex.shade(shadingN)
        if (!shadingN.valid) return vertex.shade(shadingP)
        vertex.shade(shadingP, shadingN, weightP, weightN)
    }
    override fun rotate(rot: Rotation) = EdgeInterpolateFallback(face.rotate(rot), edgeDir.rotate(rot), pos)
}

class CornerInterpolateDimming(
    val face1: ForgeDirection,
    val face2: ForgeDirection,
    val edgeDir: ForgeDirection,
    val weight: Float,
    val dimming: Float,
    val fallbackDimming: Float = DEFAULT_CORNER_DIMMING,
) : Shader {
    val offset = Int3(edgeDir)
    override fun shade(context: ShadingContext, vertex: RenderVertex) {
        var shading1 = context.aoShading(face1, edgeDir, face2)
        var shading2 = context.aoShading(face2, edgeDir, face1)
        var weight1 = weight
        var weight2 = 1.0f - weight
        if (!shading1.valid && !shading2.valid) {
            return vertex.shade(
                context.blockBrightness(offset) brMul fallbackDimming,
                context.blockColor(offset) colorMul fallbackDimming,
            )
        }
        if (!shading1.valid) {
            shading1 = shading2
            weight1 *= dimming
        }
        if (!shading2.valid) {
            shading2 = shading1
            weight2 *= dimming
        }
        vertex.shade(shading1, shading2, weight1, weight2)
    }

    override fun rotate(rot: Rotation) = CornerInterpolateDimming(
        face1.rotate(rot),
        face2.rotate(rot),
        edgeDir.rotate(rot),
        weight,
        dimming,
        fallbackDimming,
    )
}

class FaceCenter(val face: ForgeDirection) : Shader {
    override fun shade(context: ShadingContext, vertex: RenderVertex) {
        vertex.red = 0.0f
        vertex.green = 0.0f
        vertex.blue = 0.0f
        val b = IntArray(4)
        faceCorners[face.ordinal].asList.forEachIndexed { idx, corner ->
            val shading = context.aoShading(face, corner.first, corner.second)
            vertex.red += shading.red
            vertex.green += shading.green
            vertex.blue += shading.blue
            b[idx] = shading.brightness
        }
        vertex.apply {
            red *= 0.25f
            green *= 0.25f
            blue *= 0.25f
        }
        vertex.brightness = brSum(0.25f, *b)
    }
    override fun rotate(rot: Rotation) = FaceCenter(face.rotate(rot))
}

class FaceFlat(val face: ForgeDirection) : Shader {
    override fun shade(context: ShadingContext, vertex: RenderVertex) {
        val color = context.blockColor(Int3.zero)
        vertex.shade(context.blockBrightness(face.offset), color)
    }
    override fun rotate(rot: Rotation): Shader = FaceFlat(face.rotate(rot))
}
