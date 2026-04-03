package mods.betterfoliage.client.render;

import net.minecraft.util.IIcon;

import mods.betterfoliage.utils.MathUtils;

public class PartialSprite implements IIcon {

    private final IIcon baseSprite;
    private final float topCut;
    private final float bottomCut;
    private final float leftCut;
    private final float rightCut;

    public PartialSprite(IIcon baseSprite, float topCut, float bottomCut, float leftCut, float rightCut) {
        this.baseSprite = baseSprite;
        this.topCut = topCut;
        this.bottomCut = bottomCut;
        this.leftCut = leftCut;
        this.rightCut = rightCut;
    }

    private static int ceil(float x) {
        int res = (int) x;
        return x - res > 0 ? res + 1 : res;
    }

    @Override
    public int getIconWidth() {
        return ceil(baseSprite.getIconWidth() * getHeightRatio());
    }

    @Override
    public int getIconHeight() {
        return ceil(baseSprite.getIconHeight() * getWidthRatio());
    }

    @Override
    public float getMinU() {
        return baseSprite.getInterpolatedU(leftCut * 16D);
    }

    @Override
    public float getMaxU() {
        return baseSprite.getInterpolatedU((1 - rightCut) * 16D);
    }

    @Override
    public float getMinV() {
        return baseSprite.getInterpolatedV(topCut * 16D);
    }

    @Override
    public float getMaxV() {
        return baseSprite.getInterpolatedV((1 - bottomCut) * 16D);
    }

    @Override
    public float getInterpolatedU(double value) {
        double remap = MathUtils.remapToRange(0D, 16D, getMinU(), getMaxU(), value);
        return baseSprite.getInterpolatedU(remap * 16D);
    }

    @Override
    public float getInterpolatedV(double value) {
        double remap = MathUtils.remapToRange(0D, 16D, getMinV(), getMaxV(), value);
        return baseSprite.getInterpolatedV(remap * 16D);
    }

    @Override
    public String getIconName() {
        return baseSprite.getIconName();
    }

    public float getHeightRatio() {
        return 1F - (topCut + bottomCut);
    }

    public float getWidthRatio() {
        return 1F - (leftCut + rightCut);
    }
}
