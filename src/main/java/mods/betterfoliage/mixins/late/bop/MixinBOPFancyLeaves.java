package mods.betterfoliage.mixins.late.bop;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import biomesoplenty.common.blocks.BlockBOPAppleLeaves;
import biomesoplenty.common.blocks.BlockBOPColorizedLeaves;
import biomesoplenty.common.blocks.BlockBOPLeaves;
import biomesoplenty.common.blocks.BlockBOPPersimmonLeaves;
import mods.betterfoliage.config.Configs;

@SuppressWarnings("UnusedMixin")
@Mixin(
    // spotless:off
    value = {
        BlockBOPAppleLeaves.class,
        BlockBOPColorizedLeaves.class,
        BlockBOPLeaves.class,
        BlockBOPPersimmonLeaves.class,
    })
    // spotless:on
public abstract class MixinBOPFancyLeaves {

    @ModifyExpressionValue(
        method = "registerBlockIcons",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/Loader;isModLoaded(Ljava/lang/String;)Z",
            remap = false),
        require = 1)
    private boolean betterfoliage$overrideBOPFancyLeaves(boolean original) {
        return Configs.ModCompat.bopUseRoundLeaves;
    }
}
