package mods.betterfoliage.client.render

import mods.octarinecore.client.render.AbstractBlockRenderingHandler
import mods.octarinecore.client.render.Axis
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Dir
import mods.octarinecore.client.render.Int3
import mods.octarinecore.client.render.Model
import mods.octarinecore.client.render.QuadIconResolver
import mods.octarinecore.client.render.Rotation
import mods.octarinecore.client.render.blockContext
import mods.octarinecore.client.render.face
import mods.octarinecore.client.render.modelRenderer
import mods.octarinecore.client.render.neverRender
import mods.octarinecore.client.render.noPost
import mods.octarinecore.client.render.rot
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraftforge.common.util.ForgeDirection

/** Index of SOUTH-EAST quadrant. */
const val SE = 0

/** Index of NORTH-EAST quadrant. */
const val NE = 1

/** Index of NORTH-WEST quadrant. */
const val NW = 2

/** Index of SOUTH-WEST quadrant. */
const val SW = 3

abstract class AbstractRenderColumn(modId: String) : AbstractBlockRenderingHandler(modId) {

    enum class BlockType {
        SOLID,
        NONSOLID,
        PARALLEL,
        PERPENDICULAR,
    }
    enum class QuadrantType {
        SMALL_RADIUS,
        LARGE_RADIUS,
        SQUARE,
        INVISIBLE,
    }

    /** The rotations necessary to bring the models in position for the 4 quadrants */
    val quadrantRotations = Array(4) { Rotation.rot90[ForgeDirection.UP.ordinal] * it }

    // ============================
    // Configuration
    // ============================
    abstract val radiusSmall: Double
    abstract val radiusLarge: Double
    abstract val surroundPredicate: (Block) -> Boolean
    abstract val connectPerpendicular: Boolean
    abstract val connectSolids: Boolean
    abstract val lenientConnect: Boolean

    // ============================
    // Models
    // ============================
    val sideSquare = model { columnSideSquare(-0.5, 0.5) }
    val sideRoundSmall = model { columnSide(radiusSmall, -0.5, 0.5) }
    val sideRoundLarge = model { columnSide(radiusLarge, -0.5, 0.5) }

    val extendTopSquare = model {
        columnSideSquare(0.5, 0.5 + radiusLarge, topExtension(radiusLarge))
    }
    val extendTopRoundSmall = model {
        columnSide(radiusSmall, 0.5, 0.5 + radiusLarge, topExtension(radiusLarge))
    }
    val extendTopRoundLarge = model {
        columnSide(radiusLarge, 0.5, 0.5 + radiusLarge, topExtension(radiusLarge))
    }
    fun extendTop(type: QuadrantType) = when (type) {
        QuadrantType.SMALL_RADIUS -> extendTopRoundSmall.model
        QuadrantType.LARGE_RADIUS -> extendTopRoundLarge.model
        QuadrantType.SQUARE -> extendTopSquare.model
        QuadrantType.INVISIBLE -> extendTopSquare.model
    }

    val extendBottomSquare = model {
        columnSideSquare(-0.5 - radiusLarge, -0.5, bottomExtension(radiusLarge))
    }
    val extendBottomRoundSmall = model {
        columnSide(radiusSmall, -0.5 - radiusLarge, -0.5, bottomExtension(radiusLarge))
    }
    val extendBottomRoundLarge = model {
        columnSide(radiusLarge, -0.5 - radiusLarge, -0.5, bottomExtension(radiusLarge))
    }
    fun extendBottom(type: QuadrantType) = when (type) {
        QuadrantType.SMALL_RADIUS -> extendBottomRoundSmall.model
        QuadrantType.LARGE_RADIUS -> extendBottomRoundLarge.model
        QuadrantType.SQUARE -> extendBottomSquare.model
        QuadrantType.INVISIBLE -> extendBottomSquare.model
    }

    val topSquare = model { columnLidSquare() }
    val topRoundSmall = model { columnLid(radiusSmall) }
    val topRoundLarge = model { columnLid(radiusLarge) }
    fun flatTop(type: QuadrantType) = when (type) {
        QuadrantType.SMALL_RADIUS -> topRoundSmall.model
        QuadrantType.LARGE_RADIUS -> topRoundLarge.model
        QuadrantType.SQUARE -> topSquare.model
        QuadrantType.INVISIBLE -> topSquare.model
    }

    val bottomSquare = model { columnLidSquare { it.rotate(rot(ForgeDirection.EAST) * 2 + rot(ForgeDirection.UP)) } }
    val bottomRoundSmall = model { columnLid(radiusSmall) { it.rotate(rot(ForgeDirection.EAST) * 2 + rot(ForgeDirection.UP)) } }
    val bottomRoundLarge = model { columnLid(radiusLarge) { it.rotate(rot(ForgeDirection.EAST) * 2 + rot(ForgeDirection.UP)) } }
    fun flatBottom(type: QuadrantType) = when (type) {
        QuadrantType.SMALL_RADIUS -> bottomRoundSmall.model
        QuadrantType.LARGE_RADIUS -> bottomRoundLarge.model
        QuadrantType.SQUARE -> bottomSquare.model
        QuadrantType.INVISIBLE -> bottomSquare.model
    }

    val transitionTop = model { mix(sideRoundLarge.model, sideRoundSmall.model) { it > 1 } }
    val transitionBottom = model { mix(sideRoundSmall.model, sideRoundLarge.model) { it > 1 } }

    val sideTexture: QuadIconResolver = { ctx, qi, _ ->
        if ((qi and 1) == 0) ctx.icon(ForgeDirection.SOUTH) else ctx.icon(ForgeDirection.EAST)
    }
    val upTexture: QuadIconResolver = { ctx, _, _ -> ctx.icon(ForgeDirection.UP) }
    val downTexture: QuadIconResolver = { ctx, _, _ -> ctx.icon(ForgeDirection.DOWN) }

    fun continuous(q1: QuadrantType, q2: QuadrantType) = q1 == q2 || ((q1 == QuadrantType.SQUARE || q1 == QuadrantType.INVISIBLE) && (q2 == QuadrantType.SQUARE || q2 == QuadrantType.INVISIBLE))

    abstract val axisFunc: (Block, Int) -> Axis
    abstract val blockPredicate: (Block, Int) -> Boolean

    override fun render(ctx: BlockContext, parent: RenderBlocks): Boolean {
        if (ctx.isSurroundedBy(surroundPredicate)) return false

        // get AO data
        if (renderWorldBlockBase(parent, face = neverRender)) return true

        // check log neighborhood
        val logAxis = ctx.blockAxis
        val baseRotation = rotationFromUp[(logAxis to Dir.P).face.ordinal]

        val upType = ctx.blockType(baseRotation, logAxis, Int3(0, 1, 0))
        val downType = ctx.blockType(baseRotation, logAxis, Int3(0, -1, 0))

        val quadrants = Array(4) { QuadrantType.SMALL_RADIUS }.checkNeighbors(ctx, baseRotation, logAxis, 0)
        val quadrantsTop = Array(4) { QuadrantType.SMALL_RADIUS }
        if (upType == BlockType.PARALLEL) quadrantsTop.checkNeighbors(ctx, baseRotation, logAxis, 1)
        val quadrantsBottom = Array(4) { QuadrantType.SMALL_RADIUS }
        if (downType == BlockType.PARALLEL) quadrantsBottom.checkNeighbors(ctx, baseRotation, logAxis, -1)

        quadrantRotations.forEachIndexed { idx, quadrantRotation ->
            // set rotation for the current quadrant
            val rotation = baseRotation + quadrantRotation

            // disallow sharp discontinuities in the chamfer radius, or tapering-in where inappropriate
            if (quadrants[idx] == QuadrantType.LARGE_RADIUS &&
                upType == BlockType.PARALLEL &&
                quadrantsTop[idx] != QuadrantType.LARGE_RADIUS &&
                downType == BlockType.PARALLEL &&
                quadrantsBottom[idx] != QuadrantType.LARGE_RADIUS
            ) {
                quadrants[idx] = QuadrantType.SMALL_RADIUS
            }

            // render side of current quadrant
            val sideModel =
                when (quadrants[idx]) {
                    QuadrantType.SMALL_RADIUS -> sideRoundSmall.model
                    QuadrantType.LARGE_RADIUS ->
                        if (upType == BlockType.PARALLEL && quadrantsTop[idx] == QuadrantType.SMALL_RADIUS) {
                            transitionTop.model
                        } else if (downType == BlockType.PARALLEL && quadrantsBottom[idx] == QuadrantType.SMALL_RADIUS) {
                            transitionBottom.model
                        } else {
                            sideRoundLarge.model
                        }
                    QuadrantType.SQUARE -> sideSquare.model
                    else -> null
                }

            if (sideModel != null) {
                modelRenderer.render(
                    sideModel,
                    rotation,
                    blockContext.blockCenter,
                    icon = sideTexture,
                    rotateUV = { 0 },
                    postProcess = noPost,
                )
            }

            // render top and bottom end of current quadrant
            var upModel: Model? = null
            var downModel: Model? = null
            var upIcon = upTexture
            var downIcon = downTexture
            var shouldRotateUp = true
            var shouldRotateDown = true

            when (upType) {
                BlockType.NONSOLID -> upModel = flatTop(quadrants[idx])
                BlockType.PERPENDICULAR -> {
                    if (!connectPerpendicular) {
                        upModel = flatTop(quadrants[idx])
                    } else {
                        upIcon = sideTexture
                        upModel = extendTop(quadrants[idx])
                        shouldRotateUp = false
                    }
                }
                BlockType.PARALLEL -> {
                    if (!continuous(quadrants[idx], quadrantsTop[idx])) {
                        if (quadrants[idx] == QuadrantType.SQUARE || quadrants[idx] == QuadrantType.INVISIBLE) {
                            upModel = topSquare.model
                        }
                    }
                }
                BlockType.SOLID -> {}
            }
            when (downType) {
                BlockType.NONSOLID -> downModel = flatBottom(quadrants[idx])
                BlockType.PERPENDICULAR -> {
                    if (!connectPerpendicular) {
                        downModel = flatBottom(quadrants[idx])
                    } else {
                        downIcon = sideTexture
                        downModel = extendBottom(quadrants[idx])
                        shouldRotateDown = false
                    }
                }
                BlockType.PARALLEL -> {
                    if (!continuous(quadrants[idx], quadrantsBottom[idx]) &&
                        (quadrants[idx] == QuadrantType.SQUARE || quadrants[idx] == QuadrantType.INVISIBLE)
                    ) {
                        downModel = bottomSquare.model
                    }
                }
                BlockType.SOLID -> {}
            }

            if (upModel != null) {
                modelRenderer.render(
                    upModel,
                    rotation,
                    blockContext.blockCenter,
                    icon = upIcon,
                    rotateUV = { if (shouldRotateUp) idx else 0 },
                    postProcess = noPost,
                )
            }
            if (downModel != null) {
                modelRenderer.render(
                    downModel,
                    rotation,
                    blockContext.blockCenter,
                    icon = downIcon,
                    rotateUV = { if (shouldRotateDown) 3 - idx else 0 },
                    postProcess = noPost,
                )
            }
        }

        return true
    }

    /** Sets the type of the given quadrant only if the new value is "stronger" (larger ordinal). */
    fun Array<QuadrantType>.upgrade(idx: Int, value: QuadrantType) {
        if (this[idx].ordinal < value.ordinal) this[idx] = value
    }

    /** Fill the array with [QuadrantType]s based on the blocks to the sides of this one. */
    fun Array<QuadrantType>.checkNeighbors(
        ctx: BlockContext,
        rotation: Rotation,
        logAxis: Axis,
        yOff: Int,
    ): Array<QuadrantType> {
        val blkS = ctx.blockType(rotation, logAxis, Int3(0, yOff, 1))
        val blkE = ctx.blockType(rotation, logAxis, Int3(1, yOff, 0))
        val blkN = ctx.blockType(rotation, logAxis, Int3(0, yOff, -1))
        val blkW = ctx.blockType(rotation, logAxis, Int3(-1, yOff, 0))

        // a solid block on one side will make the 2 neighboring quadrants SQUARE
        // if there are solid blocks to both sides of a quadrant, it is INVISIBLE
        if (connectSolids) {
            if (blkS == BlockType.SOLID) {
                upgrade(SW, QuadrantType.SQUARE)
                upgrade(SE, QuadrantType.SQUARE)
            }
            if (blkE == BlockType.SOLID) {
                upgrade(SE, QuadrantType.SQUARE)
                upgrade(NE, QuadrantType.SQUARE)
            }
            if (blkN == BlockType.SOLID) {
                upgrade(NE, QuadrantType.SQUARE)
                upgrade(NW, QuadrantType.SQUARE)
            }
            if (blkW == BlockType.SOLID) {
                upgrade(NW, QuadrantType.SQUARE)
                upgrade(SW, QuadrantType.SQUARE)
            }
            if (blkS == BlockType.SOLID && blkE == BlockType.SOLID) upgrade(SE, QuadrantType.INVISIBLE)
            if (blkN == BlockType.SOLID && blkE == BlockType.SOLID) upgrade(NE, QuadrantType.INVISIBLE)
            if (blkN == BlockType.SOLID && blkW == BlockType.SOLID) upgrade(NW, QuadrantType.INVISIBLE)
            if (blkS == BlockType.SOLID && blkW == BlockType.SOLID) upgrade(SW, QuadrantType.INVISIBLE)
        }
        val blkSE = ctx.blockType(rotation, logAxis, Int3(1, yOff, 1))
        val blkNE = ctx.blockType(rotation, logAxis, Int3(1, yOff, -1))
        val blkNW = ctx.blockType(rotation, logAxis, Int3(-1, yOff, -1))
        val blkSW = ctx.blockType(rotation, logAxis, Int3(-1, yOff, 1))

        if (lenientConnect) {
            // if the block forms the tip of an L-shape, connect to its neighbor with SQUARE quadrants
            if (blkE == BlockType.PARALLEL && (blkSE == BlockType.PARALLEL || blkNE == BlockType.PARALLEL)) {
                upgrade(SE, QuadrantType.SQUARE)
                upgrade(NE, QuadrantType.SQUARE)
            }
            if (blkN == BlockType.PARALLEL && (blkNE == BlockType.PARALLEL || blkNW == BlockType.PARALLEL)) {
                upgrade(NE, QuadrantType.SQUARE)
                upgrade(NW, QuadrantType.SQUARE)
            }
            if (blkW == BlockType.PARALLEL && (blkNW == BlockType.PARALLEL || blkSW == BlockType.PARALLEL)) {
                upgrade(NW, QuadrantType.SQUARE)
                upgrade(SW, QuadrantType.SQUARE)
            }
            if (blkS == BlockType.PARALLEL && (blkSE == BlockType.PARALLEL || blkSW == BlockType.PARALLEL)) {
                upgrade(SW, QuadrantType.SQUARE)
                upgrade(SE, QuadrantType.SQUARE)
            }
        }

        // if the block forms the middle of an L-shape, or is part of a 2x2 configuration,
        // connect to its neighbors with SQUARE quadrants, INVISIBLE on the inner corner, and
        // LARGE_RADIUS on the outer corner
        if (blkN == BlockType.PARALLEL && blkW == BlockType.PARALLEL && (lenientConnect || blkNW == BlockType.PARALLEL)) {
            upgrade(SE, QuadrantType.LARGE_RADIUS)
            upgrade(NE, QuadrantType.SQUARE)
            upgrade(SW, QuadrantType.SQUARE)
            upgrade(NW, QuadrantType.INVISIBLE)
        }
        if (blkS == BlockType.PARALLEL && blkW == BlockType.PARALLEL && (lenientConnect || blkSW == BlockType.PARALLEL)) {
            upgrade(NE, QuadrantType.LARGE_RADIUS)
            upgrade(SE, QuadrantType.SQUARE)
            upgrade(NW, QuadrantType.SQUARE)
            upgrade(SW, QuadrantType.INVISIBLE)
        }
        if (blkS == BlockType.PARALLEL && blkE == BlockType.PARALLEL && (lenientConnect || blkSE == BlockType.PARALLEL)) {
            upgrade(NW, QuadrantType.LARGE_RADIUS)
            upgrade(NE, QuadrantType.SQUARE)
            upgrade(SW, QuadrantType.SQUARE)
            upgrade(SE, QuadrantType.INVISIBLE)
        }
        if (blkN == BlockType.PARALLEL && blkE == BlockType.PARALLEL && (lenientConnect || blkNE == BlockType.PARALLEL)) {
            upgrade(SW, QuadrantType.LARGE_RADIUS)
            upgrade(SE, QuadrantType.SQUARE)
            upgrade(NW, QuadrantType.SQUARE)
            upgrade(NE, QuadrantType.INVISIBLE)
        }
        return this
    }

    /** Get the axis of the block */
    val BlockContext.blockAxis: Axis
        get() = axisFunc(block(Int3.zero), meta(Int3.zero))

    /** Get the type of the block at the given offset in a rotated reference frame. */
    fun BlockContext.blockType(rotation: Rotation, axis: Axis, offset: Int3): BlockType {
        val offsetRot = offset.rotate(rotation)
        val logBlock = block(offsetRot)
        val logMeta = meta(offsetRot)
        return if (!blockPredicate(logBlock, logMeta)) {
            if (logBlock.isOpaqueCube) BlockType.SOLID else BlockType.NONSOLID
        } else {
            if (axisFunc(logBlock, logMeta) == axis) BlockType.PARALLEL else BlockType.PERPENDICULAR
        }
    }
}
