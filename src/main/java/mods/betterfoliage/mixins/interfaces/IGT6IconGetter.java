package mods.betterfoliage.mixins.interfaces;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IGT6IconGetter {

    @SideOnly(Side.CLIENT)
    IIcon betterfoliage$getIconForSide(int side);
}
