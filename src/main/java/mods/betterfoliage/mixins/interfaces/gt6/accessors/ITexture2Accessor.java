package mods.betterfoliage.mixins.interfaces.gt6.accessors;

import net.minecraft.block.Block;

import gregapi.render.ITexture;

public interface ITexture2Accessor {

    ITexture betterfoliage$getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender);
}
