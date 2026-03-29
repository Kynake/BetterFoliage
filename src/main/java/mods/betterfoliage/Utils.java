package mods.betterfoliage;

public final class Utils {

    public static int hash(int x) {
        x ^= x >>> 16;
        x *= 0x7feb352d;
        x ^= x >>> 15;
        x *= 0x846ca68b;
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
}
