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

        return debugRenderHalfLog(world, x, y, z, block, renderer, axis);
    }

    private static boolean debugRenderHalfLog(IBlockAccess world, int x, int y, int z, Block block,
          RenderBlocks renderer, ForgeDirection axis) {

        final ForgeDirection[] sides = UVPlane.getSides(axis);
        final ForgeDirection side = sides[y % sides.length];
        return RenderRoundHalfLog(world, x, y, z, block, renderer, axis, side);
    }

    private static ForgeDirection determineLogAxis(IBlockAccess world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);

        return switch ((meta >> 2) & 3) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            default -> ForgeDirection.UP;
        };
    }

    /// Renders a log block with all rounded corners
    private static boolean RenderRoundLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
                                          ForgeDirection axis) {

        boolean didRender = false;

        final ForgeDirection[] sides = UVPlane.getSides(axis);
        for (final ForgeDirection clock : sides) {
            final ForgeDirection counter = axis.getRotation(clock);
            didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, clock, counter);
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
//        double leftU;
//        double rightU;
//        double topV;
//        double botV;
//
//        double midU;
//        double midV;
//
//        double lengthU;
//        double lengthV;
//
//        double halfU;
//        double halfV;
//
//        UVPlane plane;
//
//        double offsetU;
//        double offsetV;
//
//        double squareLeftU;
//        double squareRightU;
//        double squareTopV;
//        double squareBotV;
//
//        final IIcon spriteFront = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
//        leftU = spriteFront.getMinU();
//        rightU = spriteFront.getMaxU();
//        topV = spriteFront.getMinV();
//        botV = spriteFront.getMaxV();
//
//        midU = (rightU + leftU) / 2.0;
//        midV = (botV + topV) / 2.0;
//
//        lengthU = rightU - leftU;
//        lengthV = botV - topV;
//
//        halfU = leftU / 2.0;
//        halfV = lengthV / 2.0;
//
//        plane = UVPlane.toUVPlane(axis);
//        offsetU = plane.getOffsetU(side);
//        offsetV = plane.getOffsetV(side);
//
//        squareLeftU = midU - offsetU * halfU;
//        squareRightU = midU + offsetU * halfU;
//
//        squareTopV = midV - offsetV * halfV;
//        squareBotV = midV + offsetV * halfV;

        // Faces
        tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, 0.25, 0.25);
        tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, 0.25, 0.26);
        tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, 0.26, 0.26);
        tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, 0.26, 0.25);
        didRender = true;

        final IIcon spriteBack = renderer.getBlockIcon(block, world, x, y, z, axis.getOpposite().ordinal());

        tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, 0.25, 0.25);
        tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, 0.25, 0.26);
        tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, 0.26, 0.26);
        tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, 0.26, 0.25);
        didRender = true;

        // Sides
        tess.addVertexWithUV(frontCenterCounterX, frontCenterCounterY, frontCenterCounterZ, 0.25, 0.25);
        tess.addVertexWithUV(backCenterCounterX, backCenterCounterY, backCenterCounterZ, 0.25, 0.26);
        tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, 0.26, 0.26);
        tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, 0.26, 0.25);
        didRender = true;

        tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, 0.26, 0.25);
        tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, 0.26, 0.26);
        tess.addVertexWithUV(backCenterClockX, backCenterClockY, backCenterClockZ, 0.25, 0.26);
        tess.addVertexWithUV(frontCenterClockX, frontCenterClockY, frontCenterClockZ, 0.25, 0.25);
        didRender = true;

        // Back
        tess.addVertexWithUV(frontCornerCounterX, frontCornerCounterY, frontCornerCounterZ, 0.26, 0.25);
        tess.addVertexWithUV(backCornerCounterX, backCornerCounterY, backCornerCounterZ, 0.26, 0.26);
        tess.addVertexWithUV(backCornerClockX, backCornerClockY, backCornerClockZ, 0.25, 0.26);
        tess.addVertexWithUV(frontCornerClockX, frontCornerClockY, frontCornerClockZ, 0.25, 0.25);
        didRender = true;

        /// Round corners
        didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, side, counterclockwise);
        didRender = didRender | RenderRoundCorner(world, x, y, z, block, renderer, axis, counterclockwise.getOpposite(), side);

        return didRender;
    }

    private static boolean RenderRoundCorner(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, ForgeDirection clockwise, ForgeDirection counterclockwise) {

        boolean didRender = false;
        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        final double axisX = (axis.offsetX / 2.0);
        final double axisY = (axis.offsetY / 2.0);
        final double axisZ = (axis.offsetZ / 2.0);

        final double clockX = (clockwise.offsetX / 2.0);
        final double clockY = (clockwise.offsetY / 2.0);
        final double clockZ = (clockwise.offsetZ / 2.0);

        final double counterX = (counterclockwise.offsetX / 2.0);
        final double counterY = (counterclockwise.offsetY / 2.0);
        final double counterZ = (counterclockwise.offsetZ / 2.0);

        // TODO: Radius definer
        final double frontRadius = Config.roundLogs.INSTANCE.getRadiusSmall();
        final double backRadius = Config.roundLogs.INSTANCE.getRadiusSmall();

        final double frontEdgeToCenter = 0.5 - frontRadius;
        final double frontDiagToCenter = (frontRadius / 2.0) + frontEdgeToCenter;

        final double backEdgeToCenter = 0.5 - backRadius;
        final double backDiagToCenter = (backRadius / 2.0) + backEdgeToCenter;

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
        final double backCenterX = midX - axisX;
        final double backCenterY = midY - axisY;
        final double backCenterZ = midZ - axisZ;

        final double backClockCenterX = backCenterX + clockX;
        final double backClockCenterY = backCenterY + clockY;
        final double backClockCenterZ = backCenterZ + clockZ;

        final double backCounterCenterX = backCenterX + counterX;
        final double backCounterCenterY = backCenterY + counterY;
        final double backCounterCenterZ = backCenterZ + counterZ;

        final double backClockEdgeX = backClockCenterX + counterclockwise.offsetX * backEdgeToCenter;
        final double backClockEdgeY = backClockCenterY + counterclockwise.offsetY * backEdgeToCenter;
        final double backClockEdgeZ = backClockCenterZ + counterclockwise.offsetZ * backEdgeToCenter;

        final double backCounterEdgeX = backCounterCenterX + clockwise.offsetX * backEdgeToCenter;
        final double backCounterEdgeY = backCounterCenterY + clockwise.offsetY * backEdgeToCenter;
        final double backCounterEdgeZ = backCounterCenterZ + clockwise.offsetZ * backEdgeToCenter;

        final double backDiagX = backCenterX + backDiagToCenter * (clockwise.offsetX + counterclockwise.offsetX);
        final double backDiagY = backCenterY + backDiagToCenter * (clockwise.offsetY + counterclockwise.offsetY);
        final double backDiagZ = backCenterZ + backDiagToCenter * (clockwise.offsetZ + counterclockwise.offsetZ);

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

        /// Sides
        final IIcon spriteClock = renderer.getBlockIcon(block, world, x, y, z, clockwise.ordinal());

        leftU = spriteClock.getMinU();
        rightU = spriteClock.getMaxU();
        topV = spriteClock.getMinV();
        botV = spriteClock.getMaxV();

        lengthU = (rightU - leftU);
        midU = (rightU + leftU) / 2.0;

        double frontEdgeU = midU + frontEdgeToCenter * lengthU;
        double backEdgeU = midU + backEdgeToCenter * lengthU;

        // Clock Side
        tess.addVertexWithUV(frontClockCenterX, frontClockCenterY, frontClockCenterZ, midU, topV);
        tess.addVertexWithUV(backClockCenterX, backClockCenterY, backClockCenterZ, midU, botV);
        tess.addVertexWithUV(backClockEdgeX, backClockEdgeY, backClockEdgeZ, backEdgeU, botV);
        tess.addVertexWithUV(frontClockEdgeX, frontClockEdgeY, frontClockEdgeZ, frontEdgeU, topV);

        // Clock Diag
        tess.addVertexWithUV(frontClockEdgeX, frontClockEdgeY, frontClockEdgeZ, frontEdgeU, topV);
        tess.addVertexWithUV(backClockEdgeX, backClockEdgeY, backClockEdgeZ, backEdgeU, botV);
        tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, rightU, botV);
        tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, rightU, topV);

        didRender = true;

        final IIcon spriteCounter = renderer.getBlockIcon(block, world, x, y, z, counterclockwise.ordinal());

        leftU = spriteClock.getMinU();
        rightU = spriteClock.getMaxU();
        topV = spriteClock.getMinV();
        botV = spriteClock.getMaxV();

        lengthU = (rightU - leftU);
        midU = (rightU + leftU) / 2.0;

        frontEdgeU = midU - frontEdgeToCenter * lengthU;
        backEdgeU = midU - backEdgeToCenter * lengthU;

        // Counter Side
        tess.addVertexWithUV(frontDiagX, frontDiagY, frontDiagZ, leftU, topV);
        tess.addVertexWithUV(backDiagX, backDiagY, backDiagZ, leftU, botV);
        tess.addVertexWithUV(backCounterEdgeX, backCounterEdgeY, backCounterEdgeZ, backEdgeU, botV);
        tess.addVertexWithUV(frontCounterEdgeX, frontCounterEdgeY, frontCounterEdgeZ, frontEdgeU, topV);

        // Counter Diag
        tess.addVertexWithUV(frontCounterEdgeX, frontCounterEdgeY, frontCounterEdgeZ, frontEdgeU, topV);
        tess.addVertexWithUV(backCounterEdgeX, backCounterEdgeY, backCounterEdgeZ, backEdgeU, botV);
        tess.addVertexWithUV(backCounterCenterX, backCounterCenterY, backCounterCenterZ, midU, botV);
        tess.addVertexWithUV(frontCounterCenterX, frontCounterCenterY, frontCounterCenterZ, midU, topV);

        didRender = true;

        return didRender;
    }



}
