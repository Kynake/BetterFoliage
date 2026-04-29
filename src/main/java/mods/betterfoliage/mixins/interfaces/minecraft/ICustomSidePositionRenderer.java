package mods.betterfoliage.mixins.interfaces.minecraft;

/**
 * Enables customization when rendering the faces of a standard block.
 * A Scale and a Separation value can be specified:
 * - Scale will alter the size of a rendered quad
 * - Separation will alter the distance from the block's center to render the quad at
 * Custom rendering can be specified to work on the block's Top, Bottom or Sides separately.
 */
public interface ICustomSidePositionRenderer {

    void betterfoliage$setSidesWithCustomProperties(double separation, double scale, boolean customSides,
        boolean customTop, boolean customBottom);

    void betterfoliage$resetCustomProperties();
}
