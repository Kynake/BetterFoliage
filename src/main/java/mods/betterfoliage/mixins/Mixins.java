package mods.betterfoliage.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import mods.betterfoliage.client.integration.Mod;

public enum Mixins implements IMixins {

    // TODO: Add logic checks for mixins that don't need to apply depending the features enabled
    // (To be done after configs refactor)

    // spotless:off
    BETTER_FOLIAGE(new MixinBuilder()
        .addClientMixins(
            "minecraft.MixinBlock",
            "minecraft.MixinRenderBlocks",
            "minecraft.MixinWorldClient",
            "minecraft.MixinRenderBlocks_Grass",
            "minecraft.MixinRenderBlocks_CustomSideSprites",
            "minecraft.MixinRenderBlocks_CustomSidePositions",
            "minecraft.MixinRenderBlocks_CrossedSquares",
            "minecraft.MixinRenderBlocks_OffsetSprite")
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
    NOTFINE_EARLY(new MixinBuilder()
        .addClientMixins("notfine.MixinShouldSideBeRendered")
        .addRequiredMod(Mod.NOTFINE)
        .setPhase(Phase.EARLY)),
    NOTFINE_LATE(new MixinBuilder()
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
