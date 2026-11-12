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
            "minecraft.MixinChunkCache",
            "minecraft.MixinRenderBlocks",
            "minecraft.MixinWorldClient",
            "minecraft.MixinRenderBlocks_Grass",
            "minecraft.MixinRenderBlocks_CustomSideSprites",
            "minecraft.MixinRenderBlocks_CustomSidePositions",
            "minecraft.MixinRenderBlocks_CrossedSquares",
            "minecraft.MixinRenderBlocks_RoundLogs",
            "minecraft.MixinRenderBlocks_OffsetSprite",
            "minecraftforge.MixinBlockFluidBase")
        .setPhase(Phase.EARLY)),
    // Multi-mod mixin, at least one of them must be present
    BERRY_BUSH(new MixinBuilder()
        .addClientMixins("betterfoliage.berrybush.MixinLeafRenderer")
        .setApplyIf(() -> Mod.NATURA.isLoaded() || Mod.TCON.isLoaded())
        .setPhase(Phase.LATE)),
    ANGELICA(new MixinBuilder()
        .addClientMixins(
            "angelica.MixinLightDataCache",
            "angelica.MixinWorldSlice",
            "angelica.MixinAngelicaChunkBuilderMeshingTask")
        .addRequiredMod(Mod.ANGELICA)
        .setPhase(Phase.LATE)),
    GT6(new MixinBuilder()
        .addClientMixins(
            "gt6.accessors.MixinBlockTextureCopied",
            "gt6.accessors.MixinBlockTextureDefault",
            "gt6.accessors.MixinTileEntityBase06Covers",
            "gt6.MixinBlockBase",
            "gt6.MixinBlockWaterlike",
            "gt6.MixinMultiTileEntityBlock",
            "gt6.MixinMultiTileEntityTreeHole",
            "gt6.MixinTileEntityBase01Root",
            "betterfoliage.gt6.MixinLeafRegistry",
            "betterfoliage.gt6.MixinLogRenderer")
        .addRequiredMod(Mod.GT6)
        .setPhase(Phase.LATE)),
    IC2(new MixinBuilder()
        .addClientMixins("betterfoliage.ic2.MixinLogRenderer")
        .addRequiredMod(Mod.IC2)
        .setPhase(Phase.LATE)),
    TFC(new MixinBuilder()
        .addClientMixins(
            "tfc.MixinBlockLogHoriz",
            "tfc.MixinBlockLogHoriz2",
            "betterfoliage.tfc.MixinLogRenderer",
            "betterfoliage.tfc.MixinGrassRenderer")
        .addRequiredMod(Mod.TFC)
        .setPhase(Phase.LATE)),
    NOTFINE_EARLY(new MixinBuilder()
        .addClientMixins("notfine.MixinShouldSideBeRendered")
        .addRequiredMod(Mod.NOTFINE)
        .setPhase(Phase.EARLY)),
    NOTFINE_LATE(new MixinBuilder()
        .addClientMixins("notfine.MixinLeafRenderUtil")
        .addRequiredMod(Mod.NOTFINE)
        .setPhase(Phase.LATE))
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
