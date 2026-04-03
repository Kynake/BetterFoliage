package mods.betterfoliage.client.render;

import net.minecraft.util.IIcon;

public interface TextureProvider {

    IIcon getTextureForCoord(int x, int y, int z);
}
