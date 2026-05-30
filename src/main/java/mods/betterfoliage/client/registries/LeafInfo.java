package mods.betterfoliage.client.registries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.client.resource.SpriteSetRandom;

public class LeafInfo implements ISpriteProvider {

    private static final Map<String, SpriteSetRandom> registeredParticleTypes = new HashMap<>();

    public final String maskType;
    public final String particleType;

    public final int averageColor;

    public final ResourceLocation baseLeafResource;
    public final ResourceLocation generatedLeafResource;
    public final IIcon roundLeafSprite;

    public final SpriteSetRandom particleSprites;

    public static void clearRegistries() {
        registeredParticleTypes.clear();
    }

    public LeafInfo(TextureMap atlas, IIcon baseSprite, String leafDomain, String maskType, String particleType) {
        this.maskType = maskType;
        this.particleType = particleType;

        this.averageColor = RenderUtils.averageSpriteSquare(baseSprite, 0);

        this.baseLeafResource = ResourceUtils.convertToResourceLocation(baseSprite);
        this.generatedLeafResource = new ResourceLocation(leafDomain, baseLeafResource.getResourcePath());
        this.roundLeafSprite = atlas.registerIcon(ResourceUtils.convertToSpriteName(baseLeafResource, leafDomain));

        if (registeredParticleTypes.containsKey(particleType)) {
            particleSprites = registeredParticleTypes.get(particleType);
        } else {
            particleSprites = new SpriteSetRandom(
                BetterFoliageMod.DOMAIN,
                "textures/blocks/falling_leaf_" + particleType + "_",
                ".png",
                false);
            particleSprites.registerManually(atlas);

            registeredParticleTypes.put(particleType, particleSprites);
        }
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return roundLeafSprite;
    }
}
