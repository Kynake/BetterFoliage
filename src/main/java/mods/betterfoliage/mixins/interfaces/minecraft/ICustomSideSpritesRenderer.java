package mods.betterfoliage.mixins.interfaces.minecraft;

import mods.betterfoliage.client.render.ISpriteProvider;

/**
 * Allows overriding the IIcons for each side of a block.
 */
public interface ICustomSideSpritesRenderer {

    void betterfoliage$setSpriteProvider(ISpriteProvider provider);

    void betterfoliage$resetSpriteProvider();
}
