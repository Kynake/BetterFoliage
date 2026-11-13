package mods.betterfoliage.mixins.late.betterfoliage.gt6;

import net.minecraft.client.renderer.texture.TextureMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.betterfoliage.client.integration.GT6Integration;
import mods.betterfoliage.client.registries.GrassRegistry;

@SuppressWarnings("UnusedMixin")
@Mixin(GrassRegistry.class)
public abstract class MixinGrassRegistry {

    @Inject(method = "onSpriteStitch", at = @At("TAIL"), remap = false, require = 1)
    private void betterfoliage$gt6RegisterGrass(TextureMap atlas, CallbackInfo ci) {
        GT6Integration.registerGT6Grass(atlas, (GrassRegistry) (Object) this);
    }
}
