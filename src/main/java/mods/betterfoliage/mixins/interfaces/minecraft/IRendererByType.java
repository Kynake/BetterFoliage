package mods.betterfoliage.mixins.interfaces.minecraft;

import net.minecraft.block.Block;

public interface IRendererByType {

    boolean betterfoliage$renderBaseBlock(Block block, int x, int y, int z);
}
