package mods.betterfoliage.client.resource.generators;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.ResourceUtils;

public abstract class SingleTextureGenerator extends TextureGenerator implements ISpriteProvider {

    protected final ResourceLocation baseResource;
    protected final ResourceLocation generatedResource;

    protected IIcon generatedTexture;

    public SingleTextureGenerator(String name, String domain, String baseDomain, String baseResourcePath) {
        super(name, "Single texture Generator for: " + name, "single_tex_" + domain);
        this.baseResource = new ResourceLocation(baseDomain, baseResourcePath);
        this.generatedResource = new ResourceLocation(this.domain, baseResourcePath);
    }

    @Override
    protected void onSpriteStitch(TextureMap atlas) {
        if (atlas.getTextureType() != 0) {
            return;
        }

        final String textureName = ResourceUtils.convertToSpriteName(generatedResource);
        generatedTexture = atlas.registerIcon(textureName);
    }

    // TODO: support for Normal (_n) and Specular (_s)?
    // Maybe add to base class instead
    @Override
    public boolean resourceExists(ResourceLocation location) {
        if (ResourceUtils.isMcMeta(location)) {
            return ResourceUtils.resourceHasMcMeta(baseResource);
        }

        return location.equals(generatedResource);
    }

    @Override
    protected InputStream getGeneratedMcMeta(ResourceLocation location) throws IOException {
        return ResourceUtils
            .getResource(new ResourceLocation(baseResource.getResourceDomain(), location.getResourcePath()))
            .getInputStream();
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z) {
        return generatedTexture;
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return generatedTexture;
    }
}
