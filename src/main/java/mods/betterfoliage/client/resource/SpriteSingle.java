package mods.betterfoliage.client.resource;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import mods.betterfoliage.client.render.ISpriteProvider;

public class SpriteSingle extends StitchListener implements ISpriteProvider {

    private final ResourceLocation spriteLocation;
    private IIcon sprite;

    public SpriteSingle(String domain, String path) {
        super();
        spriteLocation = new ResourceLocation(domain, path);
    }

    @Override
    protected void onSpriteStitch(TextureStitchEvent.Pre event) {
        int type = event.map.getTextureType();
        boolean isValid = (type == 0 && spriteLocation.getResourcePath()
            .startsWith("textures/blocks/")) || (type == 1
                && spriteLocation.getResourcePath()
                    .startsWith("textures/items/"));

        if (!isValid) {
            return;
        }

        String textureName = ResourceUtils.convertToSpriteName(spriteLocation);
        if (textureName != null) {
            sprite = event.map.registerIcon(textureName);
        }
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return sprite;
    }
}
