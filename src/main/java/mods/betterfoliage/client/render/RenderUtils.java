package mods.betterfoliage.client.render;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderUtils {

    private static final float COUNTERCLOCK_SIN = MathHelper.sin((float) (Math.PI / 2D));
    private static final float COUNTERCLOCK_COS = MathHelper.cos((float) (Math.PI / 2D));
    private static final float CLOCKWISE_SIN = MathHelper.sin((float) (3D * Math.PI / 2D));
    private static final float CLOCKWISE_COS = MathHelper.cos((float) (3D * Math.PI / 2D));

    public static float getColorMultiplierBySide(int side) {
        return switch (side) {
            case 0 -> 0.5f; // Down (-Y)
            case 2, 3 -> 0.8f; // North (-Z), South (+Z)
            case 4, 5 -> 0.6f; // West (-X), East (+X)
            default -> 1f; // Up (+Y), or UNKNOWN
        };
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
}
