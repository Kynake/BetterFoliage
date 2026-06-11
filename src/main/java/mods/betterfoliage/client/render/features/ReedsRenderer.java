package mods.betterfoliage.client.render.features;

import mods.betterfoliage.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.generators.ReedsGenerator;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.betterfoliage.utils.SimplexNoiseGenerator;
import mods.octarinecore.client.render.BlockContext;

public class ReedsRenderer extends BlockRenderer {

    private static final int SALT = 1754;

    private static ReedsRenderer instance;

    private final SimplexNoiseGenerator noise = new SimplexNoiseGenerator(SALT);

    private final ISpriteProvider reeds = new ReedsGenerator(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_reed_",
        ".png");

    public static ReedsRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Reeds Renderer");
            instance = new ReedsRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Config.reed.INSTANCE.getEnabled()) return false;

        if (!BlockUtils.isWater(world.getBlock(x, y + 1, z))) return false;

        if (world.getBlock(x, y + 2, z).getMaterial() != Material.air) return false;

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) return false;

        // TODO: Use [0, 1] based range
        final float threshold = Config.reed.INSTANCE.getPopulation() / 32.0f;
        if (noise.isAboveThreshold(x, z, threshold)) return false;

        final int currentBiomeId = world.getBiomeGenForCoords(x, z).biomeID;
        for (int biomeId : Config.reed.INSTANCE.getBiomes()) {
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
        double hOffset = Config.reed.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        float verticalScale = (float) MathUtils
            .hashToRange(coordHash, Config.reed.INSTANCE.getHeightMin(), Config.reed.INSTANCE.getHeightMax());

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 2, z));
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        IIcon sprite = reeds.getSpriteForCoord(x, y + 1, z);

        ICrossedSquaresRenderer reedsRenderer = (ICrossedSquaresRenderer) renderer;
        reedsRenderer.betterfoliage$setVerticalScale(verticalScale);
        renderer.drawCrossedSquares(
            sprite,
            xOffset,
            y + 1,
            zOffset,
            (float) Config.algae.INSTANCE.getSize() * ReedsGenerator.HORIZONTAL_SCALING);
        reedsRenderer.betterfoliage$resetVerticalScale();

        return true;
    }
}
