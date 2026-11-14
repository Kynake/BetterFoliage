package mods.betterfoliage.mixins.late.betterfoliage.gt5u;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.render.particles.LeafParticleRenderer;
import mods.betterfoliage.utils.BlockUtils;

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
        int spriteAverageColor, int blockColor, LeafInfo leafInfo, World world, int x, int y, int z) {
        if (GT5UIntegration.isStandardBlockClean(world, x, y, z)) return true;

        ((LeafParticleRenderer) (Object) this).setRGBColor(BlockUtils.getStandardColorMultiplier(world, x, y, z));
        return false;
    }
}
