package mods.betterfoliage.utils;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public final class MathUtils {

    // TODO: Make configurable
    // Mods uses a set number as random seed because the world seed can't be accessed Client-side in a server.
    private static final int SEED = 402653189;

    private static final float COUNTERCLOCK_SIN = MathHelper.sin((float) (Math.PI / 2D));
    private static final float COUNTERCLOCK_COS = MathHelper.cos((float) (Math.PI / 2D));
    private static final float CLOCKWISE_SIN = MathHelper.sin((float) (3D * Math.PI / 2D));
    private static final float CLOCKWISE_COS = MathHelper.cos((float) (3D * Math.PI / 2D));

    public static int hash(int x) {
        x ^= x >>> 16;
        x *= 0x7FEB352D;
        x ^= x >>> 15;
        x *= 0x846CA68B;
        x ^= x >>> 16;
        return x;
    }

    public static int hashCoords(int x, int y, int z) {
        return hashCoords(x, y, z, 0);
    }

    public static int hashCoords(int x, int y, int z, int salt) {
        int hash = hash(salt + SEED);
        hash = hash(x + hash);
        hash = hash(y + hash);
        hash = hash(z + hash);
        return hash;
    }

    // The hash is assumed to be in the range [Integer.MIN_VALUE, Integer.MAX_VALUE]
    public static double hashToRange(int hash, double min, double max) {
        double invLerp = (double) ((long) hash - 0xFFFFFFFF80000000L) / (double) 0xFFFFFFFFL;
        return lerp(min, max, invLerp);
    }

    public static double inverseLerp(double min, double max, double value) {
        return (value - min) / (max - min);
    }

    public static double lerp(double min, double max, double lerpAmount) {
        return (max - min) * lerpAmount + min;
    }

    public static double remapToRange(double fromRangeMin, double fromRangeMax, double toRangeMin, double toRangeMax,
        double value) {
        return lerp(toRangeMin, toRangeMax, inverseLerp(fromRangeMin, fromRangeMax, value));
    }

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
