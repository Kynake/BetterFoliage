package mods.betterfoliage.mixins.late.angelica;

import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.gtnewhorizons.angelica.rendering.celeritas.light.LightDataCache;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(LightDataCache.class)
public abstract class MixinLightDataCache {

    @ModifyVariable(method = "compute", name = "op", at = @At("STORE"), remap = false)
    private boolean betterfoliage$overrideIsOpaqueCube(boolean isOpaque, @Local(argsOnly = true, name = "x") int x,
        @Local(argsOnly = true, name = "y") int y, @Local(argsOnly = true, name = "z") int z,
        @Local(name = "world") IBlockAccess world) {
        return Hooks.overrideIsOpaqueCube(world, x, y, z, isOpaque);
    }
}
