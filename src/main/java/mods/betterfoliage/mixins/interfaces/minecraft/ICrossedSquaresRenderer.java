package mods.betterfoliage.mixins.interfaces.minecraft;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICrossedSquaresRenderer {

    /// Vertical Scale
    void betterfoliage$setVerticalScale(float verticalScale);

    void betterfoliage$resetVerticalScale();

    /// Second Sprite
    void betterfoliage$setSecondSprite(IIcon secondSprite);

    /// Rotation
    void betterfoliage$setRotation(double x, double y, double z, ForgeDirection axis);

    void betterfoliage$resetRotation();

    /// Ambient Occlusion
    void betterfoliage$setAORender(Block block, int x, int y, int z, boolean useBlockColor);

    void betterfoliage$resetAORender();
}
