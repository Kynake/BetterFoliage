package mods.betterfoliage.utils;

public final class MathUtils {

    public static int hash(int x) {
        x ^= x >>> 16;
        x *= 0x7FEB352D;
        x ^= x >>> 15;
        x *= 0x846CA68B;
        x ^= x >>> 16;
        return x;
    }

    public static int hashCoords(int x, int y, int z, int salt) {
        int hash = hash(salt + 97);
        hash = hash(x + hash);
        hash = hash(y + hash);
        hash = hash(z + hash);
        return hash;
    }

    public static int hashWorldCoords(int x, int y, int z, long seed) {
        int hash = hash(((int) seed) + 53);
        hash = hash((int) (seed >> 32) + hash);
        return hashCoords(x, y, z, hash);
    }

    public static double hashToRange(int hash, double min, double max) {
        double invLerp = (double) ((long) hash - 0xFFFFFFFF80000000L) / (double) 0xFFFFFFFFL;
        return (max - min) * invLerp + min;
    }
}
