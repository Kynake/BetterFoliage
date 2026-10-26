package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.Configs;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.GrassRegistry;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.generators.ShortGrassGenerator;
import mods.betterfoliage.client.resource.generators.ShortGrassSnowGenerator;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

public class GrassRenderer extends BlockRenderer {

    // TODO: Reminder SPECIAL CASES to handle:
    // Grass shader wind
    // TFC
    // Primal (Frodo's mod) <--- New Compat

    // TODO make configurable? (per snow layer height maybe)
    public static final float SNOW_HEIGHT_OFFSET = 0.0625f;

    private static GrassRenderer instance;

    // spotless:off
    private final ISpriteProvider shortGrass = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_grass_long_", ".png");

    private final ISpriteProvider shortGrassSnow = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_grass_snowed_", ".png");

    private final ISpriteProvider genGrass = new ShortGrassGenerator(
        "minecraft",
        "textures/blocks/tallgrass.png");

    private final ISpriteProvider genGrassSnow = new ShortGrassSnowGenerator(
        "minecraft",
        "textures/blocks/tallgrass.png");
    // spotless:on

    // private PartialSprite genGrass = null;

    public static GrassRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! GrassRenderer");
            instance = new GrassRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        return (Config.connectedGrass.INSTANCE.getEnabled() | Configs.ShortGrass.grassEnabled)
            && Config.blocks.INSTANCE.getGrass()
                .matchesID(world.getBlock(x, y, z));
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

        boolean isConnected = Config.connectedGrass.INSTANCE.getEnabled();
        if (isConnected) {
            isConnected = Config.connectedGrass.INSTANCE.getSnowEnabled() || !hasSnowAbove;

            if (isConnected) {
                Block blockBelow = world.getBlock(x, y - 1, z);
                isConnected = Config.blocks.INSTANCE.getDirt()
                    .matchesID(blockBelow)
                    || Config.blocks.INSTANCE.getGrass()
                        .matchesID(blockBelow);
            }
        }

        boolean renderResult;
        if (isConnected) {
            if (hasSnowAbove && Config.connectedGrass.INSTANCE.getSnowEnabled()) {
                IIcon snowTop = blockAbove.getIcon(world, x, y + 1, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(snowTop);
                renderResult = renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();
            } else {
                IGrassBlockRenderer grassRenderer = (IGrassBlockRenderer) renderer;

                IIcon grassTop = block.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(grassTop);
                grassRenderer.betterfoliage$setGrassRender(true);

                renderResult = renderer.renderStandardBlock(block, x, y, z);

                grassRenderer.betterfoliage$setGrassRender(false);
                renderer.clearOverrideBlockTexture();
            }
        } else {
            renderResult = renderer.renderStandardBlock(block, x, y, z);
        }

        // Render short grass
        if (!Configs.ShortGrass.grassEnabled) return renderResult;
        if (hasSnowAbove && !Configs.ShortGrass.snowEnabled) return renderResult;
        if (blockAbove.isOpaqueCube() || blocksShortGrassRendering(world, blockAbove, x, y + 1, z)) return renderResult;

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
            setGrassColor(world, tessellator, block, x, y, z);
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

    private void setGrassColor(IBlockAccess world, Tessellator tessellator, Block grassBlock, int x, int y, int z) {

        final IIcon grassTopTexture = grassBlock.getIcon(world, x, y, z, 1);

        final int blockMultiplier = BlockUtils.getStandardColorMultiplier(world, grassBlock, x, y, z);

        final int color = blockMultiplier == RenderUtils.DEFAULT_COLOR_MULTIPLIER ? GrassRegistry.getInstance()
            .getColorForSprite(grassTopTexture) : blockMultiplier;

        tessellator.setColorOpaque_I(color);
    }

    private boolean blocksShortGrassRendering(IBlockAccess world, Block blockAbove, int x, int y, int z) {
        return false;

        // TODO Enable after configurable list is implemented
        // int meta = world.getBlockMetadata(x, y, z);
        // return (blockAbove == Blocks.tallgrass && meta == 1) || (blockAbove == Blocks.double_plant && meta == 2);
    }
}
