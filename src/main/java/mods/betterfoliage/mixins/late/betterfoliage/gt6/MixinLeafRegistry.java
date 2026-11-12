package mods.betterfoliage.mixins.late.betterfoliage.gt6;

import net.minecraft.client.renderer.texture.TextureMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.betterfoliage.client.integration.GT6Integration;
import mods.betterfoliage.client.registries.LeafRegistry;

@SuppressWarnings("UnusedMixin")
@Mixin(LeafRegistry.class)
public class MixinLeafRegistry {

    @Inject(method = "onSpriteStitch", at = @At("TAIL"), remap = false)
    private void betterfoliage$gt6RegisterLeaves(TextureMap atlas, CallbackInfo ci) {
        GT6Integration.registerGT6Leaves(atlas, (LeafRegistry) (Object) this);
    }
}
