package mods.betterfoliage.client.integration;

import net.minecraft.block.Block;

import gregtech.common.pollution.ColorOverrideType;
import gregtech.common.pollution.Pollution;
import gregtech.common.pollution.PollutionConfig;

public final class GT5UIntegration {

    public static int applyPollutionTintStandard(int baseColor, Block block, int x, int z) {
        if (!Mod.GT5U.isLoaded() || !PollutionConfig.pollution) return baseColor;

        if (Pollution.standardBlocks == null) return baseColor;

        final ColorOverrideType colorOverride = Pollution.standardBlocks.matchesID(block);
        if (colorOverride == null) return baseColor;

        return colorOverride.getColor(baseColor, x, z);
    }
}
