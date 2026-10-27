package mods.betterfoliage.utils;

import java.util.Random;

import mods.betterfoliage.Configs;

public final class MathUtils {

    public static final float PI2 = (float) (Math.PI * 2.0);
    public static final float ROOT2 = (float) (Math.sqrt(2.0));

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
        int hash = hash(salt + Configs.seed);
        hash = hash(x + hash);
        hash = hash(y + hash);
        hash = hash(z + hash);
        return hash;
    }

    // The hash is assumed to be in the range [Integer.MIN_VALUE, Integer.MAX_VALUE]
    public static double hashInverseLerp(int hash) {
        return (double) ((long) hash - 0xFFFFFFFF80000000L) / (double) 0xFFFFFFFFL;
    }

    // TODO: convert to floats, use float lerp instead
    public static double hashToRange(int hash, double min, double max) {
        return lerp(min, max, hashInverseLerp(hash));
    }

    public static float hashToRange(int hash, float min, float max) {
        return lerp(min, max, (float) hashInverseLerp(hash));
    }

    public static double lerp(double min, double max, double lerpAmount) {
        return (max - min) * lerpAmount + min;
    }

    public static float lerp(float min, float max, float lerpAmount) {
        return (max - min) * lerpAmount + min;
    }

    public static float inverseLerp(float min, float max, float value) {
        return (value - min) / (max - min);
    }

    public static float remapToRange(float fromMin, float fromMax, float toMin, float toMax, float value) {
        return lerp(toMin, toMax, inverseLerp(fromMin, fromMax, value));
    }

    public static float randomBetween(Random rng, float min, float max) {
        return lerp(min, max, rng.nextFloat());
    }

    public static int randomBetween(Random rng, int min, int max) {
        return min + rng.nextInt(max - min);
    }
}
