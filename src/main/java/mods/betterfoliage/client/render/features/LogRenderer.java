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

//        return RenderRoundLog(world, x, y, z, block, renderer, axis);

        return RenderRoundHalfLog(world, x, y, z, block, renderer, axis, RenderUtils.getAxisSides(axis)[0]);
    }

    private static ForgeDirection determineLogAxis(IBlockAccess world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);

        return switch ((meta >> 2) & 3) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.NORTH;
            default -> ForgeDirection.UP;
        };
    }

    /// Renders a log block rounded on all sides
    private static boolean RenderRoundLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis) {
        boolean didRender = false;

        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
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

    ///  Renders a log block with two adjacent rounded corners and two adjacent square corners
    private static boolean RenderRoundHalfLog(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
        ForgeDirection axis, ForgeDirection side) {

        boolean didRender = false;

        final Tessellator tess = Tessellator.instance;

        // TODO: AO for each vertex
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);

        final double midX = x + 0.5;
        final double midY = y + 0.5;
        final double midZ = z + 0.5;

        // Round log always uses small radius
        final double radius = Config.roundLogs.INSTANCE.getRadiusSmall();

        final double diagEdgeToCenter = 0.5 - radius;
        final double diagMiddleToCenter = (radius / 2.0) + diagEdgeToCenter;

        ///////////////////////////////// UVs ///////////////////////////////////
        /// Top Sprite
        final IIcon topSprite = renderer.getBlockIcon(block, world, x, y, z, axis.ordinal());
        final double topLeftU = topSprite.getMinU(), topRightU = topSprite.getMaxU();
        final double topTopV = topSprite.getMinV(), topBotV = topSprite.getMaxV();

        final double topCenterU = (topLeftU + topRightU) / 2.0;
        final double topCenterV = (topTopV + topBotV) / 2.0;

        final double topLengthU = topRightU - topLeftU;
        final double topLengthV = topTopV - topBotV;

        /// Bottom Sprite
        final int bottomSide = axis.getOpposite()
            .ordinal();
        final IIcon botSprite = renderer.getBlockIcon(block, world, x, y, z, bottomSide);
        final double botLeftU = botSprite.getMinU(), botRightU = botSprite.getMaxU();
        final double botTopV = botSprite.getMinV(), botBotV = botSprite.getMaxV();

        final double botCenterU = (botLeftU + botRightU) / 2.0;
        final double botCenterV = (botTopV + botBotV) / 2.0;

        final double botLengthU = botRightU - botLeftU;
        final double botLengthV = botBotV - botTopV;

        final ForgeDirection clockDir = side.getRotation(axis);
        final ForgeDirection oppDir = clockDir.getOpposite();

        /// Side Sprites

        final IIcon sideSprite = renderer.getBlockIcon(block, world, x, y, z, side.ordinal());

        final double leftU = sideSprite.getMinU(), rightU = sideSprite.getMaxU();
        final double topV = sideSprite.getMinV(), bottomV = sideSprite.getMaxV();

        final double edgeLeftU = MathUtils.lerp(leftU, rightU, radius);
        final double edgeRightU = MathUtils.lerp(leftU, rightU, 1 - radius);

        final IIcon leftSprite = renderer.getBlockIcon(block, world, x, y, z, clockDir.ordinal());
        final double leftSideLeftU = leftSprite.getMinU(), leftSideRightU = leftSprite.getMaxU();
        final double leftSideTopV = leftSprite.getMinV(), leftSideBottomV = leftSprite.getMaxV();

        final IIcon rightSprite = renderer.getBlockIcon(block, world, x, y, z, oppDir.ordinal());
        final double rightSideLeftU = rightSprite.getMinU(), rightSideRightU = rightSprite.getMaxU();
        final double rightSideTopV = rightSprite.getMinV(), rightSideBottomV = rightSprite.getMaxV();

        final IIcon backSprite = renderer.getBlockIcon(block, world, x, y, z, side.getOpposite().ordinal());
        final double backSideLeftU = backSprite.getMinU(), backSideRightU = backSprite.getMaxU();
        final double backSideTopV = backSprite.getMinV(), backSideBottomV = backSprite.getMaxV();

        ///////////////////////////////// Verts ///////////////////////////////////

        final double edgeX = diagEdgeToCenter * clockDir.offsetX;
        final double edgeY = diagEdgeToCenter * clockDir.offsetY;
        final double edgeZ = diagEdgeToCenter * clockDir.offsetZ;

        /// Top
        final double topCenterX = midX + axis.offsetX / 2.0;
        final double topCenterY = midY + axis.offsetY / 2.0;
        final double topCenterZ = midZ + axis.offsetZ / 2.0;

        final double topDiagLeftX = topCenterX + diagMiddleToCenter * (side.offsetX + clockDir.offsetX);
        final double topDiagLeftY = topCenterY + diagMiddleToCenter * (side.offsetY + clockDir.offsetY);
        final double topDiagLeftZ = topCenterZ + diagMiddleToCenter * (side.offsetZ + clockDir.offsetZ);

        final double topDiagRightX = topCenterX + diagMiddleToCenter * (side.offsetX + oppDir.offsetX);
        final double topDiagRightY = topCenterY + diagMiddleToCenter * (side.offsetY + oppDir.offsetY);
        final double topDiagRightZ = topCenterZ + diagMiddleToCenter * (side.offsetZ + oppDir.offsetZ);

        final double topEdgeCenterX = topCenterX + side.offsetX / 2.0;
        final double topEdgeCenterY = topCenterY + side.offsetY / 2.0;
        final double topEdgeCenterZ = topCenterZ + side.offsetZ / 2.0;

        final double topEdgeLeftX = topEdgeCenterX + edgeX;
        final double topEdgeLeftY = topEdgeCenterY + edgeY;
        final double topEdgeLeftZ = topEdgeCenterZ + edgeZ;

        final double topEdgeRightX = topEdgeCenterX - edgeX;
        final double topEdgeRightY = topEdgeCenterY - edgeY;
        final double topEdgeRightZ = topEdgeCenterZ - edgeZ;

        final double topLeftSideCenterX = topCenterX + clockDir.offsetX / 2.0;
        final double topLeftSideCenterY = topCenterY + clockDir.offsetY / 2.0;
        final double topLeftSideCenterZ = topCenterZ + clockDir.offsetZ / 2.0;

        final double topLeftSideNearX = topLeftSideCenterX + diagEdgeToCenter * side.offsetX;
        final double topLeftSideNearY = topLeftSideCenterY + diagEdgeToCenter * side.offsetY;
        final double topLeftSideNearZ = topLeftSideCenterZ + diagEdgeToCenter * side.offsetZ;

        final double topLeftSideFarX = topLeftSideCenterX - side.offsetX / 2.0;
        final double topLeftSideFarY = topLeftSideCenterY - side.offsetY / 2.0;
        final double topLeftSideFarZ = topLeftSideCenterZ - side.offsetZ / 2.0;


        final double topRightSideCenterX = topCenterX + oppDir.offsetX / 2.0;
        final double topRightSideCenterY = topCenterY + oppDir.offsetY / 2.0;
        final double topRightSideCenterZ = topCenterZ + oppDir.offsetZ / 2.0;

        final double topRightSideNearX = topRightSideCenterX + diagEdgeToCenter * side.offsetX;
        final double topRightSideNearY = topRightSideCenterY + diagEdgeToCenter * side.offsetY;
        final double topRightSideNearZ = topRightSideCenterZ + diagEdgeToCenter * side.offsetZ;

        final double topRightSideFarX = topRightSideCenterX - side.offsetX / 2.0;
        final double topRightSideFarY = topRightSideCenterY - side.offsetY / 2.0;
        final double topRightSideFarZ = topRightSideCenterZ - side.offsetZ / 2.0;

        /// Bottom
        final double botCenterX = midX - axis.offsetX / 2.0;
        final double botCenterY = midY - axis.offsetY / 2.0;
        final double botCenterZ = midZ - axis.offsetZ / 2.0;

        final double botDiagLeftX = botCenterX + diagMiddleToCenter * (side.offsetX + clockDir.offsetX);
        final double botDiagLeftY = botCenterY + diagMiddleToCenter * (side.offsetY + clockDir.offsetY);
        final double botDiagLeftZ = botCenterZ + diagMiddleToCenter * (side.offsetZ + clockDir.offsetZ);

        final double botDiagRightX = botCenterX + diagMiddleToCenter * (side.offsetX + oppDir.offsetX);
        final double botDiagRightY = botCenterY + diagMiddleToCenter * (side.offsetY + oppDir.offsetY);
        final double botDiagRightZ = botCenterZ + diagMiddleToCenter * (side.offsetZ + oppDir.offsetZ);

        final double botEdgeCenterX = botCenterX + side.offsetX / 2.0;
        final double botEdgeCenterY = botCenterY + side.offsetY / 2.0;
        final double botEdgeCenterZ = botCenterZ + side.offsetZ / 2.0;

        final double botEdgeLeftX = botEdgeCenterX + edgeX;
        final double botEdgeLeftY = botEdgeCenterY + edgeY;
        final double botEdgeLeftZ = botEdgeCenterZ + edgeZ;

        final double botEdgeRightX = botEdgeCenterX - edgeX;
        final double botEdgeRightY = botEdgeCenterY - edgeY;
        final double botEdgeRightZ = botEdgeCenterZ - edgeZ;


        final double botLeftSideCenterX = botCenterX + clockDir.offsetX / 2.0;
        final double botLeftSideCenterY = botCenterY + clockDir.offsetY / 2.0;
        final double botLeftSideCenterZ = botCenterZ + clockDir.offsetZ / 2.0;

        final double botLeftSideNearX = botLeftSideCenterX + diagEdgeToCenter * side.offsetX;
        final double botLeftSideNearY = botLeftSideCenterY + diagEdgeToCenter * side.offsetY;
        final double botLeftSideNearZ = botLeftSideCenterZ + diagEdgeToCenter * side.offsetZ;

        final double botLeftSideFarX = botLeftSideCenterX - side.offsetX / 2.0;
        final double botLeftSideFarY = botLeftSideCenterY - side.offsetY / 2.0;
        final double botLeftSideFarZ = botLeftSideCenterZ - side.offsetZ / 2.0;

        final double botRightSideCenterX = botCenterX + oppDir.offsetX / 2.0;
        final double botRightSideCenterY = botCenterY + oppDir.offsetY / 2.0;
        final double botRightSideCenterZ = botCenterZ + oppDir.offsetZ / 2.0;

        final double botRightSideNearX = botRightSideCenterX + diagEdgeToCenter * side.offsetX;
        final double botRightSideNearY = botRightSideCenterY + diagEdgeToCenter * side.offsetY;
        final double botRightSideNearZ = botRightSideCenterZ + diagEdgeToCenter * side.offsetZ;

        final double botRightSideFarX = botRightSideCenterX - side.offsetX / 2.0;
        final double botRightSideFarY = botRightSideCenterY - side.offsetY / 2.0;
        final double botRightSideFarZ = botRightSideCenterZ - side.offsetZ / 2.0;

        /// DRAW

        // TODO FIX UVs, Hardcoded by side

        /// Top Back
        tess.addVertexWithUV(topLeftSideFarX, topLeftSideFarY, topLeftSideFarZ, topLeftU, topTopV);
        tess.addVertexWithUV(topLeftSideCenterX, topLeftSideCenterY, topLeftSideCenterZ, topLeftU, topCenterV);
        tess.addVertexWithUV(topRightSideCenterX, topRightSideCenterY, topRightSideCenterZ, topRightU, topCenterV);
        tess.addVertexWithUV(topRightSideFarX, topRightSideFarY, topRightSideFarZ, topRightU, topTopV);

        /// Top Left Side
        tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topCenterU, topCenterV);
        tess.addVertexWithUV(topLeftSideCenterX, topLeftSideCenterY, topLeftSideCenterZ, topLeftU, topCenterV);
        tess.addVertexWithUV(topLeftSideNearX, topLeftSideNearY, topLeftSideNearZ, topRightU, topCenterV);
        tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, topRightU, topTopV);

        /// Top Center Left
        tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topCenterU, topCenterV);
        tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, topRightU, topTopV);
        tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, topLeftU, topCenterV);
        tess.addVertexWithUV(topEdgeCenterX, topEdgeCenterY, topEdgeCenterZ, topRightU, topCenterV);

        /// Top Right Side
        tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topCenterU, topCenterV);
        tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, topRightU, topTopV);
        tess.addVertexWithUV(topRightSideNearX, topRightSideNearY, topRightSideNearZ, topRightU, topCenterV);
        tess.addVertexWithUV(topRightSideCenterX, topRightSideCenterY, topRightSideCenterZ, topLeftU, topCenterV);

        /// Top Center Left
        tess.addVertexWithUV(topCenterX, topCenterY, topCenterZ, topCenterU, topCenterV);
        tess.addVertexWithUV(topEdgeCenterX, topEdgeCenterY, topEdgeCenterZ, topRightU, topCenterV);
        tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, topLeftU, topCenterV);
        tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, topRightU, topTopV);

        /// Bottom Back
        tess.addVertexWithUV(botLeftSideCenterX, botLeftSideCenterY, botLeftSideCenterZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botLeftSideFarX, botLeftSideFarY, botLeftSideFarZ, botLeftU, botTopV);
        tess.addVertexWithUV(botRightSideFarX, botRightSideFarY, botRightSideFarZ, botRightU, botTopV);
        tess.addVertexWithUV(botRightSideCenterX, botRightSideCenterY, botRightSideCenterZ, botRightU, botCenterV);

        /// Bottom Left Side
        tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, botRightU, botTopV);
        tess.addVertexWithUV(botLeftSideNearX, botLeftSideNearY, botLeftSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(botLeftSideCenterX, botLeftSideCenterY, botLeftSideCenterZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botCenterU, botCenterV);

        /// Bottom Center Left
        tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, botRightU, botTopV);
        tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botCenterU, botCenterV);
        tess.addVertexWithUV(botEdgeCenterX, botEdgeCenterY, botEdgeCenterZ, botRightU, botCenterV);

        /// Bottom Right Side
        tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, botRightU, botTopV);
        tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botCenterU, botCenterV);
        tess.addVertexWithUV(botRightSideCenterX, botRightSideCenterY, botRightSideCenterZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botRightSideNearX, botRightSideNearY, botRightSideNearZ, botRightU, botCenterV);

        /// Bottom Center Right
        tess.addVertexWithUV(botEdgeCenterX, botEdgeCenterY, botEdgeCenterZ, botRightU, botCenterV);
        tess.addVertexWithUV(botCenterX, botCenterY, botCenterZ, botCenterU, botCenterV);
        tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, botRightU, botTopV);
        tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, botLeftU, botCenterV);


        /// Sides

        /// Backside
        tess.addVertexWithUV(topRightSideFarX, topRightSideFarY, topRightSideFarZ, botRightU, botCenterV);
        tess.addVertexWithUV(botRightSideFarX, botRightSideFarY, botRightSideFarZ, botRightU, botCenterV);
        tess.addVertexWithUV(botLeftSideFarX, botLeftSideFarY, botLeftSideFarZ, botRightU, botTopV);
        tess.addVertexWithUV(topLeftSideFarX, topLeftSideFarY, topLeftSideFarZ, botLeftU, botCenterV);

        /// Left Side
        tess.addVertexWithUV(topLeftSideFarX, topLeftSideFarY, topLeftSideFarZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botLeftSideFarX, botLeftSideFarY, botLeftSideFarZ, botRightU, botTopV);
        tess.addVertexWithUV(botLeftSideNearX, botLeftSideNearY, botLeftSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(topLeftSideNearX, topLeftSideNearY, topLeftSideNearZ, botRightU, botCenterV);

        /// Diag Left
        tess.addVertexWithUV(topLeftSideNearX, topLeftSideNearY, topLeftSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(botLeftSideNearX, botLeftSideNearY, botLeftSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, botRightU, botTopV);
        tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, botLeftU, botCenterV);

        /// Front Left
        tess.addVertexWithUV(topDiagLeftX, topDiagLeftY, topDiagLeftZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botDiagLeftX, botDiagLeftY, botDiagLeftZ, botRightU, botTopV);
        tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, botRightU, botCenterV);
        tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, botRightU, botCenterV);

        /// Front
        tess.addVertexWithUV(topEdgeLeftX, topEdgeLeftY, topEdgeLeftZ, botRightU, botCenterV);
        tess.addVertexWithUV(botEdgeLeftX, botEdgeLeftY, botEdgeLeftZ, botRightU, botCenterV);
        tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, botRightU, botTopV);
        tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, botLeftU, botCenterV);

        /////
        /// Front Right
        tess.addVertexWithUV(topEdgeRightX, topEdgeRightY, topEdgeRightZ, botRightU, botCenterV);
        tess.addVertexWithUV(botEdgeRightX, botEdgeRightY, botEdgeRightZ, botRightU, botCenterV);
        tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, botRightU, botTopV);
        tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, botLeftU, botCenterV);

        /// Diag Right
        tess.addVertexWithUV(topDiagRightX, topDiagRightY, topDiagRightZ, botLeftU, botCenterV);
        tess.addVertexWithUV(botDiagRightX, botDiagRightY, botDiagRightZ, botRightU, botTopV);
        tess.addVertexWithUV(botRightSideNearX, botRightSideNearY, botRightSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(topRightSideNearX, topRightSideNearY, topRightSideNearZ, botRightU, botCenterV);
        //////

        /// Right Side
        tess.addVertexWithUV(topRightSideNearX, topRightSideNearY, topRightSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(botRightSideNearX, botRightSideNearY, botRightSideNearZ, botRightU, botCenterV);
        tess.addVertexWithUV(botRightSideFarX, botRightSideFarY, botRightSideFarZ, botRightU, botTopV);
        tess.addVertexWithUV(topRightSideFarX, topRightSideFarY, topRightSideFarZ, botLeftU, botCenterV);

        didRender = true;


        return didRender;
    }

}
