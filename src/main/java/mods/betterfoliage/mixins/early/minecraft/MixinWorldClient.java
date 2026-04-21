package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(WorldClient.class)
public abstract class MixinWorldClient {

    @ModifyExpressionValue(
        method = "doVoidFogParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/WorldClient;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block betterfoliage$onRandomDisplayTick(Block original, @Local(name = "i1") int x,
        @Local(name = "j1") int y, @Local(name = "k1") int z) {
        Hooks.onRandomDisplayTick(original, (WorldClient) (Object) this, x, y, z);
        return original;
    }
}
