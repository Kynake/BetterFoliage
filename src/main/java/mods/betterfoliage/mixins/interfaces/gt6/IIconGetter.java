package mods.betterfoliage.mixins.interfaces.gt6;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IIconGetter {

    @SideOnly(Side.CLIENT)
    IIcon betterfoliage$getIconForSide(int side);
}
