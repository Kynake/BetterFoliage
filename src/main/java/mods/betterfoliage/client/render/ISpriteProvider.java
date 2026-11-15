package mods.betterfoliage.client.render;

import net.minecraft.util.IIcon;

public interface ISpriteProvider {

    default IIcon getSpriteForCoord(int x, int y, int z) {
        return getSpriteForCoord(x, y, z, 0);
    }

    IIcon getSpriteForCoord(int x, int y, int z, int side);
}
