package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSet;
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
        if (!Config.algae.INSTANCE.getEnabled()) return false;
        if (!(BlockUtils.isWater(world.getBlock(x, y + 1, z)) && BlockUtils.isWater(world.getBlock(x, y + 2, z)))) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) return false;

        // TODO: Use [0, 1] based range
        final float threshold = Config.algae.INSTANCE.getPopulation() / 32.0f;
        if (noise.isAboveThreshold(x, z, threshold)) return false;

        final int currentBiomeId = world.getBiomeGenForCoords(x, z).biomeID;
        for (int biomeId : Config.algae.INSTANCE.getBiomes()) {
            if (currentBiomeId == biomeId) return true;
        }

        return false;
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
        int coordHash = MathUtils.hashCoords(x, y + 1, z, SALT);
        double hOffset = Config.algae.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        IIcon sprite = algae.getSpriteForCoord(x, y + 1, z);

        // TODO: Add config for mixing two sprites on the same crossed square
        renderer.drawCrossedSquares(sprite, xOffset, y + 1, zOffset, (float) Config.algae.INSTANCE.getSize());

        return true;
    }
}
