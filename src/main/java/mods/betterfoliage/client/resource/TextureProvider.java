package mods.betterfoliage.client.resource;

import net.minecraft.util.IIcon;

public interface TextureProvider {

    IIcon getTextureForCoord(int x, int y, int z);
}
