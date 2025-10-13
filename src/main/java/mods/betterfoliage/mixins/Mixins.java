package mods.betterfoliage.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    HOOK_BLOCK_RENDERING(new MixinBuilder()
        .addClientMixins("minecraft.MixinRenderBlocks")
        .setPhase(Phase.EARLY));
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
