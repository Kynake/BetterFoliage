package mods.betterfoliage.client.render;

import net.minecraft.util.IIcon;

public interface ISpriteProvider {

    IIcon getSpriteForCoord(int x, int y, int z);
}
