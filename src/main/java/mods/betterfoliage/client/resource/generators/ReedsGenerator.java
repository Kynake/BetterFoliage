package mods.betterfoliage.client.resource.generators;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.utils.MathUtils;

public class ReedsGenerator extends TextureGenerator implements ISpriteProvider {

    public static float HORIZONTAL_SCALING = 2f;

    private final String baseDomain;

    private final Set<ResourceLocation> backingResources;
    private List<IIcon> sprites;

    public ReedsGenerator(String domain, String prefix, String suffix) {
        super("Generated Reeds", "Reed textures. Fitted into squares.", "gen_reeds");
        this.baseDomain = domain;

        backingResources = ResourceUtils.findResourcesWithPattern(baseDomain, prefix, suffix);
    }

    @Override
    protected void onSpriteStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;

        sprites = new ArrayList<>(backingResources.size());

        for (ResourceLocation res : backingResources) {
            String textureName = ResourceUtils.convertToSpriteName(res, domain);
            if (textureName == null) continue;
            sprites.add(event.map.registerIcon(textureName));
        }
    }

    @Override
    protected BufferedImage getGeneratedTexture(ResourceLocation location) throws IOException {
        if (!resourceExists(location)) {
            throw new IOException("Resource " + location + " is not handled by this generator!");
        }

        IResource base = ResourceUtils.getResource(remapToBase(location));
        BufferedImage baseImage = ImageIO.read(base.getInputStream());

        if (baseImage == null) {
            throw new IIOException("Base image for generation of resource " + location + " is null!");
        }

        int baseWidth = baseImage.getWidth();
        int xPos = baseWidth / 2;
        int width = baseWidth * 2;
        int frames = baseImage.getHeight() / width;

        BufferedImage genImage = new BufferedImage(width, baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = genImage.createGraphics();

        for (int frame = 0; frame < frames; frame++) {
            BufferedImage baseSubFrame = baseImage.getSubimage(0, width * frame, baseWidth, width);
            graphics.drawImage(baseSubFrame, xPos, width * frame, null);
        }

        graphics.dispose();
        return genImage;
    }

    @Override
    protected InputStream getGeneratedMcMeta(ResourceLocation location) throws IOException {
        return ResourceUtils.getResource(remapToBase(location))
            .getInputStream();
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        ResourceLocation remappedResource = remapToBase(location);
        ResourceLocation remappedNonMeta = ResourceUtils.getBaseForMcMeta(remappedResource);

        return backingResources.contains(remappedNonMeta)
            && (!ResourceUtils.isMcMeta(remappedResource) || ResourceUtils.resourceHasMcMeta(remappedNonMeta));
    }

    private ResourceLocation remapToBase(ResourceLocation location) {
        return new ResourceLocation(baseDomain, location.getResourcePath());
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        if (sprites == null || sprites.isEmpty()) {
            return null;
        }

        int index = Math.abs(MathUtils.hashCoords(x, y, z, side) % sprites.size());
        return sprites.get(index);
    }
}
