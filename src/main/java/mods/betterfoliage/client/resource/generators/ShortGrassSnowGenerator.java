package mods.betterfoliage.client.resource.generators;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.utils.MathUtils;

public class ShortGrassSnowGenerator extends ShortGrassGenerator {

    // TODO make both configurable?
    private static final int BLEND_COLOR = 0xFF_FF_FF; // White
    private static final float BLEND_RATIO = 2f / 3f;

    public ShortGrassSnowGenerator(String baseDomain, String baseResourcePath) {
        super("Generated Short Snow Grass", "gen_grass_snow", baseDomain, baseResourcePath);
    }

    @Override
    protected BufferedImage getGeneratedTexture(ResourceLocation location) throws IOException {
        BufferedImage base = super.getGeneratedTexture(location);

        for (int x = 0; x < base.getWidth(); x++) {
            for (int y = 0; y < base.getHeight(); y++) {
                base.setRGB(x, y, MathUtils.blendRGB(base.getRGB(x, y), BLEND_COLOR, BLEND_RATIO));
            }
        }

        return base;
    }
}
