package mods.betterfoliage.client.render.features;

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
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;
import mods.betterfoliage.utils.SimplexNoiseGenerator;

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
        if (!Configs.Reeds.enabled) return false;

        if (!BlockUtils.isWater(world.getBlock(x, y + 1, z))) return false;

        if (world.getBlock(x, y + 2, z)
            .getMaterial() != Material.air) return false;

        final int biomeID = world.getBiomeGenForCoords(x, z).biomeID;
        if (!Configs.Reeds.biomes[biomeID]) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) return false;

        return noise.isBelowThreshold(x, z, Configs.Reeds.threshold);
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
        final float hOffset = Configs.Reeds.hOffset;
        double xOffset = (double) x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = (double) z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        float verticalScale = MathUtils.hashToRange(coordHash, Configs.Reeds.heightMin, Configs.Reeds.heightMax);

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
