package mods.betterfoliage.mixins.interfaces.minecraft;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICrossedSquaresRenderer {

    void betterfoliage$setIsRenderingCrossedSquares(boolean isCrossedSquares);

    void betterfoliage$setVerticalScale(float verticalScale);

    void betterfoliage$setRotation(double x, double y, double z, ForgeDirection axis);

    void betterfoliage$resetRotation();
}
