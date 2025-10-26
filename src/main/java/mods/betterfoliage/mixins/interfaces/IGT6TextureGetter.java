package mods.betterfoliage.mixins.interfaces;

import net.minecraft.block.Block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;

public interface IGT6TextureGetter {

    @SideOnly(Side.CLIENT)
    ITexture betterfoliage$getTexture(Block block, int renderPass, byte side, boolean[] sidesToRender);
}
