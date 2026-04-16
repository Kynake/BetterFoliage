package mods.betterfoliage.client.resource.generators;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.resource.ResourceUtils;

public class ShortGrassGenerator extends SingleTextureGenerator {

    // TODO make configurable?
    // Only use the top 6 / 16 pixels from the base texture
    private static final float topCutRatio = 6.f / 16.f;

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

        BufferedImage genImage = createEmptyFrom(baseImage);
        Graphics2D graphics = genImage.createGraphics();

        int width = baseImage.getWidth();
        int frames = baseImage.getHeight() / width;
        int topPixels = (int) (width * topCutRatio);

        for (int frame = 0; frame < frames; frame++) {
            BufferedImage baseSubFrame = baseImage.getSubimage(0, width * frame, width, topPixels);
            graphics.drawImage(baseSubFrame, 0, width * (frame + 1) - topPixels, null);
        }

        return genImage;
    }
}
