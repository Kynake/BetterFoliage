package mods.betterfoliage.client.render.features;

import mods.betterfoliage.client.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.utils.MathUtils;

public class LogRenderer extends BlockRenderer {

    private static final ForgeDirection[] VERTICAL_LOG_SIDES = { ForgeDirection.NORTH, ForgeDirection.SOUTH,
        ForgeDirection.EAST, ForgeDirection.WEST };

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

        RenderLog(world, x, y, z, block, renderer, ForgeDirection.UP);

        return true;
    }

    private static void RenderLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, ForgeDirection axis) {
        final Tessellator tess = Tessellator.instance;
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        final double radiusSmall = Config.roundLogs.INSTANCE.getRadiusSmall();

        final double diagEdgeToCenterSmall = 0.5 - radiusSmall;
        final double diagMiddleToCenterSmall = (radiusSmall / 2.0) + diagEdgeToCenterSmall;

        /// Top Octagon Sprite
        final IIcon topOctSprite = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
        final double topOctLeftU = topOctSprite.getMinU(), topOctRightU = topOctSprite.getMaxU();
        final double topOctLeftV = topOctSprite.getMinV(), topOctRightV = topOctSprite.getMaxV();

        final double topOctCenterU = (topOctLeftU + topOctRightU) / 2.0;
        final double topOctCenterV = (topOctLeftV + topOctRightV) / 2.0;

        final double topOctLengthU = topOctRightU - topOctLeftU;
        final double topOctLengthV = topOctRightV - topOctLeftV;

        /// Bottom Octagon Sprite
        final IIcon botOctSprite = renderer.getBlockIcon(block, world, x, y, z, axis.getOpposite().ordinal());
        final double botOctLeftU = botOctSprite.getMinU(), botOctRightU = botOctSprite.getMaxU();
        final double botOctLeftV = botOctSprite.getMinV(), botOctRightV = botOctSprite.getMaxV();

        final double botOctCenterU = (botOctLeftU + botOctRightU) / 2.0;
        final double botOctCenterV = (botOctLeftV + botOctRightV) / 2.0;

        final double botOctLengthU = botOctRightU - botOctLeftU;
        final double botOctLengthV = botOctRightV - botOctLeftV;

        final ForgeDirection[] sides = RenderUtils.getAxisSides(axis);
        for (final ForgeDirection sideDir : sides) {
            final ForgeDirection clockDir = sideDir.getRotation(axis);

            /// Axes:
            //                  A axis
            //                  |
            //                 /|\
            //                / * \
            //               |\___/|
            // clockDir <--- * | | |
            //               | |*| | sideDir
            //                \|_|/

            final IIcon sprite = renderer.getBlockIcon(block, world, x, y, z, sideDir.ordinal());

            final double leftU = sprite.getMinU(), rightU = sprite.getMaxU();
            final double topV = sprite.getMinV(), bottomV = sprite.getMaxV();

            final double edgeLeftU = MathUtils.lerp(leftU, rightU, radiusSmall);
            final double edgeRightU = MathUtils.lerp(leftU, rightU, 1 - radiusSmall);

            /// 3D coords (6 vertexes per side, 2 sides, 3 axes per vertex = 6 * 2 * 3 = 36)
            //      *       --> topCenter
            //     / \
            //    /   \
            //   /     \
            //  *   *    *  --> topDiagLeft, topDiagRight | botCenter (in the middle)
            //  |\ / \ /|
            //  | *-*-* |   --> topEdgeLeft, topEdgeCenter, topEdgeRight
            //  | |   | |
            //  |/|   |\|
            //  * |   | *   --> botDiagLeft, botDiagRight
            //   \|   |/
            //    *-*-*     --> botEdgeLeft, botEdgeCenter, botEdgeRight

            /// Top
            final double topCenterX, topCenterY, topCenterZ;
            topCenterX = midX + axis.offsetX / 2.0;
            topCenterY = midY + axis.offsetY / 2.0;
            topCenterZ = midZ + axis.offsetZ / 2.0;

            final double topDiagLeftX, topDiagLeftY, topDiagLeftZ;
            topDiagLeftX = topCenterX + diagMiddleToCenterSmall * (sideDir.offsetX + clockDir.offsetX);
            topDiagLeftY = topCenterY + diagMiddleToCenterSmall * (sideDir.offsetY + clockDir.offsetY);
            topDiagLeftZ = topCenterZ + diagMiddleToCenterSmall * (sideDir.offsetZ + clockDir.offsetZ);

            final double topDiagRightX, topDiagRightY, topDiagRightZ;
            topDiagRightX = topCenterX + diagMiddleToCenterSmall * (sideDir.offsetX + clockDir.getOpposite().offsetX);
            topDiagRightY = topCenterY + diagMiddleToCenterSmall * (sideDir.offsetY + clockDir.getOpposite().offsetY);
            topDiagRightZ = topCenterZ + diagMiddleToCenterSmall * (sideDir.offsetZ + clockDir.getOpposite().offsetZ);

            final double topEdgeCenterX, topEdgeCenterY, topEdgeCenterZ;
            topEdgeCenterX = topCenterX + sideDir.offsetX / 2.0;
            topEdgeCenterY = topCenterY + sideDir.offsetY / 2.0;
            topEdgeCenterZ = topCenterZ + sideDir.offsetZ / 2.0;

            final double topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ;
            topEdgeLeftX = topEdgeCenterX + diagEdgeToCenterSmall * clockDir.offsetX;
            topEdgeLeftY = topEdgeCenterY + diagEdgeToCenterSmall * clockDir.offsetY;
            topEdgeLeftZ = topEdgeCenterZ + diagEdgeToCenterSmall * clockDir.offsetZ;

            final double topEdgeRightX, topEdgeRightY, topEdgeRightZ;
            topEdgeRightX = topEdgeCenterX - diagEdgeToCenterSmall * clockDir.offsetX;
            topEdgeRightY = topEdgeCenterY - diagEdgeToCenterSmall * clockDir.offsetY;
            topEdgeRightZ = topEdgeCenterZ - diagEdgeToCenterSmall * clockDir.offsetZ;

            /// Bottom
            final double botCenterX, botCenterY, botCenterZ;
            botCenterX = midX - axis.offsetX / 2.0;
            botCenterY = midY - axis.offsetY / 2.0;
            botCenterZ = midZ - axis.offsetZ / 2.0;

            final double botDiagLeftX, botDiagLeftY, botDiagLeftZ;
            botDiagLeftX = botCenterX + diagMiddleToCenterSmall * (sideDir.offsetX + clockDir.offsetX);
            botDiagLeftY = botCenterY + diagMiddleToCenterSmall * (sideDir.offsetY + clockDir.offsetY);
            botDiagLeftZ = botCenterZ + diagMiddleToCenterSmall * (sideDir.offsetZ + clockDir.offsetZ);

            final double botDiagRightX, botDiagRightY, botDiagRightZ;
            botDiagRightX = botCenterX + diagMiddleToCenterSmall * (sideDir.offsetX + clockDir.getOpposite().offsetX);
            botDiagRightY = botCenterY + diagMiddleToCenterSmall * (sideDir.offsetY + clockDir.getOpposite().offsetY);
            botDiagRightZ = botCenterZ + diagMiddleToCenterSmall * (sideDir.offsetZ + clockDir.getOpposite().offsetZ);

            final double botEdgeCenterX, botEdgeCenterY, botEdgeCenterZ;
            botEdgeCenterX = botCenterX + sideDir.offsetX / 2.0;
            botEdgeCenterY = botCenterY + sideDir.offsetY / 2.0;
            botEdgeCenterZ = botCenterZ + sideDir.offsetZ / 2.0;

            final double botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ;
            botEdgeLeftX = botEdgeCenterX + diagEdgeToCenterSmall * clockDir.offsetX;
            botEdgeLeftY = botEdgeCenterY + diagEdgeToCenterSmall * clockDir.offsetY;
            botEdgeLeftZ = botEdgeCenterZ + diagEdgeToCenterSmall * clockDir.offsetZ;

            final double botEdgeRightX, botEdgeRightY, botEdgeRightZ;
            botEdgeRightX = botEdgeCenterX - diagEdgeToCenterSmall * clockDir.offsetX;
            botEdgeRightY = botEdgeCenterY - diagEdgeToCenterSmall * clockDir.offsetY;
            botEdgeRightZ = botEdgeCenterZ - diagEdgeToCenterSmall * clockDir.offsetZ;

            /// UV Mapping
            final double localLeftU, localLeftV;
            final double localEdgeU, localEdgeV;
            final double localRightU, localRightV;

            /// DRAW

            /// Left Diagonal
            tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, leftU, topV);
            tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, leftU, bottomV);
            tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, edgeLeftU, bottomV);
            tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, edgeLeftU, topV);

            /// Side
            tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, edgeLeftU, topV);
            tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, edgeLeftU, bottomV);
            tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, edgeRightU, bottomV);
            tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, edgeRightU, topV);

            /// Right Diagonal
            tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, edgeRightU, topV);
            tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, edgeRightU, bottomV);
            tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, rightU, bottomV);
            tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, rightU, topV);

            /// Top Left
//            tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topOctCenterU, topOctCenterV);
//            tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, topLeftU, topLeftV);
//            tess.addVertexWithUV(leftX, y + 1, leftZ, topEdgeLeftU, topEdgeLeftV);
//            tess.addVertexWithUV(midFaceX, y + 1, midFaceZ, topMiddleU, topMiddleV);

//            /// Top Right
//            tess.addVertexWithUV(midX, y + 1, midZ, upCenterU, upCenterV);
//            tess.addVertexWithUV(midFaceX, y + 1, midFaceZ, topMiddleU, topMiddleV);
//            tess.addVertexWithUV(rightX, y + 1, rightZ, topEdgeRightU, topEdgeRightV);
//            tess.addVertexWithUV(diagRightX, y + 1, diagRightZ, topRightU, topRightV);
//
//            /// Bottom Left
//            tess.addVertexWithUV(midX, y, midZ, downCenterU, downCenterV);
//            tess.addVertexWithUV(midFaceX, y, midFaceZ, botMiddleU, botMiddleV);
//            tess.addVertexWithUV(leftX, y, leftZ, botEdgeLeftU, botEdgeLeftV);
//            tess.addVertexWithUV(diagLeftX, y, diagLeftZ, botLeftU, botLeftV);
//
//            /// Bottom Right
//            tess.addVertexWithUV(midX, y, midZ, downCenterU, downCenterV);
//            tess.addVertexWithUV(diagRightX, y, diagRightZ, botRightU, botRightV);
//            tess.addVertexWithUV(rightX, y, rightZ, botEdgeRightU, botEdgeRightV);
//            tess.addVertexWithUV(midFaceX, y, midFaceZ, botMiddleU, botMiddleV);
        }
    }

    private static void RenderVerticalLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
        final Tessellator tess = Tessellator.instance;
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midZ = z + 0.5;

        final double radius = Config.roundLogs.INSTANCE.getRadiusSmall();

        final IIcon upSprite = renderer.getBlockIcon(block, world, x, y, z, ForgeDirection.UP.ordinal());
        final double upLeftU = upSprite.getMinU(), upRightU = upSprite.getMaxU();
        final double upTopV = upSprite.getMinV(), upBottomV = upSprite.getMaxV();
        final double upCenterU = (upLeftU + upRightU) / 2.0, upCenterV = (upTopV + upBottomV) / 2.0;
        final double upDistU = upRightU - upLeftU, upDistV = upBottomV - upTopV;

        final IIcon downSprite = renderer.getBlockIcon(block, world, x, y, z, ForgeDirection.DOWN.ordinal());
        final double downLeftU = downSprite.getMinU(), downRightU = downSprite.getMaxU();
        final double downTopV = downSprite.getMinV(), downBottomV = downSprite.getMaxV();
        final double downCenterU = (downLeftU + downRightU) / 2.0, downCenterV = (downTopV + downBottomV) / 2.0;
        final double downDistU = downRightU - downLeftU, downDistV = downBottomV - downTopV;

        final double edgeFromCenter = 0.5 - radius;
        final double diagFromCenter = (radius / 2.0) + edgeFromCenter;

        for (final ForgeDirection dir : VERTICAL_LOG_SIDES) {
            final ForgeDirection clockDir = dir.getRotation(ForgeDirection.UP);

            final IIcon sprite = renderer.getBlockIcon(block, world, x, y, z, dir.ordinal());

            final double leftU = sprite.getMinU(), rightU = sprite.getMaxU();
            final double topV = sprite.getMinV(), bottomV = sprite.getMaxV();

            final double edgeLeftU = MathUtils.lerp(leftU, rightU, radius);
            final double edgeRightU = MathUtils.lerp(leftU, rightU, 1 - radius);

            final double leftX, rightX, leftZ, rightZ;
            final double diagLeftX, diagRightX, diagLeftZ, diagRightZ;
            final double midFaceX, midFaceZ;

            final double topLeftU, topLeftV;
            final double topEdgeLeftU, topEdgeLeftV;
            final double topMiddleU, topMiddleV;
            final double topEdgeRightU, topEdgeRightV;
            final double topRightU, topRightV;

            final double botLeftU, botLeftV;
            final double botEdgeLeftU, botEdgeLeftV;
            final double botMiddleU, botMiddleV;
            final double botEdgeRightU, botEdgeRightV;
            final double botRightU, botRightV;

            if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
                leftZ = midFaceZ = rightZ = z + (1 + dir.offsetZ) / 2.0;
                diagLeftZ = diagRightZ = midZ + diagFromCenter * dir.offsetZ;

                midFaceX = midX;

                final double edgeDist = edgeFromCenter * clockDir.offsetX;
                leftX = midX + edgeDist;
                rightX = midX - edgeDist;

                final double diagDist = diagFromCenter * clockDir.offsetX;
                diagLeftX = midX + diagDist;
                diagRightX = midX - diagDist;

                topLeftU = upCenterU + upDistU / 2 * clockDir.offsetX;
                topEdgeLeftU = upCenterU + upDistU * edgeDist;
                topMiddleU = upCenterU;
                topEdgeRightU = upCenterU - upDistU * edgeDist;
                topRightU = upCenterU - upDistU / 2 * clockDir.offsetX;

                topLeftV = topEdgeLeftV = topMiddleV = topEdgeRightV = topRightV = upCenterV
                    + upDistV / 2 * dir.offsetZ;

                ///
                botLeftU = downCenterU + downDistU / 2 * clockDir.offsetX;
                botEdgeLeftU = downCenterU + downDistU * edgeDist;
                botMiddleU = downCenterU;
                botEdgeRightU = downCenterU - downDistU * edgeDist;
                botRightU = downCenterU - downDistU / 2 * clockDir.offsetX;

                botLeftV = botEdgeLeftV = botMiddleV = botEdgeRightV = botRightV = downCenterV
                    - downDistV / 2 * dir.offsetZ;

            } else {
                leftX = midFaceX = rightX = x + (1 + dir.offsetX) / 2.0;
                diagLeftX = diagRightX = x + 0.5 + diagFromCenter * dir.offsetX;

                midFaceZ = midZ;

                final double edgeDist = edgeFromCenter * clockDir.offsetZ;
                leftZ = midZ + edgeDist;
                rightZ = midZ - edgeDist;

                final double diagDist = diagFromCenter * clockDir.offsetZ;
                diagLeftZ = midZ + diagDist;
                diagRightZ = midZ - diagDist;

                topLeftV = upCenterV + upDistV / 2 * clockDir.offsetZ;
                topEdgeLeftV = upCenterV + upDistV * edgeDist;
                topMiddleV = upCenterV;
                topEdgeRightV = upCenterV - upDistV * edgeDist;
                topRightV = upCenterV - upDistV / 2 * clockDir.offsetZ;

                topLeftU = topEdgeLeftU = topMiddleU = topEdgeRightU = topRightU = upCenterU
                    + upDistU / 2 * dir.offsetX;

                ///
                botLeftV = downCenterV - downDistV / 2 * clockDir.offsetZ;
                botEdgeLeftV = downCenterV - downDistV * edgeDist;
                botMiddleV = downCenterV;
                botEdgeRightV = downCenterV + downDistV * edgeDist;
                botRightV = downCenterV + downDistV / 2 * clockDir.offsetZ;

                botLeftU = botEdgeLeftU = botMiddleU = botEdgeRightU = botRightU = downCenterU
                    + downDistU / 2 * dir.offsetX;

            }

            /// Left Diagonal
            tess.addVertexWithUV(diagLeftX, y + 1, diagLeftZ, leftU, topV);
            tess.addVertexWithUV(diagLeftX, y, diagLeftZ, leftU, bottomV);
            tess.addVertexWithUV(leftX, y, leftZ, edgeLeftU, bottomV);
            tess.addVertexWithUV(leftX, y + 1, leftZ, edgeLeftU, topV);

            /// Side
            tess.addVertexWithUV(leftX, y + 1, leftZ, edgeLeftU, topV);
            tess.addVertexWithUV(leftX, y, leftZ, edgeLeftU, bottomV);
            tess.addVertexWithUV(rightX, y, rightZ, edgeRightU, bottomV);
            tess.addVertexWithUV(rightX, y + 1, rightZ, edgeRightU, topV);

            /// Right Diagonal
            tess.addVertexWithUV(rightX, y + 1, rightZ, edgeRightU, topV);
            tess.addVertexWithUV(rightX, y, rightZ, edgeRightU, bottomV);
            tess.addVertexWithUV(diagRightX, y, diagRightZ, rightU, bottomV);
            tess.addVertexWithUV(diagRightX, y + 1, diagRightZ, rightU, topV);

            /// Top Left
            tess.addVertexWithUV(midX, y + 1, midZ, upCenterU, upCenterV);
            tess.addVertexWithUV(diagLeftX, y + 1, diagLeftZ, topLeftU, topLeftV);
            tess.addVertexWithUV(leftX, y + 1, leftZ, topEdgeLeftU, topEdgeLeftV);
            tess.addVertexWithUV(midFaceX, y + 1, midFaceZ, topMiddleU, topMiddleV);

            /// Top Right
            tess.addVertexWithUV(midX, y + 1, midZ, upCenterU, upCenterV);
            tess.addVertexWithUV(midFaceX, y + 1, midFaceZ, topMiddleU, topMiddleV);
            tess.addVertexWithUV(rightX, y + 1, rightZ, topEdgeRightU, topEdgeRightV);
            tess.addVertexWithUV(diagRightX, y + 1, diagRightZ, topRightU, topRightV);

            /// Bottom Left
            tess.addVertexWithUV(midX, y, midZ, downCenterU, downCenterV);
            tess.addVertexWithUV(midFaceX, y, midFaceZ, botMiddleU, botMiddleV);
            tess.addVertexWithUV(leftX, y, leftZ, botEdgeLeftU, botEdgeLeftV);
            tess.addVertexWithUV(diagLeftX, y, diagLeftZ, botLeftU, botLeftV);

            /// Bottom Right
            tess.addVertexWithUV(midX, y, midZ, downCenterU, downCenterV);
            tess.addVertexWithUV(diagRightX, y, diagRightZ, botRightU, botRightV);
            tess.addVertexWithUV(rightX, y, rightZ, botEdgeRightU, botEdgeRightV);
            tess.addVertexWithUV(midFaceX, y, midFaceZ, botMiddleU, botMiddleV);
        }
    }
}
