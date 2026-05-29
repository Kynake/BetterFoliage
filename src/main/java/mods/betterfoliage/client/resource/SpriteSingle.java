package mods.betterfoliage.client.resource;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.client.render.ISpriteProvider;

public class SpriteSingle extends StitchListener implements ISpriteProvider {

    private final ResourceLocation spriteLocation;
    private IIcon sprite;

    public SpriteSingle(String domain, String path) {
        this(domain, path, true);
    }

    public SpriteSingle(String domain, String path, boolean selfRegister) {
        super(selfRegister);
        spriteLocation = new ResourceLocation(domain, path);
    }

    @Override
    protected void onSpriteStitch(TextureMap atlas) {
        int type = atlas.getTextureType();
        boolean isValid = (type == 0 && spriteLocation.getResourcePath()
            .startsWith("textures/blocks/")) || (type == 1
                && spriteLocation.getResourcePath()
                    .startsWith("textures/items/"));

        if (!isValid) {
            return;
        }

        String textureName = ResourceUtils.convertToSpriteName(spriteLocation);
        if (textureName != null) {
            sprite = atlas.registerIcon(textureName);
        }
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return sprite;
    }
}
