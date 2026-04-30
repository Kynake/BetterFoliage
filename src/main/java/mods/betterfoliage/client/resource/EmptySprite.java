package mods.betterfoliage.client.resource;

import net.minecraft.util.IIcon;

public class EmptySprite implements IIcon {

    private static EmptySprite instance = null;

    public static EmptySprite getInstance() {
        if (instance == null) {
            instance = new EmptySprite();
        }

        return instance;
    }

    private EmptySprite() {}

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }

    @Override
    public float getMinU() {
        return 0;
    }

    @Override
    public float getMaxU() {
        return 0;
    }

    @Override
    public float getInterpolatedU(double u) {
        return 0;
    }

    @Override
    public float getMinV() {
        return 0;
    }

    @Override
    public float getMaxV() {
        return 0;
    }

    @Override
    public float getInterpolatedV(double v) {
        return 0;
    }

    @Override
    public String getIconName() {
        return "";
    }
}
