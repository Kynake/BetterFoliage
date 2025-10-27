package mods.betterfoliage.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import mods.betterfoliage.client.integration.Mod;

public enum Mixins implements IMixins {

    // spotless:off
    BETTER_FOLIAGE(new MixinBuilder()
        .addClientMixins(
            "minecraft.MixinBlock",
            "minecraft.MixinRenderBlocks",
            "minecraft.MixinWorldClient")
        .setPhase(Phase.EARLY)),
    GT6(new MixinBuilder()
        .addClientMixins(
            "gt6.MixinBlockTextureCopied_IconGetter",
            "gt6.MixinBlockTextureDefault_IconGetter",
            "gt6.MixinBlockTextureFluid_IconGetter",
            "gt6.MixinBlockTextureMulti_IconGetter",
            "gt6.MixinBlockTextureSided_IconGetter",
            "gt6.MixinITextureUtil",
            "gt6.MixinMultiTileEntityTreeHole",
            "gt6.MixinTileEntityBase01Root",
            "gt6.MixinTileEntityBase06Covers_Texture2Getter")
        .addRequiredMod(Mod.GT6)
        .setPhase(Phase.LATE)),
    ;
    // spotless:on
    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
