package mods.betterfoliage.client.render.features;

import mods.betterfoliage.client.render.UVPlane;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.RenderUtils;

public class LogRenderer extends BlockRenderer {

    private static LogRenderer instance;

    public static LogRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Log Renderer");
            instance = new LogRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Config.roundLogs.INSTANCE.getEnabled()) return false;

        return Config.blocks.INSTANCE.getLogs()
            .matchesID(world.getBlock(x, y, z));
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        ForgeDirection axis = determineLogAxis(world, x, y, z, block);

        //return RenderRoundLog(world, x, y, z, block, renderer, axis);

        return debugRender(world, x, y, z, block, renderer, axis);
    }

    private static boolean debugRender(IBlockAccess world, int x, int y, int z, Block block,
          RenderBlocks renderer, ForgeDirection axis) {

        boolean didRender = RenderRoundLog(world, x, y, z, block, renderer, axis, false);

        final int meta = world.getBlockMetadata(x, y, z);
        if (((meta >> 2) & 3) == 0) {
            for (final ForgeDirection side : UVPlane.getSides(axis)) {
                didRender |= RenderRoundLog(world, x, y, z, block, renderer, side, true);
            }
        }

        return didRender;
    }

    private static ForgeDirection determineLogAxis(IBlockAccess world, int x, int y, int z, Block block) {
        final int meta = world.getBlockMetadata(x, y, z);

        return switch ((meta >> 2) & 3) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            default -> ForgeDirection.UP;
        };
    }

    /// Renders a log block with all rounded corners
    private static boolean RenderRoundLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, boolean isConnectorPiece) {

        boolean didRender = false;

        final ForgeDirection[] sides = UVPlane.getSides(axis);
        for (final ForgeDirection clock : sides) {
            final ForgeDirection counter = axis.getRotation(clock);
            didRender |= RenderRoundCorner(world, x, y, z, block, renderer, axis, clock, counter,
                isConnectorPiece);
        }

        return didRender;
    }

    /// Renders a log block with two adjacent rounded corners and two adjacent square corners
    private static boolean RenderRoundHalfLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, ForgeDirection side) {

        boolean didRender = false;
        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final ForgeDirection counterclockwise = axis.getRotation(side);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        final double axisX = (axis.offsetX / 2.0);
        final double axisY = (axis.offsetY / 2.0);
        final double axisZ = (axis.offsetZ / 2.0);

        final double sideX = (side.offsetX / 2.0);
        final double sideY = (side.offsetY / 2.0);
        final double sideZ = (side.offsetZ / 2.0);

        final double counterX = (counterclockwise.offsetX / 2.0);
        final double counterY = (counterclockwise.offsetY / 2.0);
        final double counterZ = (counterclockwise.offsetZ / 2.0);

        /// Coords
        // Front
        final double frontCenterX = midX + axisX;
        final double frontCenterY = midY + axisY;
        final double frontCenterZ = midZ + axisZ;

        final double frontCenterCounterX = frontCenterX + counterX;
        final double frontCenterCounterY = frontCenterY + counterY;
        final double frontCenterCounterZ = frontCenterZ + counterZ;

        final double frontCenterClockX = frontCenterX - counterX;
        final double frontCenterClockY = frontCenterY - counterY;
        final double frontCenterClockZ = frontCenterZ - counterZ;

        final double frontCornerCounterX = frontCenterCounterX - sideX;
        final double frontCornerCounterY = frontCenterCounterY - sideY;
        final double frontCornerCounterZ = frontCenterCounterZ - sideZ;

        final double frontCornerClockX = frontCenterClockX - sideX;
        final double frontCornerClockY = frontCenterClockY - sideY;
        final double frontCornerClockZ = frontCenterClockZ - sideZ;

        // Back
        final double backCenterX = midX - axisX;
        final double backCenterY = midY - axisY;
        final double backCenterZ = midZ - axisZ;

        final double backCenterCounterX = backCenterX + counterX;
        final double backCenterCounterY = backCenterY + counterY;
        final double backCenterCounterZ = backCenterZ + counterZ;

        final double backCenterClockX = backCenterX - counterX;
        final double backCenterClockY = backCenterY - counterY;
        final double backCenterClockZ = backCenterZ - counterZ;

        final double backCornerCounterX = backCenterCounterX - sideX;
        final double backCornerCounterY = backCenterCounterY - sideY;
        final double backCornerCounterZ = backCenterCounterZ - sideZ;

        final double backCornerClockX = backCenterClockX - sideX;
        final double backCornerClockY = backCenterClockY - sideY;
        final double backCornerClockZ = backCenterClockZ - sideZ;

        /// Square corners
        IIcon sprite;

        double leftU;
        double rightU;
        double topV;
        double botV;

        double midU;
        double midV;

        double lengthU;
        double lengthV;

        double halfU;
        double halfV;

        UVPlane plane;

        double halfOffsetU;
        double halfOffsetV;

        double halfCounterOffsetU;
        double halfCounterOffsetV;

        double centerCounterU;
        double centerCounterV;

        double centerClockU;
        double centerClockV;

        double cornerCounterU;
        double cornerCounterV;

        double cornerClockU;
        double cornerClockV;

        {
            sprite = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            midV = (botV + topV) / 2.0;

            lengthU = rightU - leftU;
            lengthV = botV - topV;

            halfU = lengthU / 2.0;
            halfV = lengthV / 2.0;

            plane = UVPlane.toUVPlane(axis);

            halfOffsetU = plane.getOffsetU(side) * halfU;
            halfOffsetV = plane.getOffsetV(side) * halfV;

            halfCounterOffsetU = plane.getOffsetU(counterclockwise) * halfU;
            halfCounterOffsetV = plane.getOffsetV(counterclockwise) * halfV;

            centerCounterU = midU + halfCounterOffsetU;
            centerCounterV = midV + halfCounterOffsetV;

            centerClockU = midU - halfCounterOffsetU;
            centerClockV = midV - halfCounterOffsetV;

            cornerCounterU = centerCounterU - halfOffsetU;
            cornerCounterV = centerCounterV - halfOffsetV;

            cornerClockU = centerClockU - halfOffsetU;
            cornerClockV = centerClockV - halfOffsetV;

            // Faces
            tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, centerCounterU, centerCounterV);
            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, cornerCounterU, cornerCounterV);
            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, cornerClockU, cornerClockV);
            tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, centerClockU, centerClockV);
            didRender = true;
        }

        {
            sprite = renderer.getBlockIcon(block, world, x, y, z, axis.getOpposite().ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            midV = (botV + topV) / 2.0;

            lengthU = rightU - leftU;
            lengthV = botV - topV;

            halfU = lengthU / 2.0;
            halfV = lengthV / 2.0;

            plane = UVPlane.toUVPlane(axis.getOpposite());

            halfOffsetU = plane.getOffsetU(side) * halfU;
            halfOffsetV = plane.getOffsetV(side) * halfV;

            halfCounterOffsetU = plane.getOffsetU(counterclockwise) * halfU;
            halfCounterOffsetV = plane.getOffsetV(counterclockwise) * halfV;

            centerCounterU = midU + halfCounterOffsetU;
            centerCounterV = midV + halfCounterOffsetV;

            centerClockU = midU - halfCounterOffsetU;
            centerClockV = midV - halfCounterOffsetV;

            cornerCounterU = centerCounterU - halfOffsetU;
            cornerCounterV = centerCounterV - halfOffsetV;

            cornerClockU = centerClockU - halfOffsetU;
            cornerClockV = centerClockV - halfOffsetV;

            tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, centerClockU, centerClockV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, cornerClockU, cornerClockV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, cornerCounterU, cornerCounterV);
            tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, centerCounterU, centerCounterV);
            didRender = true;
        }

        final double axisOffset = axisX + axisY + axisZ;

        // Sides
        {
            // Counter
            sprite = renderer.getBlockIcon(block, world, x, y, z, counterclockwise.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            lengthU = rightU - leftU;

            centerCounterU = midU + axisOffset * lengthU;

            tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, midU, topV);
            tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, midU, botV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, centerCounterU, botV);
            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, centerCounterU, topV);
            didRender = true;
        }

        {
            // Clock
            ForgeDirection clock = counterclockwise.getOpposite();

            sprite = renderer.getBlockIcon(block, world, x, y, z, clock.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            lengthU = rightU - leftU;

            cornerCounterU = midU - axisOffset * lengthU;

            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, cornerCounterU, topV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, cornerCounterU, botV);
            tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, midU, botV);
            tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, midU, topV);
            didRender = true;
        }

        {
            // Back
            sprite = renderer.getBlockIcon(block, world, x, y, z, side.getOpposite().ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, leftU, topV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, leftU, botV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, rightU, botV);
            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, rightU, topV);
            didRender = true;
        }

        /// Round corners
        didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, side, counterclockwise, false);
        didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, counterclockwise.getOpposite(), side, false);

        return didRender;
    }

    /// Renders a log block with one rounded corner and three square corners.
    /// Rounded corner is always counterclockwise of side.
    private static boolean RenderRoundQuarterLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, ForgeDirection side) {

        boolean didRender = false;
        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final ForgeDirection counterclockwise = axis.getRotation(side);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        final double axisX = (axis.offsetX / 2.0);
        final double axisY = (axis.offsetY / 2.0);
        final double axisZ = (axis.offsetZ / 2.0);

        final double sideX = (side.offsetX / 2.0);
        final double sideY = (side.offsetY / 2.0);
        final double sideZ = (side.offsetZ / 2.0);

        final double counterX = (counterclockwise.offsetX / 2.0);
        final double counterY = (counterclockwise.offsetY / 2.0);
        final double counterZ = (counterclockwise.offsetZ / 2.0);

        /// Coords
        // Front
        final double frontCenterX = midX + axisX;
        final double frontCenterY = midY + axisY;
        final double frontCenterZ = midZ + axisZ;

        final double frontCenterCounterX = frontCenterX + counterX;
        final double frontCenterCounterY = frontCenterY + counterY;
        final double frontCenterCounterZ = frontCenterZ + counterZ;

        final double frontCenterClockX = frontCenterX - counterX;
        final double frontCenterClockY = frontCenterY - counterY;
        final double frontCenterClockZ = frontCenterZ - counterZ;

        final double frontCornerCounterX = frontCenterCounterX - sideX;
        final double frontCornerCounterY = frontCenterCounterY - sideY;
        final double frontCornerCounterZ = frontCenterCounterZ - sideZ;

        final double frontCornerClockX = frontCenterClockX - sideX;
        final double frontCornerClockY = frontCenterClockY - sideY;
        final double frontCornerClockZ = frontCenterClockZ - sideZ;

        // Front quarter square
        final double frontCenterSideX = frontCenterX + sideX;
        final double frontCenterSideY = frontCenterY + sideY;
        final double frontCenterSideZ = frontCenterZ + sideZ;

        final double frontCornerSideX = frontCenterClockX + sideX;
        final double frontCornerSideY = frontCenterClockY + sideY;
        final double frontCornerSideZ = frontCenterClockZ + sideZ;

        // Back
        final double backCenterX = midX - axisX;
        final double backCenterY = midY - axisY;
        final double backCenterZ = midZ - axisZ;

        final double backCenterCounterX = backCenterX + counterX;
        final double backCenterCounterY = backCenterY + counterY;
        final double backCenterCounterZ = backCenterZ + counterZ;

        final double backCenterClockX = backCenterX - counterX;
        final double backCenterClockY = backCenterY - counterY;
        final double backCenterClockZ = backCenterZ - counterZ;

        final double backCornerCounterX = backCenterCounterX - sideX;
        final double backCornerCounterY = backCenterCounterY - sideY;
        final double backCornerCounterZ = backCenterCounterZ - sideZ;

        final double backCornerClockX = backCenterClockX - sideX;
        final double backCornerClockY = backCenterClockY - sideY;
        final double backCornerClockZ = backCenterClockZ - sideZ;

        // Back quarter square
        final double backCenterSideX = backCenterX + sideX;
        final double backCenterSideY = backCenterY + sideY;
        final double backCenterSideZ = backCenterZ + sideZ;

        final double backCornerSideX = backCenterClockX + sideX;
        final double backCornerSideY = backCenterClockY + sideY;
        final double backCornerSideZ = backCenterClockZ + sideZ;

        /// Square corners
        IIcon sprite;

        double leftU;
        double rightU;
        double topV;
        double botV;

        double midU;
        double midV;

        double lengthU;
        double lengthV;

        double halfU;
        double halfV;

        UVPlane plane;

        double halfOffsetU;
        double halfOffsetV;

        double halfCounterOffsetU;
        double halfCounterOffsetV;

        double centerCounterU;
        double centerCounterV;

        double centerClockU;
        double centerClockV;

        double cornerCounterU;
        double cornerCounterV;

        double cornerClockU;
        double cornerClockV;

        double centerSideU;
        double centerSideV;

        double cornerSideU;
        double cornerSideV;

        // Faces
        {
            sprite = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            midV = (botV + topV) / 2.0;

            lengthU = rightU - leftU;
            lengthV = botV - topV;

            halfU = lengthU / 2.0;
            halfV = lengthV / 2.0;

            plane = UVPlane.toUVPlane(axis);

            halfOffsetU = plane.getOffsetU(side) * halfU;
            halfOffsetV = plane.getOffsetV(side) * halfV;

            halfCounterOffsetU = plane.getOffsetU(counterclockwise) * halfU;
            halfCounterOffsetV = plane.getOffsetV(counterclockwise) * halfV;

            centerCounterU = midU + halfCounterOffsetU;
            centerCounterV = midV + halfCounterOffsetV;

            centerClockU = midU - halfCounterOffsetU;
            centerClockV = midV - halfCounterOffsetV;

            cornerCounterU = centerCounterU - halfOffsetU;
            cornerCounterV = centerCounterV - halfOffsetV;

            cornerClockU = centerClockU - halfOffsetU;
            cornerClockV = centerClockV - halfOffsetV;

            // Side opposite face
            tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, centerCounterU, centerCounterV);
            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, cornerCounterU, cornerCounterV);
            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, cornerClockU, cornerClockV);
            tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, centerClockU, centerClockV);

            centerSideU = midU + halfOffsetU;
            centerSideV = midV + halfOffsetV;

            cornerSideU = centerSideU - halfCounterOffsetU;
            cornerSideV = centerSideV - halfCounterOffsetV;

            // Quarter face
            tess.addVertexWithUV(frontCenterSideX, frontCenterSideY, frontCenterSideZ, centerSideU, centerSideV);
            tess.addVertexWithUV(frontCenterX, frontCenterY, frontCenterZ, midU, midV);
            tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, centerClockU, centerClockV);
            tess.addVertexWithUV(frontCornerSideX, frontCornerSideY, frontCornerSideZ, cornerSideU, cornerSideV);

            didRender = true;
        }

        {
            sprite = renderer.getBlockIcon(block, world, x, y, z, axis.getOpposite().ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            midV = (botV + topV) / 2.0;

            lengthU = rightU - leftU;
            lengthV = botV - topV;

            halfU = lengthU / 2.0;
            halfV = lengthV / 2.0;

            plane = UVPlane.toUVPlane(axis.getOpposite());

            halfOffsetU = plane.getOffsetU(side) * halfU;
            halfOffsetV = plane.getOffsetV(side) * halfV;

            halfCounterOffsetU = plane.getOffsetU(counterclockwise) * halfU;
            halfCounterOffsetV = plane.getOffsetV(counterclockwise) * halfV;

            centerCounterU = midU + halfCounterOffsetU;
            centerCounterV = midV + halfCounterOffsetV;

            centerClockU = midU - halfCounterOffsetU;
            centerClockV = midV - halfCounterOffsetV;

            cornerCounterU = centerCounterU - halfOffsetU;
            cornerCounterV = centerCounterV - halfOffsetV;

            cornerClockU = centerClockU - halfOffsetU;
            cornerClockV = centerClockV - halfOffsetV;

            // Side opposite face
            tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, centerClockU, centerClockV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, cornerClockU, cornerClockV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, cornerCounterU, cornerCounterV);
            tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, centerCounterU, centerCounterV);

            centerSideU = midU + halfOffsetU;
            centerSideV = midV + halfOffsetV;

            cornerSideU = centerSideU - halfCounterOffsetU;
            cornerSideV = centerSideV - halfCounterOffsetV;

            // Quarter face
            tess.addVertexWithUV(backCornerSideX, backCornerSideY, backCornerSideZ, cornerSideU, cornerSideV);
            tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, centerClockU, centerClockV);
            tess.addVertexWithUV(backCenterX, backCenterY, backCenterZ, midU, midV);
            tess.addVertexWithUV(backCenterSideX, backCenterSideY, backCenterSideZ, centerSideU, centerSideV);

            didRender = true;
        }

        final double axisOffset = axisX + axisY + axisZ;

        // Sides
        {
            // Main Side
            sprite = renderer.getBlockIcon(block, world, x, y, z, side.ordinal());
            leftU = sprite.getMinU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();
            midU = (sprite.getMaxU() + leftU) / 2.0;

            tess.addVertexWithUV(frontCornerSideX, frontCornerSideY, frontCornerSideZ, leftU, topV);
            tess.addVertexWithUV(backCornerSideX, backCornerSideY, backCornerSideZ, leftU, botV);
            tess.addVertexWithUV(backCenterSideX, backCenterSideY, backCenterSideZ, midU, botV);
            tess.addVertexWithUV(frontCenterSideX, frontCenterSideY, frontCenterSideZ, midU, topV);
            didRender = true;
        }

        {
            // Counter
            sprite = renderer.getBlockIcon(block, world, x, y, z, counterclockwise.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            lengthU = rightU - leftU;

            centerCounterU = midU + axisOffset * lengthU;

            tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, midU, topV);
            tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, midU, botV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, centerCounterU, botV);
            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, centerCounterU, topV);
            didRender = true;
        }

        {
            // Clock
            ForgeDirection clock = counterclockwise.getOpposite();

            sprite = renderer.getBlockIcon(block, world, x, y, z, clock.ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            midU = (rightU + leftU) / 2.0;
            lengthU = rightU - leftU;

            final double axisU = axisOffset * lengthU;
            cornerCounterU = midU - axisU;
            cornerClockU = midU + axisU;

            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, leftU, topV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, leftU, botV);
            tess.addVertexWithUV(backCornerSideX, backCornerSideY, backCornerSideZ, rightU, botV);
            tess.addVertexWithUV(frontCornerSideX, frontCornerSideY, frontCornerSideZ, rightU, topV);
            didRender = true;
        }

        {
            // Back
            sprite = renderer.getBlockIcon(block, world, x, y, z, side.getOpposite().ordinal());
            leftU = sprite.getMinU();
            rightU = sprite.getMaxU();
            topV = sprite.getMinV();
            botV = sprite.getMaxV();

            tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, leftU, topV);
            tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, leftU, botV);
            tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, rightU, botV);
            tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, rightU, topV);
            didRender = true;
        }

        /// Round corners
        didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, side, counterclockwise, false);

        return didRender;
    }

    private static boolean RenderRoundCorner(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, ForgeDirection clockwise, ForgeDirection counterclockwise, boolean isConnectorPiece) {

        boolean didRender = false;
        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        final double axisX = axis.offsetX / 2.0;
        final double axisY = axis.offsetY / 2.0;
        final double axisZ = axis.offsetZ / 2.0;

        final double clockX = clockwise.offsetX / 2.0;
        final double clockY = clockwise.offsetY / 2.0;
        final double clockZ = clockwise.offsetZ / 2.0;

        final double counterX = counterclockwise.offsetX / 2.0;
        final double counterY = counterclockwise.offsetY / 2.0;
        final double counterZ = counterclockwise.offsetZ / 2.0;

        // TODO: Radius definer
        final double frontRadius = Config.roundLogs.INSTANCE.getRadiusSmall();
        final double backRadius = Config.roundLogs.INSTANCE.getRadiusSmall();

        final double frontEdgeToCenter = 0.5 - frontRadius;
        final double frontDiagToCenter = (frontRadius / 2.0) + frontEdgeToCenter;

        final double backEdgeToCenter = 0.5 - backRadius;

        /// COORDS
        // Front
        final double frontCenterX = midX + axisX;
        final double frontCenterY = midY + axisY;
        final double frontCenterZ = midZ + axisZ;

        final double frontClockCenterX = frontCenterX + clockX;
        final double frontClockCenterY = frontCenterY + clockY;
        final double frontClockCenterZ = frontCenterZ + clockZ;

        final double frontCounterCenterX = frontCenterX + counterX;
        final double frontCounterCenterY = frontCenterY + counterY;
        final double frontCounterCenterZ = frontCenterZ + counterZ;

        final double frontClockEdgeX = frontClockCenterX + counterclockwise.offsetX * frontEdgeToCenter;
        final double frontClockEdgeY = frontClockCenterY + counterclockwise.offsetY * frontEdgeToCenter;
        final double frontClockEdgeZ = frontClockCenterZ + counterclockwise.offsetZ * frontEdgeToCenter;

        final double frontCounterEdgeX = frontCounterCenterX + clockwise.offsetX * frontEdgeToCenter;
        final double frontCounterEdgeY = frontCounterCenterY + clockwise.offsetY * frontEdgeToCenter;
        final double frontCounterEdgeZ = frontCounterCenterZ + clockwise.offsetZ * frontEdgeToCenter;

        final double frontDiagX = frontCenterX + frontDiagToCenter * (clockwise.offsetX + counterclockwise.offsetX);
        final double frontDiagY = frontCenterY + frontDiagToCenter * (clockwise.offsetY + counterclockwise.offsetY);
        final double frontDiagZ = frontCenterZ + frontDiagToCenter * (clockwise.offsetZ + counterclockwise.offsetZ);

        // Back
        final double backCenterX;
        final double backCenterY;
        final double backCenterZ;

        final double backClockCenterX;
        final double backClockCenterY;
        final double backClockCenterZ;

        final double backCounterCenterX;
        final double backCounterCenterY;
        final double backCounterCenterZ;

        double backDiagX;
        double backDiagY;
        double backDiagZ;

        if (isConnectorPiece) {
            final double zProtection = Config.roundLogs.INSTANCE.getZProtection();
            final double backConnector = (backRadius * zProtection / 2.0) + backEdgeToCenter;

            backCenterX = midX;
            backCenterY = midY;
            backCenterZ = midZ;

            backClockCenterX = backCenterX + clockX * zProtection;
            backClockCenterY = backCenterY + clockY * zProtection;
            backClockCenterZ = backCenterZ + clockZ * zProtection;

            backCounterCenterX = backCenterX + counterX * zProtection;
            backCounterCenterY = backCenterY + counterY * zProtection;
            backCounterCenterZ = backCenterZ + counterZ * zProtection;

            backDiagX = backCenterX + backConnector * (clockwise.offsetX + counterclockwise.offsetX);
            backDiagY = backCenterY + backConnector * (clockwise.offsetY + counterclockwise.offsetY);
            backDiagZ = backCenterZ + backConnector * (clockwise.offsetZ + counterclockwise.offsetZ);
        }
        else {
            final double backDiagToCenter = (backRadius / 2.0) + backEdgeToCenter;

            backCenterX = midX - axisX;
            backCenterY = midY - axisY;
            backCenterZ = midZ - axisZ;

            backClockCenterX = backCenterX + clockX;
            backClockCenterY = backCenterY + clockY;
            backClockCenterZ = backCenterZ + clockZ;

            backCounterCenterX = backCenterX + counterX;
            backCounterCenterY = backCenterY + counterY;
            backCounterCenterZ = backCenterZ + counterZ;

            backDiagX = backCenterX + backDiagToCenter * (clockwise.offsetX + counterclockwise.offsetX);
            backDiagY = backCenterY + backDiagToCenter * (clockwise.offsetY + counterclockwise.offsetY);
            backDiagZ = backCenterZ + backDiagToCenter * (clockwise.offsetZ + counterclockwise.offsetZ);
        }

        final double backClockEdgeX = backClockCenterX + counterclockwise.offsetX * backEdgeToCenter;
        final double backClockEdgeY = backClockCenterY + counterclockwise.offsetY * backEdgeToCenter;
        final double backClockEdgeZ = backClockCenterZ + counterclockwise.offsetZ * backEdgeToCenter;

        final double backCounterEdgeX = backCounterCenterX + clockwise.offsetX * backEdgeToCenter;
        final double backCounterEdgeY = backCounterCenterY + clockwise.offsetY * backEdgeToCenter;
        final double backCounterEdgeZ = backCounterCenterZ + clockwise.offsetZ * backEdgeToCenter;

        ////////// RENDER //////////
        double leftU;
        double rightU;
        double topV;
        double botV;

        double midU;
        double midV;

        double lengthU;
        double lengthV;

        double halfU;
        double halfV;

        UVPlane plane;

        double clockOffsetU;
        double clockOffsetV;

        double counterOffsetU;
        double counterOffsetV;

        double clockU;
        double clockV;

        double counterU;
        double counterV;

        double cornerU;
        double cornerV;

        double clockEdgeU;
        double clockEdgeV;

        double counterEdgeU;
        double counterEdgeV;

        if (!isConnectorPiece) {
            /// Front
            {
                final IIcon spriteFront = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());

                leftU = spriteFront.getMinU();
                rightU = spriteFront.getMaxU();
                topV = spriteFront.getMinV();
                botV = spriteFront.getMaxV();

                midU = (rightU + leftU) / 2.0;
                midV = (botV + topV) / 2.0;

                lengthU = rightU - leftU;
                lengthV = botV - topV;

                halfU = lengthU / 2.0;
                halfV = lengthV / 2.0;

                plane = UVPlane.toUVPlane(axis);

                clockOffsetU = plane.getOffsetU(clockwise);
                clockOffsetV = plane.getOffsetV(clockwise);

                counterOffsetU = plane.getOffsetU(counterclockwise);
                counterOffsetV = plane.getOffsetV(counterclockwise);

                clockU = midU + clockOffsetU * halfU;
                clockV = midV + clockOffsetV * halfV;

                counterU = midU + counterOffsetU * halfU;
                counterV = midV + counterOffsetV * halfV;

                cornerU = midU + (clockOffsetU + counterOffsetU) * halfU;
                cornerV = midV + (clockOffsetV + counterOffsetV) * halfV;

                clockEdgeU = cornerU - counterOffsetU * frontRadius * lengthU;
                clockEdgeV = cornerV - counterOffsetV * frontRadius * lengthU;

                counterEdgeU = cornerU - clockOffsetU * frontRadius * lengthU;
                counterEdgeV = cornerV - clockOffsetV * frontRadius * lengthV;

                tess.addVertexWithUV(frontCenterX, frontCenterY, frontCenterZ, midU, midV);
                tess.addVertexWithUV(frontClockCenterX, frontClockCenterY, frontClockCenterZ, clockU, clockV);
                tess.addVertexWithUV(frontClockEdgeX, frontClockEdgeY, frontClockEdgeZ, clockEdgeU, clockEdgeV);
                tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, cornerU, cornerV);

                tess.addVertexWithUV(frontCenterX, frontCenterY, frontCenterZ, midU, midV);
                tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, cornerU, cornerV);
                tess.addVertexWithUV(frontCounterEdgeX, frontCounterEdgeY, frontCounterEdgeZ, counterEdgeU, counterEdgeV);
                tess.addVertexWithUV(frontCounterCenterX, frontCounterCenterY, frontCounterCenterZ, counterU, counterV);

                didRender = true;
            }

            /// Back
            {
                final IIcon spriteBack = renderer.getBlockIcon(block, world, x, y, z, axis.getOpposite().ordinal());

                leftU = spriteBack.getMinU();
                rightU = spriteBack.getMaxU();
                topV = spriteBack.getMinV();
                botV = spriteBack.getMaxV();

                midU = (rightU + leftU) / 2.0;
                midV = (botV + topV) / 2.0;

                lengthU = rightU - leftU;
                lengthV = botV - topV;

                halfU = lengthU / 2.0;
                halfV = lengthV / 2.0;

                plane = UVPlane.toUVPlane(axis.getOpposite());

                clockOffsetU = plane.getOffsetU(clockwise);
                clockOffsetV = plane.getOffsetV(clockwise);

                counterOffsetU = plane.getOffsetU(counterclockwise);
                counterOffsetV = plane.getOffsetV(counterclockwise);

                clockU = midU + clockOffsetU * halfU;
                clockV = midV + clockOffsetV * halfV;

                counterU = midU + counterOffsetU * halfU;
                counterV = midV + counterOffsetV * halfV;

                cornerU = midU + (clockOffsetU + counterOffsetU) * halfU;
                cornerV = midV + (clockOffsetV + counterOffsetV) * halfV;

                clockEdgeU = cornerU - counterOffsetU * backRadius * lengthU;
                clockEdgeV = cornerV - counterOffsetV * backRadius * lengthU;

                counterEdgeU = cornerU - clockOffsetU * backRadius * lengthU;
                counterEdgeV = cornerV - clockOffsetV * backRadius * lengthV;

                tess.addVertexWithUV(backCenterX, backCenterY, backCenterZ, midU, midV);
                tess.addVertexWithUV(backCounterCenterX, backCounterCenterY, backCounterCenterZ, counterU, counterV);
                tess.addVertexWithUV(backCounterEdgeX, backCounterEdgeY, backCounterEdgeZ, counterEdgeU, counterEdgeV);
                tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, cornerU, cornerV);

                tess.addVertexWithUV(backCenterX, backCenterY, backCenterZ, midU, midV);
                tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, cornerU, cornerV);
                tess.addVertexWithUV(backClockEdgeX, backClockEdgeY, backClockEdgeZ, clockEdgeU, clockEdgeV);
                tess.addVertexWithUV(backClockCenterX, backClockCenterY, backClockCenterZ, clockU, clockV);

                didRender = true;
            }
        }

        /// Sides
        final double axisOffset = axis.offsetX + axis.offsetY + axis.offsetZ;
        final double axisOffsetHalf = axisX + axisY + axisZ;

        final double axisOffsetConnector;

        final IIcon spriteClock;
        final IIcon spriteCounter;

        if (isConnectorPiece) {
            axisOffsetConnector = 0;

            final ForgeDirection baseAxis = determineLogAxis(world, x, y, z, block);
            final ForgeDirection transposeConnector = baseAxis.getRotation(axis);

            spriteClock = renderer.getBlockIcon(block, world, x, y, z, clockwise.getRotation(transposeConnector).ordinal());
            spriteCounter = renderer.getBlockIcon(block, world, x, y, z, counterclockwise.getRotation(transposeConnector).ordinal());
        }
        else {
            axisOffsetConnector = axisOffsetHalf;

            spriteClock = renderer.getBlockIcon(block, world, x, y, z, clockwise.ordinal());
            spriteCounter = renderer.getBlockIcon(block, world, x, y, z, counterclockwise.ordinal());
        }


        leftU = spriteClock.getMinU();
        rightU = spriteClock.getMaxU();
        topV = spriteClock.getMinV();
        botV = spriteClock.getMaxV();

        lengthU = rightU - leftU;
        lengthV = botV - topV;

        midU = (rightU + leftU) / 2.0;
        midV = (botV + topV) / 2.0;

        cornerU = midU + axisOffsetHalf * lengthU;

        double frontEdgeU = midU + frontEdgeToCenter * axisOffset * lengthU;
        double backEdgeU = midU + backEdgeToCenter * axisOffset * lengthU;

        double frontV = midV - axisOffsetHalf * lengthV;
        double backV = midV + axisOffsetConnector * lengthV;

        // Clock Side
        tess.addVertexWithUV(frontClockCenterX, frontClockCenterY, frontClockCenterZ, midU, frontV);
        tess.addVertexWithUV(backClockCenterX, backClockCenterY, backClockCenterZ, midU, backV);
        tess.addVertexWithUV(backClockEdgeX, backClockEdgeY, backClockEdgeZ, backEdgeU, backV);
        tess.addVertexWithUV(frontClockEdgeX, frontClockEdgeY, frontClockEdgeZ, frontEdgeU, frontV);

        // Clock Diag
        tess.addVertexWithUV(frontClockEdgeX, frontClockEdgeY, frontClockEdgeZ, frontEdgeU, frontV);
        tess.addVertexWithUV(backClockEdgeX, backClockEdgeY, backClockEdgeZ, backEdgeU, backV);
        tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, cornerU, backV);
        tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, cornerU, frontV);

        didRender = true;

        leftU = spriteCounter.getMinU();
        rightU = spriteCounter.getMaxU();
        topV = spriteCounter.getMinV();
        botV = spriteCounter.getMaxV();

        lengthU = rightU - leftU;
        lengthV = botV - topV;

        midU = (rightU + leftU) / 2.0;
        midV = (botV + topV) / 2.0;

        cornerU = midU - axisOffsetHalf * lengthU;

        frontEdgeU = midU - frontEdgeToCenter * axisOffset * lengthU;
        backEdgeU = midU - backEdgeToCenter * axisOffset * lengthU;

        frontV = midV - lengthV * axisOffsetHalf;
        backV = midV + lengthV * axisOffsetConnector;

        // Counter Side
        tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, cornerU, frontV);
        tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, cornerU, backV);
        tess.addVertexWithUV(backCounterEdgeX, backCounterEdgeY, backCounterEdgeZ, backEdgeU, backV);
        tess.addVertexWithUV(frontCounterEdgeX, frontCounterEdgeY, frontCounterEdgeZ, frontEdgeU, frontV);

        // Counter Diag
        tess.addVertexWithUV(frontCounterEdgeX, frontCounterEdgeY, frontCounterEdgeZ, frontEdgeU, frontV);
        tess.addVertexWithUV(backCounterEdgeX, backCounterEdgeY, backCounterEdgeZ, backEdgeU, backV);
        tess.addVertexWithUV(backCounterCenterX, backCounterCenterY, backCounterCenterZ, midU, backV);
        tess.addVertexWithUV(frontCounterCenterX, frontCounterCenterY, frontCounterCenterZ, midU, frontV);

        didRender = true;

        return didRender;
    }
}
