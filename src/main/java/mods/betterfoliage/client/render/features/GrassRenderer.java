package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.Tags;
import mods.betterfoliage.client.registries.GrassRegistry;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.generators.ShortGrassGenerator;
import mods.betterfoliage.client.resource.generators.ShortGrassSnowGenerator;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

public class GrassRenderer extends BlockRenderer {

    // TODO: Reminder SPECIAL CASES to handle:
    // Grass shader wind
    // TFC
    // Primal (Frodo's mod) <--- New Compat

    public static final float SNOW_HEIGHT_OFFSET = 0.0625f;

    private static GrassRenderer instance;

    // spotless:off
    private final ISpriteProvider shortGrass = new SpriteSet(
        Tags.MOD_LEGACY_DOMAIN,
        "textures/blocks/better_grass_long_", ".png");

    private final ISpriteProvider shortGrassSnow = new SpriteSet(
        Tags.MOD_LEGACY_DOMAIN,
        "textures/blocks/better_grass_snowed_", ".png");

    private final ISpriteProvider genGrass = new ShortGrassGenerator(
        "minecraft",
        "textures/blocks/tallgrass.png");

    private final ISpriteProvider genGrassSnow = new ShortGrassSnowGenerator(
        "minecraft",
        "textures/blocks/tallgrass.png");
    // spotless:on

    public static GrassRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering GrassRenderer");
            instance = new GrassRenderer();
        }

        return instance;
    }

    /// Mixin target for blocking certain blocks from counting as grass as it's base block.
    public static boolean isEligibleAsGrassBase(IBlockAccess world, int x, int y, int z) {
        return Configs.Blocks.grass.matches(world, x, y, z);
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        return (RenderBlocks.fancyGrass && (Configs.ConnectedGrass.enabled | Configs.ShortGrass.grassEnabled))
            && Configs.Blocks.grass.matches(world, x, y, z);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        // Render grass block
        final Block blockAbove = world.getBlock(x, y + 1, z);
        boolean hasSnowAbove = BlockUtils.isSnow(blockAbove);

        boolean isConnected = Configs.ConnectedGrass.enabled;
        if (isConnected) {
            isConnected = Configs.ConnectedGrass.snowEnabled || !hasSnowAbove;

            if (isConnected) {
                final Block blockBelow = world.getBlock(x, y - 1, z);
                final int blockBelowMeta = world.getBlockMetadata(x, y - 1, z);
                isConnected = Configs.Blocks.dirt.matches(blockBelow, blockBelowMeta)
                    || Configs.Blocks.grass.matches(blockBelow, blockBelowMeta);
            }
        }

        boolean renderResult = renderBaseBlockWithOverrides(
            world,
            x,
            y,
            z,
            block,
            renderer,
            isConnected,
            hasSnowAbove,
            blockAbove);

        // Render short grass
        if (!Configs.ShortGrass.grassEnabled) return renderResult;
        if (hasSnowAbove && !Configs.ShortGrass.snowEnabled) return renderResult;
        if (blockAbove.isOpaqueCube() || Configs.Blocks.obstructShortGrass.matches(blockAbove, world, x, y + 1, z)) {
            return renderResult;
        }

        int coordHash = MathUtils.hashCoords(x, y + 1, z);
        float heightScale = MathUtils
            .hashToRange(coordHash, Configs.ShortGrass.heightMin, Configs.ShortGrass.heightMax);

        double shortGrassHeight = y + 1;

        ISpriteProvider provider;
        if (Configs.ShortGrass.useGenerated) {
            if (hasSnowAbove) {
                provider = genGrassSnow;
                shortGrassHeight += SNOW_HEIGHT_OFFSET;
            } else {
                provider = genGrass;
            }
        } else {
            if (hasSnowAbove) {
                provider = shortGrassSnow;
                shortGrassHeight += SNOW_HEIGHT_OFFSET;
            } else {
                provider = shortGrass;
            }
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        if (hasSnowAbove) {
            tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);
        } else {
            setGrassColor(x, y, z, world, tessellator, block);
        }

        float hOffset = Configs.ShortGrass.hOffset;
        double xOffset = (double) x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = (double) z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        ICrossedSquaresRenderer shortGrassRenderer = (ICrossedSquaresRenderer) renderer;

        shortGrassRenderer.betterfoliage$setVerticalScale(heightScale);

        IIcon sprite = provider.getSpriteForCoord(x, y, z);
        if (Configs.ShortGrass.mixTextures && !Configs.ShortGrass.useGenerated) {
            IIcon secondSprite = provider.getSpriteForCoord(x, y, z, 6);

            shortGrassRenderer.betterfoliage$setSecondSprite(secondSprite);
            renderer.drawCrossedSquares(sprite, xOffset, shortGrassHeight, zOffset, Configs.ShortGrass.size);
            shortGrassRenderer.betterfoliage$setSecondSprite(null);
        } else {
            renderer.drawCrossedSquares(sprite, xOffset, shortGrassHeight, zOffset, Configs.ShortGrass.size);
        }

        shortGrassRenderer.betterfoliage$resetVerticalScale();

        return true;
    }

    private static boolean renderBaseBlockWithOverrides(IBlockAccess world, int x, int y, int z, Block block,
        RenderBlocks renderer, boolean isConnected, boolean hasSnowAbove, Block blockAbove) {
        if (isConnected) {
            if (hasSnowAbove & Configs.ConnectedGrass.snowEnabled) {
                IIcon snowTop = blockAbove.getIcon(world, x, y + 1, z, 1);
                renderer.setOverrideBlockTexture(snowTop);
                boolean res = renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();
                return res;
            }

            IGrassBlockRenderer grassRenderer = (IGrassBlockRenderer) renderer;

            IIcon grassTop = block.getIcon(world, x, y, z, 1);
            renderer.setOverrideBlockTexture(grassTop);
            grassRenderer.betterfoliage$setGrassRender(true);

            boolean res = renderer.renderStandardBlock(block, x, y, z);

            grassRenderer.betterfoliage$setGrassRender(false);
            renderer.clearOverrideBlockTexture();

            return res;
        }

        return RenderUtils.renderBaseBlock(x, y, z, block, renderer);
    }

    private void setGrassColor(int x, int y, int z, IBlockAccess world, Tessellator tessellator, Block grassBlock) {
        final IIcon grassTopTexture = grassBlock.getIcon(world, x, y, z, 1);

        final int blockMultiplier = BlockUtils.getStandardColorMultiplier(world, grassBlock, x, y, z);

        final int color = blockMultiplier == RenderUtils.DEFAULT_COLOR_MULTIPLIER ? GrassRegistry.getInstance()
            .getColorForSprite(grassTopTexture) : blockMultiplier;

        tessellator.setColorOpaque_I(color);
    }
}
