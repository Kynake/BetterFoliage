package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderUtils {

    private static final float COUNTERCLOCK_SIN = MathHelper.sin((float) (Math.PI / 2D));
    private static final float COUNTERCLOCK_COS = MathHelper.cos((float) (Math.PI / 2D));
    private static final float CLOCKWISE_SIN = MathHelper.sin((float) (3D * Math.PI / 2D));
    private static final float CLOCKWISE_COS = MathHelper.cos((float) (3D * Math.PI / 2D));

    public static float getColorMultiplierBySide(ForgeDirection side) {
        return getColorMultiplierBySide(side.ordinal());
    }

    public static float getColorMultiplierBySide(int side) {
        return switch (side) {
            case 0 -> 0.5f; // Down (-Y)
            case 2, 3 -> 0.8f; // North (-Z), South (+Z)
            case 4, 5 -> 0.6f; // West (-X), East (+X)
            default -> 1f; // Up (+Y), or UNKNOWN
        };
    }

    public static void setColorMultiplierBySide(Tessellator tessellator, int side) {
        int multiplier = (int) (getColorMultiplierBySide(side) * 255f);
        tessellator.setColorRGBA(multiplier, multiplier, multiplier, 0xFF);
    }

    // TODO: use [0, 1] ratio instead
    /// Colors are in ARGB format. Alpha is copied from first color
    public static int blendRGB(int colorA, int colorB, float ratio) {
        float invRatio = (1f / ratio);
        int r = (int) (ratio * ((colorA >> 16) & 0xFF) + invRatio * ((colorB >> 16) & 0xFF));
        int g = (int) (ratio * ((colorA >> 8) & 0xFF) + invRatio * ((colorB >> 8) & 0xFF));
        int b = (int) (ratio * ((colorA) & 0xFF) + invRatio * ((colorB) & 0xFF));

        int res = colorA & 0xFF_00_00_00;
        res |= r << 16;
        res |= g << 8;
        res |= b;

        return res;
    }

    /// Colors are in ARGB format. Color channels are copied from first color
    public static int multiplyAlphas(int colorA, int colorB) {
        float alphaA = ((colorA >> 24) & 0xFF) / (float) 0xFF;
        float alphaB = ((colorB >> 24) & 0xFF) / (float) 0xFF;
        int alpha = (int) (alphaA * alphaB * 0xFF);
        int res = colorA & 0x00_FF_FF_FF;
        res |= alpha << 24;
        return res;
    }

    public static void setAOForCrossedSquareVertex(RenderBlocks renderer, int x, int y, int z, ForgeDirection firstAxis,
        ForgeDirection secondAxis, ForgeDirection thirdAxis) {

        Block block = renderer.blockAccess.getBlock(x, y, z);

        int aoX = x;
        int aoY = y;
        int aoZ = z;

        ForgeDirection aoFirst = firstAxis;
        ForgeDirection aoSecond = secondAxis;
        ForgeDirection aoThird = thirdAxis;

        int color = block.colorMultiplier(renderer.blockAccess, x, y, z);
        float colorMult = getColorMultiplierBySide(firstAxis);

        if (!isFaceOccluded(renderer.blockAccess, x, y, z, thirdAxis)) {
            colorMult = getColorMultiplierBySide(thirdAxis);
            aoFirst = thirdAxis;
            aoThird = firstAxis;
        } else if (isFaceOccluded(renderer.blockAccess, x, y, z, firstAxis)) {
            colorMult = getColorMultiplierBySide(secondAxis);

            if (isFaceOccluded(
                renderer.blockAccess,
                x + secondAxis.offsetX,
                y + secondAxis.offsetY,
                z + secondAxis.offsetZ,
                firstAxis)) {
                aoX = x + secondAxis.offsetX + thirdAxis.offsetX;
                aoY = y + secondAxis.offsetY + thirdAxis.offsetY;
                aoZ = z + secondAxis.offsetZ + thirdAxis.offsetZ;
                aoSecond = secondAxis.getOpposite();
                aoThird = secondAxis.getOpposite();
            } else {
                aoX = x + secondAxis.offsetX;
                aoY = y + secondAxis.offsetY;
                aoZ = z + secondAxis.offsetZ;
                aoSecond = secondAxis.getOpposite();
            }
        }

        setAOForBlockCorner(renderer, block, aoX, aoY, aoZ, aoFirst, aoSecond, aoThird, color, colorMult);
    }

    public static void setAOForBlockCorner(RenderBlocks renderer, Block block, int x, int y, int z,
        ForgeDirection firstAxis, ForgeDirection secondAxis, ForgeDirection thirdAxis, int color,
        float colorMultiplier) {

        x += firstAxis.offsetX;
        y += firstAxis.offsetY;
        z += firstAxis.offsetZ;

        Block secondNeighbor = renderer.blockAccess
            .getBlock(x + secondAxis.offsetX, y + secondAxis.offsetY, z + secondAxis.offsetZ);
        Block thirdNeighbor = renderer.blockAccess
            .getBlock(x + thirdAxis.offsetX, y + thirdAxis.offsetY, z + thirdAxis.offsetZ);

        float secondAO = secondNeighbor.getAmbientOcclusionLightValue();
        int secondBrightness = block.getMixedBrightnessForBlock(
            renderer.blockAccess,
            x + secondAxis.offsetX,
            y + secondAxis.offsetY,
            z + secondAxis.offsetZ);

        float thirdAO = thirdNeighbor.getAmbientOcclusionLightValue();
        int thirdBrightness = block.getMixedBrightnessForBlock(
            renderer.blockAccess,
            x + thirdAxis.offsetX,
            y + thirdAxis.offsetY,
            z + thirdAxis.offsetZ);

        float diagonalAO;
        int diagonalBrightness;

        if (secondNeighbor.getCanBlockGrass() && thirdNeighbor.getCanBlockGrass()) {
            diagonalAO = secondAO;
            diagonalBrightness = secondBrightness;
        } else {
            int diagX = x + firstAxis.offsetX + secondAxis.offsetX + thirdAxis.offsetX;
            int diagY = y + firstAxis.offsetY + secondAxis.offsetY + thirdAxis.offsetY;
            int diagZ = z + firstAxis.offsetZ + secondAxis.offsetZ + thirdAxis.offsetZ;

            Block diagonalNeighbor = renderer.blockAccess.getBlock(diagX, diagY, diagZ);

            diagonalAO = diagonalNeighbor.getAmbientOcclusionLightValue();
            diagonalBrightness = block.getMixedBrightnessForBlock(renderer.blockAccess, diagX, diagY, diagZ);
        }

        x -= firstAxis.offsetX;
        y -= firstAxis.offsetY;
        z -= firstAxis.offsetZ;

        Block neighbor = renderer.blockAccess
            .getBlock(x + firstAxis.offsetX, y + firstAxis.offsetY, z + firstAxis.offsetZ);

        float neighborAO = neighbor.getAmbientOcclusionLightValue();
        int blockBrightness = neighbor.isOpaqueCube() ? block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z)
            : block.getMixedBrightnessForBlock(
                renderer.blockAccess,
                x + firstAxis.offsetX,
                y + firstAxis.offsetY,
                z + firstAxis.offsetZ);

        float ao = colorMultiplier * (neighborAO + secondAO + thirdAO + diagonalAO) / 4f;
        int brightness = renderer
            .getAoBrightness(secondBrightness, thirdBrightness, diagonalBrightness, blockBrightness);

        float r = ao * (float) (color >> 16 & 0xFF) / 255.0f;
        float g = ao * (float) (color >> 8 & 0xFF) / 255.0f;
        float b = ao * (float) (color & 0xFF) / 255.0f;

        Tessellator tess = Tessellator.instance;
        tess.setBrightness(brightness);
        tess.setColorOpaque_F(r, g, b);
    }

    public static boolean isFaceOccluded(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        Block neighbor = world.getBlock(x + face.offsetX, y + face.offsetY, z + face.offsetZ);

        return neighbor.isOpaqueCube() || neighbor == world.getBlock(x, y, z);
    }

    /// Rotates a point in 3D space 90 degrees counter-clockwise along an axis
    public static void rotateCounterclock(ForgeDirection rotationAxis, double axisX, double axisY, double axisZ,
        double[] xyz) {
        double centerA, centerB;
        double a, b;
        float rotSin, rotCos;

        switch (rotationAxis) {
            // Constant Y (XZ)
            case DOWN -> {
                centerA = axisX;
                centerB = axisZ;
                a = xyz[0];
                b = xyz[2];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;

            }
            case UP -> {
                centerA = axisX;
                centerB = axisZ;
                a = xyz[0];
                b = xyz[2];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            // Constant Z (XY)
            case NORTH -> {
                centerA = axisX;
                centerB = axisY;
                a = xyz[0];
                b = xyz[1];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;
            }
            case SOUTH -> {
                centerA = axisX;
                centerB = axisY;
                a = xyz[0];
                b = xyz[1];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            // Constant X (YZ)
            case WEST -> {
                centerA = axisY;
                centerB = axisZ;
                a = xyz[1];
                b = xyz[2];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;
            }
            case EAST -> {
                centerA = axisY;
                centerB = axisZ;
                a = xyz[1];
                b = xyz[2];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            default -> {
                return;
            }
        }

        double deltaA = a - centerA;
        double deltaB = b - centerB;

        a = (rotCos * deltaA) + (rotSin * deltaB) + centerA;
        b = (rotCos * deltaB) + (rotSin * deltaA) + centerB;

        switch (rotationAxis) {
            // Constant Y (XZ)
            case DOWN, UP -> {
                xyz[0] = a;
                xyz[2] = b;
            }

            // Constant Z (XY)
            case NORTH, SOUTH -> {
                xyz[0] = a;
                xyz[1] = b;
            }

            // Constant X (YZ)
            case WEST, EAST -> {
                xyz[1] = a;
                xyz[2] = b;
            }
        }
    }

    /// Swizzles AO Axes by the given rotation axis 90 degrees counter-clockwise.
    /// Used for adding AO to extra leaves in dense mode
    /// NORTH / WEST rotation axis are not implemented, as they're not required
    public static void swizzleCrossAOCounterclock(ForgeDirection rotationAxis, ForgeDirection[] axes) {
        switch (rotationAxis) {
            case SOUTH -> {
                switch (axes[0]) {
                    case NORTH, SOUTH -> {
                        axes[1] = rotationAxis.getOpposite()
                            .getRotation(axes[1]);
                        axes[2] = rotationAxis.getRotation(axes[2]);
                    }

                    case WEST, EAST -> {
                        ForgeDirection first = axes[0];
                        axes[0] = rotationAxis.getRotation(axes[2]);
                        axes[2] = axes[1];
                        axes[1] = rotationAxis.getOpposite()
                            .getRotation(first);
                    }
                }
            }

            case EAST -> {
                switch (axes[0]) {
                    case NORTH, SOUTH -> {
                        ForgeDirection first = axes[0];
                        axes[0] = rotationAxis.getOpposite()
                            .getRotation(axes[2]);
                        axes[2] = axes[1];
                        axes[1] = rotationAxis.getRotation(first);
                    }

                    case WEST, EAST -> {
                        axes[1] = rotationAxis.getRotation(axes[1]);
                        axes[2] = rotationAxis.getOpposite()
                            .getRotation(axes[2]);
                    }
                }
            }
        }
    }
}
