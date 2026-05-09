package mods.betterfoliage.client.render;

public class RenderUtils {
    public static float getColorMultiplierBySide(int side) {
        return switch (side) {
            case 0 -> 0.5f; // Down (-Y)
            case 2, 3 -> 0.8f; // North (-Z), South (+Z)
            case 4, 5 -> 0.6f; // West (-X), East (+X)
            default -> 1f; // Up (+Y), or UNKNOWN
        };
    }
}
