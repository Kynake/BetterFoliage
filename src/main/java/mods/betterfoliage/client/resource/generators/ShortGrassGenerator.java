package mods.betterfoliage.client.resource.generators;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.resource.ResourceUtils;

public class ShortGrassGenerator extends SingleTextureGenerator {

    public ShortGrassGenerator(String baseDomain, String baseResourcePath) {
        super("Generated Short Grass", "gen_grass", baseDomain, baseResourcePath);
    }

    @Override
    protected BufferedImage getGeneratedTexture(ResourceLocation location) throws IOException {
        if (!resourceExists(location)) {
            throw new IOException("Resource " + location + " is not handled by this generator!");
        }

        IResource base = ResourceUtils.getResourceManager()
            .getResource(baseResource);
        BufferedImage baseImage = ImageIO.read(base.getInputStream());

        if (baseImage == null) {
            throw new IIOException("Base image for generation of resource " + location + " is null!");
        }

        BufferedImage genImage = createCopy(baseImage);
        debugPaintRed(genImage);
        return genImage;
    }
}
