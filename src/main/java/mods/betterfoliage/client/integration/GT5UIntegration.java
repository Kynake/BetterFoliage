package mods.betterfoliage.client.integration;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import gregtech.common.pollution.ColorOverrideType;
import gregtech.common.pollution.Pollution;
import gregtech.common.pollution.PollutionConfig;

public final class GT5UIntegration {

    public static int applyPollutionTintStandard(int baseColor, Block block, int x, int z) {
        if (!PollutionConfig.pollution || Pollution.standardBlocks == null) return baseColor;

        final ColorOverrideType colorOverride = Pollution.standardBlocks.matchesID(block);
        if (colorOverride == null) return baseColor;

        return colorOverride.getColor(baseColor, x, z);
    }

    public static boolean isStandardBlockClean(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int baseColor = block.colorMultiplier(world, x, y, z);
        return baseColor == applyPollutionTintStandard(baseColor, block, x, z);
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
