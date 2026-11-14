package mods.betterfoliage.mixins.late.betterfoliage.gt5u;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.render.particles.LeafParticleRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(LeafParticleRenderer.class)
public abstract class MixinLeafParticleRenderer {

    @WrapWithCondition(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lmods/betterfoliage/client/render/particles/LeafParticleRenderer;setParticleColor(II)V"),
        remap = false,
        require = 1)
    private boolean betterfoliage$gt5uOverrideParticlePollutionColor(LeafParticleRenderer instance,
        int spriteAverageColor, int calculatedBlockColor, LeafInfo leafInfo, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int baseBlockColor = block.colorMultiplier(world, x, y, z);

        // Normal leaves, handled by the default colorizer
        if (baseBlockColor != RenderUtils.DEFAULT_COLOR_MULTIPLIER) return true;

        // alpha == 0xFF, not polluted, handled by default
        int pollutedMask = GT5UIntegration.getPollutedTintColorMask(spriteAverageColor, block, x, z);
        if ((pollutedMask & RenderUtils.ALPHA_MASK) == RenderUtils.ALPHA_MASK) return true;

        ((LeafParticleRenderer) (Object) this).setRGBColor(pollutedMask);
        return false;
    }
}
