package mods.betterfoliage.client.resource.generators;

import java.awt.image.BufferedImage;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

// TODO: Maybe not necessary (needed because of generated snowtextures?)
public class ShortGrassGenerator extends TextureGenerator {

    ResourceLocation baseResource;

    public ShortGrassGenerator(ResourceLocation baseResource) {
        super("Generated Grass", "Pack for custom generated grass", "gen_grass");
        this.baseResource = baseResource;
    }

    @Override
    public IIcon getTextureForCoord(int x, int y, int z) {
        return null;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return false;
    }

    @Override
    protected void generateTextures() {

    }

    @Override
    protected BufferedImage getGeneratedTexture(ResourceLocation location) {
        return null;
    }
}
