package mods.betterfoliage.client.registries;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.ResourceUtils;

public class LeafInfo implements ISpriteProvider {

    public final ResourceLocation baseResource;
    public final ResourceLocation generatedResource;
    public final IIcon roundLeafTexture;

    // private String roundLeafType;
    // private String particleLeafType;

    public LeafInfo(TextureMap atlas, ResourceLocation baseResource, String domain) {
        this.baseResource = baseResource;
        this.generatedResource = new ResourceLocation(domain, baseResource.getResourcePath());
        this.roundLeafTexture = atlas.registerIcon(ResourceUtils.convertToSpriteName(baseResource, domain));
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return roundLeafTexture;
    }
}
