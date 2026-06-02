package mods.betterfoliage.utils;

import java.util.Random;

public final class MathUtils {

    // TODO: Make configurable
    // Mods uses a set number as random seed because the world seed can't be accessed Client-side in a server.
    private static final int SEED = 402653189;

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

    public static float lerp(float min, float max, float lerpAmount) {
        return (max - min) * lerpAmount + min;
    }

    public static double remapToRange(double fromRangeMin, double fromRangeMax, double toRangeMin, double toRangeMax,
        double value) {
        return lerp(toRangeMin, toRangeMax, inverseLerp(fromRangeMin, fromRangeMax, value));
    }

    public static double randomBetween(double min, double max) {
        return lerp(min, max, Math.random());
    }

    public static double randomBetween(Random rng, double min, double max) {
        return lerp(min, max, rng.nextDouble());
    }
}
