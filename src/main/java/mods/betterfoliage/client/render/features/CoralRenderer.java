package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.Tags;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.SpriteSingle;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSidePositionRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;
import mods.betterfoliage.utils.SimplexNoiseGenerator;

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
        ForgeDirection.WEST,
        ForgeDirection.EAST,
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
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Configs.Coral.enabled) return false;

        if (!BlockUtils.isWater(world.getBlock(x, y + 1, z))) return false;

        if (!Configs.Coral.shallowWater) {
            if (!BlockUtils.isWater(world.getBlock(x, y + 2, z))) return false;
        }

        final int biomeID = world.getBiomeGenForCoords(x, z).biomeID;
        if (!Configs.Coral.biomes[biomeID]) {
            return false;
        }

        if (!Configs.Blocks.sand.matches(world, x, y, z)) return false;

        return noise.isBelowThreshold(x, z, Configs.Coral.threshold);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        final boolean renderResult = renderer.renderStandardBlock(block, x, y, z);

        if (!renderResult) return false;

        final int coordHash = MathUtils.hashCoords(x, y, z, SALT);

        // Render Crust
        if (Configs.Coral.renderCrust) {
            final ICustomSideSpritesRenderer customSpriteRenderer = (ICustomSideSpritesRenderer) renderer;
            customSpriteRenderer.betterfoliage$setSpriteProvider(spriteProvider);

            final ICustomSidePositionRenderer customSizeRenderer = (ICustomSidePositionRenderer) renderer;
            customSizeRenderer.betterfoliage$setSidesWithCustomProperties(
                MathUtils.hashToRange(coordHash, 0.01f, Configs.Coral.vOffset),
                Configs.Coral.crustSize - 1f,
                true,
                true,
                true);

            renderer.renderStandardBlock(block, x, y, z);

            customSpriteRenderer.betterfoliage$resetSpriteProvider();
            customSizeRenderer.betterfoliage$resetCustomProperties();
        }

        // Render Coral
        if (Configs.Coral.renderCoral) {
            final ICrossedSquaresRenderer coralRenderer = (ICrossedSquaresRenderer) renderer;

            Tessellator tessellator = Tessellator.instance;

            for (int i = 0; i < SIDES.length; i++) {
                final int ordinal = SIDES[i].ordinal();

                if (CoralSpriteProvider.shouldSkipSide(x, y, z, ordinal)) {
                    continue;
                }

                final int xSide = x + SIDES[i].offsetX;
                final int ySide = y + SIDES[i].offsetY;
                final int zSide = z + SIDES[i].offsetZ;

                if (world.getBlock(xSide, ySide, zSide)
                    .isOpaqueCube()) {
                    continue;
                }

                final float hOffset = Configs.Coral.hOffset;
                final double xOffset = (double) xSide
                    + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
                final double zOffset = (double) zSide
                    + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

                tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, xSide, ySide, zSide));
                RenderUtils.setColorMultiplierBySide(tessellator, ordinal);

                coralRenderer.betterfoliage$setRotation(xSide + 0.5, ySide + 0.5, zSide + 0.5, ROTATIONS[i]);

                final IIcon sprite = spriteProvider.getFirstCoralForCoord(x, y, z, ordinal);
                if (Configs.Coral.mixCoral) {
                    final IIcon spriteTwo = spriteProvider.getSecondCoralForCoord(x, y, z, ordinal);
                    coralRenderer.betterfoliage$setSecondSprite(spriteTwo);
                    renderer.drawCrossedSquares(sprite, xOffset, ySide, zOffset, Configs.Coral.coralSize);
                    coralRenderer.betterfoliage$setSecondSprite(null);
                } else {
                    renderer.drawCrossedSquares(sprite, xOffset, ySide, zOffset, Configs.Coral.coralSize);
                }

                coralRenderer.betterfoliage$resetRotation();
            }
        }

        return true;
    }

    private static class CoralSpriteProvider implements ISpriteProvider {

        private final ISpriteProvider coral = new SpriteSet(
            Tags.MOD_LEGACY_DOMAIN,
            "textures/blocks/better_coral_",
            ".png");

        private final ISpriteProvider crust = new SpriteSet(
            Tags.MOD_LEGACY_DOMAIN,
            "textures/blocks/better_crust_",
            ".png");

        private final ISpriteProvider empty = new SpriteSingle(Tags.MOD_DOMAIN, "textures/blocks/empty.png");

        CoralSpriteProvider() {}

        @Override
        public IIcon getSpriteForCoord(int x, int y, int z, int side) {
            if (shouldSkipSide(x, y, z, side)) {
                return empty.getSpriteForCoord(x, y, z, side);
            }

            // Copy UP direction if mix is disabled
            final int crustSide = Configs.Coral.mixCrust ? side : 1;
            return crust.getSpriteForCoord(x, y, z, crustSide);
        }

        public IIcon getFirstCoralForCoord(int x, int y, int z, int side) {
            return coral.getSpriteForCoord(x, y, z, side);
        }

        public IIcon getSecondCoralForCoord(int x, int y, int z, int side) {
            return coral.getSpriteForCoord(x, y, z, side + 6);
        }

        public static boolean shouldSkipSide(int x, int y, int z, int side) {
            int hash = MathUtils.hashCoords(x, y, z, side);

            return (MathUtils.hashInverseLerp(hash) > Configs.Coral.chance);
        }
    }
}
