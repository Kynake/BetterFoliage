package mods.betterfoliage.mixins.late.falsetweaks;

import org.spongepowered.asm.mixin.Mixin;

import com.falsepattern.falsetweaks.modules.ao.AmbientOcclusionRenderer;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(AmbientOcclusionRenderer.class)
public abstract class MixinAmbientOcclusionRenderer implements IGrassBlockRenderer, ICustomSideSpritesRenderer {

    @Override
    public void betterfoliage$setSpriteProvider(ISpriteProvider provider) {
        // TODO Implement
    }

    @Override
    public void betterfoliage$resetSpriteProvider() {
        // TODO Implement
    }

    @Override
    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        // TODO Implement
    }
}
