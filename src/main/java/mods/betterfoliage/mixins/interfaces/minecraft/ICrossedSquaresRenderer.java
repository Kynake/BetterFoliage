package mods.betterfoliage.mixins.interfaces.minecraft;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICrossedSquaresRenderer {

    void betterfoliage$setVerticalScale(float verticalScale);

    void betterfoliage$resetVerticalScale();

    void betterfoliage$setSecondSprite(IIcon secondSprite);

    void betterfoliage$setRotation(double x, double y, double z, ForgeDirection axis);

    void betterfoliage$resetRotation();
}
