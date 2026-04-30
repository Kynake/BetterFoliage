package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSidePositionRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.betterfoliage.utils.SimplexNoiseGenerator;
import mods.octarinecore.client.render.BlockContext;

public class CoralRenderer extends BlockRenderer {

    private static final int SALT = 6739;

    // spotless:off
    private static final ForgeDirection[] SIDES = {
        ForgeDirection.UP,
        ForgeDirection.NORTH,
        ForgeDirection.SOUTH,
        ForgeDirection.WEST,
        ForgeDirection.EAST
    };

    private static final ForgeDirection[] ROTATIONS = {
        ForgeDirection.UNKNOWN,
        ForgeDirection.EAST,
        ForgeDirection.WEST,
        ForgeDirection.SOUTH,
        ForgeDirection.NORTH
    };
    // spotless:on

    private static CoralRenderer instance;

    private final SimplexNoiseGenerator noise = new SimplexNoiseGenerator(SALT);

    private final ISpriteProvider coral = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_coral_",
        ".png");

    private final ISpriteProvider crust = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_crust_",
        ".png");

    public static CoralRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Coral Renderer");
            instance = new CoralRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        if (!Config.coral.INSTANCE.getEnabled()) return false;

        if (!Config.coral.INSTANCE.getShallowWater()) {
            Block blockTwoAbove = ctx.block(0, 2, 0);
            if (blockTwoAbove.getMaterial() != Material.water) return false;
        }

        Block blockAbove = ctx.block(0, 1, 0);
        if (blockAbove.getMaterial() != Material.water) return false;

        if (!Config.blocks.INSTANCE.getSand()
            .matchesID(ctx.getBlock())) return false;

        // TODO: Use [0, 1] based range
        float threshold = Config.coral.INSTANCE.getPopulation() / 32.0f;
        if (noise.isAboveThreshold(ctx.getX(), ctx.getZ(), threshold)) return false;

        int currentBiomeId = ctx.getBiomeId();
        for (int biomeId : Config.coral.INSTANCE.getBiomes()) {
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

        // TODO Add configurable
        // Render Crust
        int coordHash = MathUtils.hashCoords(x, y, z, SALT);

        ICustomSideSpritesRenderer customSpriteRenderer = (ICustomSideSpritesRenderer) renderer;
        customSpriteRenderer.betterfoliage$setSpriteProvider(crust);

        ICustomSidePositionRenderer customSizeRenderer = (ICustomSidePositionRenderer) renderer;
        customSizeRenderer.betterfoliage$setSidesWithCustomProperties(
            MathUtils.hashToRange(coordHash, 0.01, Config.coral.INSTANCE.getVOffset()),
            Config.coral.INSTANCE.getCrustSize() - 1.0,
            true,
            true,
            true);

        renderer.renderStandardBlock(block, x, y, z);

        customSpriteRenderer.betterfoliage$resetSpriteProvider();
        customSizeRenderer.betterfoliage$resetCustomProperties();

        // TODO Add configurable
        // Render Coral
        ICrossedSquaresRenderer coralRenderer = (ICrossedSquaresRenderer) renderer;

        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < SIDES.length; i++) {
            int xSide = x + SIDES[i].offsetX;
            int ySide = y + SIDES[i].offsetY;
            int zSide = z + SIDES[i].offsetZ;

            if (world.getBlock(xSide, ySide, zSide)
                .isOpaqueCube()) {
                continue;
            }

            int ordinal = SIDES[i].ordinal();

            IIcon sprite = coral.getSpriteForCoord(xSide, ySide, zSide, ordinal);

            double hOffset = Config.shortGrass.INSTANCE.getHOffset();
            double xOffset = xSide + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
            double zOffset = zSide + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

            tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, xSide, ySide, zSide));
            setCrossedSquareColorForSide(tessellator, ordinal);

            coralRenderer.betterfoliage$setRotation(xSide + 0.5, ySide + 0.5, zSide + 0.5, ROTATIONS[i]);
            renderer.drawCrossedSquares(sprite, xOffset, ySide, zOffset, (float) Config.coral.INSTANCE.getSize());
            coralRenderer.betterfoliage$resetRotation();
        }

        return true;
    }

    private static void setCrossedSquareColorForSide(Tessellator tessellator, int side) {
        switch (side) {
            case 0:
                tessellator.setColorOpaque_F(0.5f, 0.5f, 0.5f);
                break;

            case 2, 3:
                tessellator.setColorOpaque_F(0.8f, 0.8f, 0.8f);
                break;

            case 4, 5:
                tessellator.setColorOpaque_F(0.6f, 0.6f, 0.6f);
                break;

            default:
                tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);
        }
    }
}
