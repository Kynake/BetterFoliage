package mods.betterfoliage.client.render;

import net.minecraft.util.IIcon;

public interface ITextureProvider {

    IIcon getTextureForCoord(int x, int y, int z);
}
