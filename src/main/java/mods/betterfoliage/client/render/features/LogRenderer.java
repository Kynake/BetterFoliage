package mods.betterfoliage.client.render.features;

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
import mods.betterfoliage.utils.MathUtils;

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
        return RenderRoundLog(world, x, y, z, block, renderer, axis);
    }

    private static ForgeDirection determineLogAxis(IBlockAccess world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);

        return switch ((meta >> 2) & 3) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.NORTH;
            default -> ForgeDirection.UP;
        };
    }

    /// Renders a log block as round on all sides
    private static boolean RenderRoundLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis) {
        boolean didRender = false;

        final Tessellator tess = Tessellator.instance;
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        // Round log always uses small radius
        final double radius = Config.roundLogs.INSTANCE.getRadiusSmall();

        final double diagEdgeToCenter = 0.5 - radius;
        final double diagMiddleToCenter = (radius / 2.0) + diagEdgeToCenter;

        /// Top Octagon Sprite
        final IIcon topOctSprite = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
        final double topOctLeftU = topOctSprite.getMinU(), topOctRightU = topOctSprite.getMaxU();
        final double topOctTopV = topOctSprite.getMinV(), topOctBotV = topOctSprite.getMaxV();

        final double topOctCenterU = (topOctLeftU + topOctRightU) / 2.0;
        final double topOctCenterV = (topOctTopV + topOctBotV) / 2.0;

        final double topOctLengthU = topOctRightU - topOctLeftU;
        final double topOctLengthV = topOctTopV - topOctBotV;

        /// Bottom Octagon Sprite
        final int bottomSide = axis.getOpposite()
            .ordinal();
        final IIcon botOctSprite = renderer.getBlockIcon(block, world, x, y, z, bottomSide);
        final double botOctLeftU = botOctSprite.getMinU(), botOctRightU = botOctSprite.getMaxU();
        final double botOctTopV = botOctSprite.getMinV(), botOctBotV = botOctSprite.getMaxV();

        final double botOctCenterU = (botOctLeftU + botOctRightU) / 2.0;
        final double botOctCenterV = (botOctTopV + botOctBotV) / 2.0;

        final double botOctLengthU = botOctRightU - botOctLeftU;
        final double botOctLengthV = botOctBotV - botOctTopV;

        final ForgeDirection[] sides = RenderUtils.getAxisSides(axis);
        for (int i = 0; i < sides.length; i++) {
            final ForgeDirection sideDir = sides[i];
            final ForgeDirection clockDir = sideDir.getRotation(axis);
            final ForgeDirection oppDir = clockDir.getOpposite();

            /// UV Mapping
            final double topLeftU, topLeftV;
            final double topLeftEdgeU, topLeftEdgeV;
            final double topEdgeU, topEdgeV;
            final double topRightEdgeU, topRightEdgeV;
            final double topRightU, topRightV;

            final double botLeftU, botLeftV;
            final double botLeftEdgeU, botLeftEdgeV;
            final double botEdgeU, botEdgeV;
            final double botRightEdgeU, botRightEdgeV;
            final double botRightU, botRightV;

            final double topRadiusDist = radius * (clockDir.offsetX + clockDir.offsetY + clockDir.offsetZ);

            switch (i) {
                case 0:
                    topLeftU = topOctRightU;
                    topLeftV = topOctTopV;
                    topEdgeU = topOctCenterU;
                    topEdgeV = topOctTopV;
                    topRightU = topOctLeftU;
                    topRightV = topOctTopV;

                    topLeftEdgeU = topEdgeU + topOctLengthU * topRadiusDist;
                    topLeftEdgeV = topOctTopV;

                    topRightEdgeU = topEdgeU - topOctLengthU * topRadiusDist;
                    topRightEdgeV = topOctTopV;

                    botLeftU = botOctRightU;
                    botLeftV = botOctBotV;
                    botEdgeU = botOctCenterU;
                    botEdgeV = botOctBotV;
                    botRightU = botOctLeftU;
                    botRightV = botOctBotV;

                    botLeftEdgeU = botEdgeU + botOctLengthU * topRadiusDist;
                    botLeftEdgeV = botOctBotV;

                    botRightEdgeU = botEdgeU - botOctLengthU * topRadiusDist;
                    botRightEdgeV = botOctBotV;
                    break;

                case 1:
                    topLeftU = topOctRightU;
                    topLeftV = topOctBotV;
                    topEdgeU = topOctRightU;
                    topEdgeV = topOctCenterV;
                    topRightU = topOctRightU;
                    topRightV = topOctTopV;

                    topLeftEdgeU = topOctRightU;
                    topLeftEdgeV = topEdgeV - topOctLengthV * topRadiusDist;

                    topRightEdgeU = topOctRightU;
                    topRightEdgeV = topEdgeV + topOctLengthV * topRadiusDist;

                    botLeftU = botOctRightU;
                    botLeftV = botOctTopV;
                    botEdgeU = botOctRightU;
                    botEdgeV = botOctCenterV;
                    botRightU = botOctRightU;
                    botRightV = botOctBotV;

                    botLeftEdgeU = botOctRightU;
                    botLeftEdgeV = botEdgeV - botOctLengthV * topRadiusDist;

                    botRightEdgeU = botOctRightU;
                    botRightEdgeV = botEdgeV + botOctLengthV * topRadiusDist;
                    break;

                case 2:
                    topLeftU = topOctLeftU;
                    topLeftV = topOctBotV;
                    topEdgeU = topOctCenterU;
                    topEdgeV = topOctBotV;
                    topRightU = topOctRightU;
                    topRightV = topOctBotV;

                    topLeftEdgeU = topEdgeU + topOctLengthU * topRadiusDist;
                    topLeftEdgeV = topOctBotV;

                    topRightEdgeU = topEdgeU - topOctLengthU * topRadiusDist;
                    topRightEdgeV = topOctBotV;

                    botLeftU = botOctLeftU;
                    botLeftV = botOctTopV;
                    botEdgeU = botOctCenterU;
                    botEdgeV = botOctTopV;
                    botRightU = botOctRightU;
                    botRightV = botOctTopV;

                    botLeftEdgeU = botEdgeU + botOctLengthU * topRadiusDist;
                    botLeftEdgeV = botOctTopV;

                    botRightEdgeU = botEdgeU - botOctLengthU * topRadiusDist;
                    botRightEdgeV = botOctTopV;
                    break;

                case 3:
                    topLeftU = topOctLeftU;
                    topLeftV = topOctTopV;
                    topEdgeU = topOctLeftU;
                    topEdgeV = topOctCenterV;
                    topRightU = topOctLeftU;
                    topRightV = topOctBotV;

                    topLeftEdgeU = topOctLeftU;
                    topLeftEdgeV = topEdgeV - topOctLengthV * topRadiusDist;

                    topRightEdgeU = topOctLeftU;
                    topRightEdgeV = topEdgeV + topOctLengthV * topRadiusDist;

                    botLeftU = botOctLeftU;
                    botLeftV = botOctBotV;
                    botEdgeU = botOctLeftU;
                    botEdgeV = botOctCenterV;
                    botRightU = botOctLeftU;
                    botRightV = botOctTopV;

                    botLeftEdgeU = botOctLeftU;
                    botLeftEdgeV = botEdgeV - botOctLengthV * topRadiusDist;

                    botRightEdgeU = botOctLeftU;
                    botRightEdgeV = botEdgeV + botOctLengthV * topRadiusDist;
                    break;

                default:
                    return false;
            }

            /// Axes:
            // spotless:off
            //                  A axis
            //                  |
            //                 /|\
            //                / * \
            //               |\___/|
            // clockDir <--- * | | |
            //               | |*| | sideDir
            //                \|_|/
            // spotless:on

            final IIcon sprite = renderer.getBlockIcon(block, world, x, y, z, sideDir.ordinal());

            final double leftU = sprite.getMinU(), rightU = sprite.getMaxU();
            final double topV = sprite.getMinV(), bottomV = sprite.getMaxV();

            final double edgeLeftU = MathUtils.lerp(leftU, rightU, radius);
            final double edgeRightU = MathUtils.lerp(leftU, rightU, 1 - radius);

            /// 3D coords (6 vertexes per side, 2 sides, 3 axes per vertex = 6 * 2 * 3 = 36)
            // spotless:off
            //     * --> topCenter
            //    / \
            //   /   \
            //  /     \
            // *   *   * --> topDiagLeft, topDiagRight | botCenter (in the middle)
            // |\ / \ /|
            // | *-*-* | --> topEdgeLeft, topEdgeCenter, topEdgeRight
            // | |   | |
            // |/|   |\|
            // * |   | * --> botDiagLeft, botDiagRight
            //  \|   |/
            //   *-*-* --> botEdgeLeft, botEdgeCenter, botEdgeRight
            // spotless:on

            /// Top
            final double topCenterX = midX + axis.offsetX / 2.0;
            final double topCenterY = midY + axis.offsetY / 2.0;
            final double topCenterZ = midZ + axis.offsetZ / 2.0;

            final double topDiagLeftX = topCenterX + diagMiddleToCenter * (sideDir.offsetX + clockDir.offsetX);
            final double topDiagLeftY = topCenterY + diagMiddleToCenter * (sideDir.offsetY + clockDir.offsetY);
            final double topDiagLeftZ = topCenterZ + diagMiddleToCenter * (sideDir.offsetZ + clockDir.offsetZ);

            final double topDiagRightX = topCenterX + diagMiddleToCenter * (sideDir.offsetX + oppDir.offsetX);
            final double topDiagRightY = topCenterY + diagMiddleToCenter * (sideDir.offsetY + oppDir.offsetY);
            final double topDiagRightZ = topCenterZ + diagMiddleToCenter * (sideDir.offsetZ + oppDir.offsetZ);

            final double topEdgeCenterX = topCenterX + sideDir.offsetX / 2.0;
            final double topEdgeCenterY = topCenterY + sideDir.offsetY / 2.0;
            final double topEdgeCenterZ = topCenterZ + sideDir.offsetZ / 2.0;

            final double edgeX = diagEdgeToCenter * clockDir.offsetX;
            final double edgeY = diagEdgeToCenter * clockDir.offsetY;
            final double edgeZ = diagEdgeToCenter * clockDir.offsetZ;

            final double topEdgeLeftX = topEdgeCenterX + edgeX;
            final double topEdgeLeftY = topEdgeCenterY + edgeY;
            final double topEdgeLeftZ = topEdgeCenterZ + edgeZ;

            final double topEdgeRightX = topEdgeCenterX - edgeX;
            final double topEdgeRightY = topEdgeCenterY - edgeY;
            final double topEdgeRightZ = topEdgeCenterZ - edgeZ;

            /// Bottom
            final double botCenterX = midX - axis.offsetX / 2.0;
            final double botCenterY = midY - axis.offsetY / 2.0;
            final double botCenterZ = midZ - axis.offsetZ / 2.0;

            final double botDiagLeftX = botCenterX + diagMiddleToCenter * (sideDir.offsetX + clockDir.offsetX);
            final double botDiagLeftY = botCenterY + diagMiddleToCenter * (sideDir.offsetY + clockDir.offsetY);
            final double botDiagLeftZ = botCenterZ + diagMiddleToCenter * (sideDir.offsetZ + clockDir.offsetZ);

            final double botDiagRightX = botCenterX + diagMiddleToCenter * (sideDir.offsetX + oppDir.offsetX);
            final double botDiagRightY = botCenterY + diagMiddleToCenter * (sideDir.offsetY + oppDir.offsetY);
            final double botDiagRightZ = botCenterZ + diagMiddleToCenter * (sideDir.offsetZ + oppDir.offsetZ);

            final double botEdgeCenterX = botCenterX + sideDir.offsetX / 2.0;
            final double botEdgeCenterY = botCenterY + sideDir.offsetY / 2.0;
            final double botEdgeCenterZ = botCenterZ + sideDir.offsetZ / 2.0;

            final double botEdgeLeftX = botEdgeCenterX + edgeX;
            final double botEdgeLeftY = botEdgeCenterY + edgeY;
            final double botEdgeLeftZ = botEdgeCenterZ + edgeZ;

            final double botEdgeRightX = botEdgeCenterX - edgeX;
            final double botEdgeRightY = botEdgeCenterY - edgeY;
            final double botEdgeRightZ = botEdgeCenterZ - edgeZ;

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
            tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topOctCenterU, topOctCenterV);
            tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, topLeftU, topLeftV);
            tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, topLeftEdgeU, topLeftEdgeV);
            tess.addVertexWithUV(topEdgeCenterX, topEdgeCenterY, topEdgeCenterZ, topEdgeU, topEdgeV);

            /// Top Right
            tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topOctCenterU, topOctCenterV);
            tess.addVertexWithUV(topEdgeCenterX, topEdgeCenterY, topEdgeCenterZ, topEdgeU, topEdgeV);
            tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, topRightEdgeU, topRightEdgeV);
            tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, topRightU, topRightV);

            /// Bottom Left
            tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botOctCenterU, botOctCenterV);
            tess.addVertexWithUV(botEdgeCenterX, botEdgeCenterY, botEdgeCenterZ, botEdgeU, botEdgeV);
            tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, botLeftEdgeU, botLeftEdgeV);
            tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, botLeftU, botLeftV);

            /// Bottom Right
            tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botOctCenterU, botOctCenterV);
            tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, botRightU, botRightV);
            tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, botRightEdgeU, botRightEdgeV);
            tess.addVertexWithUV(botEdgeCenterX, botEdgeCenterY, botEdgeCenterZ, botEdgeU, botEdgeV);

            didRender = true;
        }

        return didRender;
    }

}
