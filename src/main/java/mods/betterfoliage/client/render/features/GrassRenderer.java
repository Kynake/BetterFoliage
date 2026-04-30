package mods.betterfoliage.client.render.features;

import java.util.Map;

import net.minecraft.block.Block;
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
import mods.betterfoliage.client.render.Utils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.generators.ShortGrassGenerator;
import mods.betterfoliage.client.resource.generators.ShortGrassSnowGenerator;
import mods.betterfoliage.client.texture.GrassInfo;
import mods.betterfoliage.client.texture.GrassRegistry;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

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
    public boolean isEligible(BlockContext ctx) {
        return (Config.connectedGrass.INSTANCE.getEnabled() || Config.shortGrass.INSTANCE.getGrassEnabled())
            && Config.blocks.INSTANCE.getGrass()
                .matchesID(ctx.getBlock());
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
        Block blockAbove = world.getBlock(x, y + 1, z);
        boolean hasSnowAbove = Utils.isSnow(blockAbove);

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
        if (!Config.shortGrass.INSTANCE.getGrassEnabled()) return renderResult;
        if (hasSnowAbove && !Config.shortGrass.INSTANCE.getSnowEnabled()) return renderResult;
        if (blockAbove.isOpaqueCube() || blocksShortGrassRendering(world, blockAbove, x, y + 1, z)) return renderResult;

        int coordHash = MathUtils.hashCoords(x, y + 1, z);
        double heightScale = MathUtils.hashToRange(
            coordHash,
            Config.shortGrass.INSTANCE.getHeightMin(),
            Config.shortGrass.INSTANCE.getHeightMax());

        if (!renderResult) {
            // TODO: Revise this, calculations look wrong
            // TODO: replicate on mycelium renderer after revised
            float blockingHeight = (float) heightScale;

            if (hasSnowAbove) {
                blockingHeight += SNOW_HEIGHT_OFFSET;
            }

            // Block above completely covers short grass
            if (blockAbove.getBlockBoundsMinY() <= 0 && blockAbove.getBlockBoundsMaxY() >= blockingHeight) {
                return false;
            }
        }

        double shortGrassHeight = y + 1;

        ISpriteProvider provider;
        if (Config.shortGrass.INSTANCE.getUseGenerated()) {
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
        IIcon sprite = provider.getSpriteForCoord(x, y, z);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        if (hasSnowAbove) {
            tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);
        } else {
            setGrassColor(world, tessellator, block, x, y, z);
        }

        double hOffset = Config.shortGrass.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        ICrossedSquaresRenderer shortGrassRenderer = (ICrossedSquaresRenderer) renderer;

        // TODO: Add config for mixing two sprites on the same crossed square
        shortGrassRenderer.betterfoliage$setVerticalScale((float) heightScale);
        renderer.drawCrossedSquares(
            sprite,
            xOffset,
            shortGrassHeight,
            zOffset,
            (float) Config.shortGrass.INSTANCE.getSize());
        shortGrassRenderer.betterfoliage$resetVerticalScale();

        return true;
    }

    private void setGrassColor(IBlockAccess world, Tessellator tessellator, Block grassBlock, int x, int y, int z) {
        IIcon grassTopTexture = grassBlock.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());

        Map<IIcon, GrassInfo> grassMap = GrassRegistry.INSTANCE.getGrass();
        if (!grassMap.containsKey(grassTopTexture)) {
            return;
        }

        GrassInfo grass = grassMap.get(grassTopTexture);

        int color = grass.getOverrideColor() != null ? grass.getOverrideColor()
            : world.getBiomeGenForCoords(x, z)
                .getBiomeGrassColor(x, y, z);

        tessellator.setColorOpaque_I(color);
    }

    private boolean blocksShortGrassRendering(IBlockAccess world, Block blockAbove, int x, int y, int z) {
        return false;

        // TODO Enable after configurable list is implemented
        // int meta = world.getBlockMetadata(x, y, z);
        // return (blockAbove == Blocks.tallgrass && meta == 1) || (blockAbove == Blocks.double_plant && meta == 2);
    }
}
