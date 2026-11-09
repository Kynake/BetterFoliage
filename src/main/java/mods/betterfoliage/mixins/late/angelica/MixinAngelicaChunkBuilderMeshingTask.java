package mods.betterfoliage.mixins.late.angelica;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizons.angelica.rendering.celeritas.AngelicaChunkBuilderMeshingTask;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(AngelicaChunkBuilderMeshingTask.class)
public class MixinAngelicaChunkBuilderMeshingTask {

    @ModifyExpressionValue(
        remap = false,
        method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isOpaqueCube()Z"))
    private boolean betterfoliage$overrideIsOpaqueCube(boolean original, @Local(name = "mcWorld") World world,
        @Local(name = "x") int x, @Local(name = "y") int y, @Local(name = "z") int z) {
        return Hooks.overrideIsOpaqueCube(world, x, y, z, original);
    }
}
