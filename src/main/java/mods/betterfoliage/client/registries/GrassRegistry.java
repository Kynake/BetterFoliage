package mods.betterfoliage.client.registries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.StitchListener;

public class GrassRegistry extends StitchListener {

    private static GrassRegistry instance;

    private final Map<IIcon, GrassInfo> grasses = new HashMap<>();

    public static GrassRegistry getInstance() {
        if (instance == null) {
            instance = new GrassRegistry();
        }

        return instance;
    }

    private GrassRegistry() {
        super(true);
    }

    public int getColorForSprite(IIcon sprite) {
        if (grasses == null || !grasses.containsKey(sprite)) {
            return RenderUtils.DEFAULT_COLOR_MULTIPLIER;
        }

        return grasses.get(sprite)
            .customColor();
    }

    @Override
    protected void onSpriteStitch(TextureMap atlas) {
        if (atlas.getTextureType() != 0) return;

        grasses.clear();
        BetterFoliageMod.log.info("Initializing Grass Registry");

        for (Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block block)) continue;

            if (!Config.blocks.INSTANCE.getGrass()
                .matchesClass(block)) continue;

            block.registerBlockIcons(location -> {
                IIcon original = atlas.getTextureExtry(location);
                BetterFoliageMod.log.info("Registering Particles and Extra Leaf textures for {}", location);

                registerGrass(original);

                return original;
            });
        }
    }

    private void registerGrass(IIcon sprite) {
        grasses.put(sprite, new GrassInfo(sprite));
    }
}
