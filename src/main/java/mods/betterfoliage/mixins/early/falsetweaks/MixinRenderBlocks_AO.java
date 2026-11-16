package mods.betterfoliage.mixins.early.falsetweaks;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public class MixinRenderBlocks_AO implements IGrassBlockRenderer, ICustomSideSpritesRenderer {

    @Override
    public void betterfoliage$setSpriteProvider(ISpriteProvider provider) {
        // TODO implement
    }

    @Override
    public void betterfoliage$resetSpriteProvider() {
        // TODO implement
    }

    @Override
    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        // TODO implement
    }
}
