package mods.betterfoliage.client.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.utils.MathUtils;

public class SpriteSet extends StitchListener implements ISpriteProvider {

    protected final String domain;
    protected final String prefix;
    protected final String suffix;

    protected List<IIcon> sprites;

    public SpriteSet(String domain, String prefix, String suffix) {
        this(domain, prefix, suffix, true);
    }

    // TODO: Consider regex instead?
    // Had to remove unused some mycelium textures so that they wouldn't be selected as valid
    public SpriteSet(String domain, String prefix, String suffix, boolean selfRegister) {
        super(selfRegister);
        this.domain = domain;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    protected final void onSpriteStitch(TextureMap atlas) {
        int type = atlas.getTextureType();
        boolean isValid = (type == 0 && prefix.startsWith("textures/blocks/"))
            || (type == 1 && prefix.startsWith("textures/items/"));

        if (!isValid) {
            return;
        }

        Set<ResourceLocation> resources = ResourceUtils.findResourcesWithPattern(domain, prefix, suffix);
        sprites = new ArrayList<>(resources.size());

        for (ResourceLocation res : resources) {
            String textureName = ResourceUtils.convertToSpriteName(res);
            sprites.add(atlas.registerIcon(textureName));
        }
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
