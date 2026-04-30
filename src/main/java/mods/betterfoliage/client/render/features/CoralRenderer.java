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
import mods.betterfoliage.client.resource.EmptySprite;
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

    private final CoralSpriteProvider spriteProvider = new CoralSpriteProvider();

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
        customSpriteRenderer.betterfoliage$setSpriteProvider(spriteProvider);

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
            int ordinal = SIDES[i].ordinal();

            if (!CoralSpriteProvider.shouldRenderSide(x, y, z, ordinal)) {
                continue;
            }

            int xSide = x + SIDES[i].offsetX;
            int ySide = y + SIDES[i].offsetY;
            int zSide = z + SIDES[i].offsetZ;

            if (world.getBlock(xSide, ySide, zSide)
                .isOpaqueCube()) {
                continue;
            }

            // TODO: Add config using only one sprite on the same crossed square
            IIcon spriteOne = spriteProvider.getFirstCoralForCoord(xSide, ySide, zSide, ordinal);
            IIcon spriteTwo = spriteProvider.getSecondCoralForCoord(xSide, ySide, zSide, ordinal);

            double hOffset = Config.shortGrass.INSTANCE.getHOffset();
            double xOffset = xSide + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
            double zOffset = zSide + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

            tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, xSide, ySide, zSide));
            setCrossedSquareColorForSide(tessellator, ordinal);

            coralRenderer.betterfoliage$setSecondSprite(spriteTwo);
            coralRenderer.betterfoliage$setRotation(xSide + 0.5, ySide + 0.5, zSide + 0.5, ROTATIONS[i]);
            renderer.drawCrossedSquares(spriteOne, xOffset, ySide, zOffset, (float) Config.coral.INSTANCE.getSize());
            coralRenderer.betterfoliage$resetRotation();
            coralRenderer.betterfoliage$setSecondSprite(null);
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

    private static class CoralSpriteProvider implements ISpriteProvider {

        private final ISpriteProvider coral = new SpriteSet(
            BetterFoliageMod.LEGACY_DOMAIN,
            "textures/blocks/better_coral_",
            ".png");

        private final ISpriteProvider crust = new SpriteSet(
            BetterFoliageMod.LEGACY_DOMAIN,
            "textures/blocks/better_crust_",
            ".png");

        CoralSpriteProvider() {}

        @Override
        public IIcon getSpriteForCoord(int x, int y, int z, int side) {
            // TODO: Add option for rendering the same crust sprite on all sides (legacy behaviour)
            return shouldRenderSide(x, y, z, side) ? crust.getSpriteForCoord(x, y, z, side) : EmptySprite.getInstance();
        }

        public IIcon getFirstCoralForCoord(int x, int y, int z, int side) {
            return coral.getSpriteForCoord(x, y, z, side);
        }

        public IIcon getSecondCoralForCoord(int x, int y, int z, int side) {
            return coral.getSpriteForCoord(x, y, z, side + 6);
        }

        public static boolean shouldRenderSide(int x, int y, int z, int side) {
            int hash = MathUtils.hashCoords(x, y, z, side);

            // TODO convert to [0, 1] range RNG
            return MathUtils.hashToRange(hash, 0, 64) > Config.coral.INSTANCE.getChance();
        }
    }
}
