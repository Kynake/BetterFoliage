package mods.betterfoliage.mixins.interfaces.gt6.accessors;

import net.minecraft.block.Block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;

public interface ITexture2Accessor {

    @SideOnly(Side.CLIENT)
    ITexture betterfoliage$getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender);
}
