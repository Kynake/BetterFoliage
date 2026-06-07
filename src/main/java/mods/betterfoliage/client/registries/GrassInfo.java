package mods.betterfoliage.client.registries;

import java.awt.Color;

import net.minecraft.util.IIcon;

import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.RenderUtils;

public class GrassInfo {

    public final int customColor;

    public GrassInfo(IIcon grassSprite) {
        final int averageColor = RenderUtils.averageSpriteSquare(grassSprite, 0);
        if (averageColor == 0) {
            customColor = 0;
            return;
        }

        final int r = averageColor >> 16 & 0xFF;
        final int g = averageColor >> 8 & 0xFF;
        final int b = averageColor & 0xFF;

        final float[] hsb = Color.RGBtoHSB(r, g, b, null);

        customColor = hsb[1] > Config.shortGrass.INSTANCE.getSaturationThreshold() ? averageColor // TODO: set
                                                                                                  // brightness to 0.8?
                                                                                                  // (maybe)
            : 0;
    }
}
