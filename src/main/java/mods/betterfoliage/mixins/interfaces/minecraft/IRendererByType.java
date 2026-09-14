package mods.betterfoliage.mixins.interfaces.minecraft;

import net.minecraft.block.Block;

public interface IRendererByType {

    // TODO: Use this in more places
    boolean betterfoliage$renderBaseBlock(Block block, int x, int y, int z);
}
