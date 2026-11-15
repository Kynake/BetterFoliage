package mods.betterfoliage.client.integration;

import net.minecraft.block.Block;

import gregtech.common.pollution.ColorOverrideType;
import gregtech.common.pollution.Pollution;
import gregtech.common.pollution.PollutionConfig;
import mods.betterfoliage.client.render.RenderUtils;

public final class GT5UIntegration {

    public static int applyPollutionTintStandard(int baseColor, Block block, int x, int z) {
        if (!PollutionConfig.pollution || Pollution.standardBlocks == null) return baseColor;

        final ColorOverrideType colorOverride = Pollution.standardBlocks.matchesID(block);
        if (colorOverride == null) return baseColor;

        return colorOverride.getColor(baseColor, x, z);
    }

    public static int getPollutedTintColorMask(int baseColor, Block block, int x, int z) {
        int pollutedColor = applyPollutionTintStandard(baseColor, block, x, z);
        if (pollutedColor == baseColor) {
            // Not polluted, set alpha to 0xFF
            return baseColor | RenderUtils.ALPHA_MASK;
        }

        // Polluted
        return pollutedColor;
    }

    public static int applyPollutionTintFlower(int baseColor, int x, int z) {
        return applyPollutionTint(ColorOverrideType.FLOWER, baseColor, x, z);
    }

    public static int applyPollutionTintLeaf(int baseColor, int x, int z) {
        return applyPollutionTint(ColorOverrideType.LEAVES, baseColor, x, z);
    }

    private static int applyPollutionTint(ColorOverrideType type, int baseColor, int x, int z) {
        return !PollutionConfig.pollution ? baseColor : type.getColor(baseColor, x, z);
    }
}
