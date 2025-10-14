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

    // What: Invoke BF code for every random display tick
    // Why: Allows us to catch random display ticks, without touching block code
    @ModifyExpressionValue(
        method = "doVoidFogParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/WorldClient;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block betterfoliage$onRandomDisplayTick(Block original, @Local(ordinal = 4) int randX,
        @Local(ordinal = 5) int randY, @Local(ordinal = 6) int randZ) {
        Hooks.onRandomDisplayTick(original, (WorldClient) (Object) this, randX, randY, randZ);
        return original;
    }
}
