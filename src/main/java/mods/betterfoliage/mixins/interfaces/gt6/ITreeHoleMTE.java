package mods.betterfoliage.mixins.interfaces.gt6;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ITreeHoleMTE {

    @SideOnly(Side.CLIENT)
    IIcon betterFoliage$getTextureForSide(ForgeDirection side);
}
