package mods.betterfoliage.client.resource.generators;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.ResourceUtils;

// TODO: Maybe not necessary (needed because of generated snowtextures?)
public class ShortGrassGenerator extends TextureGenerator {

    private final ResourceLocation baseResource;
    private final ResourceLocation generatedResource;

    private IIcon generatedTexture;

    // TODO: support for mcmeta / animated textures, Normal (_n) and Specular (_s)?
    // Maybe add to base class instead

    public ShortGrassGenerator(String baseDomain, String baseResourcePath) {
        this(new ResourceLocation(baseDomain, baseResourcePath));
    }

    public ShortGrassGenerator(ResourceLocation baseResource) {
        super("Generated Grass", "Pack for custom generated grass", "gen_grass");
        this.baseResource = baseResource;
        this.generatedResource = new ResourceLocation(this.domain, baseResource.getResourcePath());
    }

    @Override
    protected void onSpriteStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) {
            return;
        }

        String name = generatedResource.getResourcePath();
        int startIndex = name.lastIndexOf('/') + 1;
        int endIndex = name.lastIndexOf('.');

        if (startIndex <= 0 || startIndex > endIndex) {
            BetterFoliageMod.log.error("Invalid resource location: {}", generatedResource);
            return;
        }

        String textureName = domain + ":" + name.substring(startIndex, endIndex);
        generatedTexture = event.map.registerIcon(textureName);
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        if (isMcMeta(location)) {
            return ResourceUtils.resourceHasMcMeta(baseResource);
        }

        return location.equals(generatedResource);
    }

    @Override
    protected InputStream getGeneratedMcMeta(ResourceLocation location) throws IOException {
        return ResourceUtils.getResourceManager()
            .getResource(new ResourceLocation(baseResource.getResourceDomain(), location.getResourcePath()))
            .getInputStream();
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

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z) {
        return generatedTexture;
    }
}
