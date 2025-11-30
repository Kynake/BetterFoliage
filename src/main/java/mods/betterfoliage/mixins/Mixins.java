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
            "gt6.accessors.MixinBlockTextureCopied",
            "gt6.accessors.MixinBlockTextureDefault",
            "gt6.accessors.MixinBlockTextureFluid",
            "gt6.accessors.MixinBlockTextureMulti",
            "gt6.accessors.MixinBlockTextureSided",
            "gt6.accessors.MixinTileEntityBase06Covers",
            "gt6.MixinITextureUtil",
            "gt6.MixinMultiTileEntityTreeHole",
            "gt6.MixinTileEntityBase01Root")
        .addRequiredMod(Mod.GT6)
        .setPhase(Phase.LATE)),
    NOTFINE(new MixinBuilder()
        .addClientMixins("notfine.MixinLeafRenderUtil")
        .addRequiredMod(Mod.NOTFINE)
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
