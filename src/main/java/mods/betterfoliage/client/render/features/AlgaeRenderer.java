package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.integration.GT5UIntegration;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;
import mods.betterfoliage.utils.SimplexNoiseGenerator;

public class AlgaeRenderer extends BlockRenderer {

    private static final int SALT = 562;

    private static AlgaeRenderer instance;

    private final SimplexNoiseGenerator noise = new SimplexNoiseGenerator(SALT);
    private final ISpriteProvider algae = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_algae_",
        ".png");

    public static AlgaeRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Algae Renderer");
            instance = new AlgaeRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Configs.Algae.enabled) return false;
        if (!(BlockUtils.isWater(world.getBlock(x, y + 1, z)) && BlockUtils.isWater(world.getBlock(x, y + 2, z)))) {
            return false;
        }

        final int biomeID = world.getBiomeGenForCoords(x, z).biomeID;
        if (!Configs.Algae.biomes[biomeID]) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) return false;

        return noise.isBelowThreshold(x, z, Configs.Algae.threshold);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        boolean renderResult = renderer.renderStandardBlock(block, x, y, z);

        if (!renderResult) return false;

        // Render Algae
        final int yOffset = y + 1;

        int coordHash = MathUtils.hashCoords(x, yOffset, z, SALT);

        final float hOffset = Configs.Algae.hOffset;
        double xOffset = (double) x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = (double) z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, yOffset, z));

        final int color = GT5UIntegration.applyLeafPollutionTint(RenderUtils.DEFAULT_COLOR_MULTIPLIER, x, z);
        tessellator.setColorOpaque_I(color);

        IIcon sprite = algae.getSpriteForCoord(x, yOffset, z);

        if (Configs.Algae.mixTextures) {
            ICrossedSquaresRenderer algaeRenderer = (ICrossedSquaresRenderer) renderer;

            IIcon secondSprite = algae.getSpriteForCoord(x, yOffset, z, 6);
            algaeRenderer.betterfoliage$setSecondSprite(secondSprite);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, Configs.Algae.size);
            algaeRenderer.betterfoliage$setSecondSprite(null);
        } else {
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, Configs.Algae.size);
        }

        return true;
    }
}
