package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.resource.TextureProvider;
import mods.betterfoliage.client.resource.TextureSet;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

public class GrassRenderer extends BlockRenderer {

    // TODO: Reminder SPECIAL CASES to handle:
    // TFC
    // Primal (Frodo's mod) <--- New Compat

    // TODO make configurable? (per snow layer height maybe)
    private static final float SNOW_HEIGHT_OFFSET = 0.0625f;

    private static GrassRenderer instance;

    // spotless:off
    private final TextureProvider shortGrass = new TextureSet(
        BetterFoliageMod.LEGACY_DOMAIN, "textures/blocks/better_grass_long_", ".png");

    private final TextureProvider shortGrassSnow = new TextureSet(
        BetterFoliageMod.LEGACY_DOMAIN, "textures/blocks/better_grass_snowed_", ".png");
    // spotless:on

    public static GrassRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! GrassRenderer");
            instance = new GrassRenderer();
        }

        return instance;
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
        boolean isConnected = Config.connectedGrass.INSTANCE.getEnabled();
        if (isConnected) {
            Block blockBelow = world.getBlock(x, y - 1, z);
            isConnected = Config.blocks.INSTANCE.getDirt()
                .matchesID(blockBelow)
                || Config.blocks.INSTANCE.getGrass()
                    .matchesID(blockBelow);
        }

        Block blockAbove = world.getBlock(x, y + 1, z);
        boolean hasSnowAbove = Utils.isSnow(blockAbove);

        IGrassBlockRenderer grassRenderer = (IGrassBlockRenderer) renderer;

        boolean renderResult;
        if (isConnected) {
            if (hasSnowAbove && Config.connectedGrass.INSTANCE.getSnowEnabled()) {
                IIcon snowTop = blockAbove.getIcon(world, x, y + 1, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(snowTop);
                renderResult = renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();
            } else {
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

        if (!renderResult) return false;

        if (!Config.shortGrass.INSTANCE.getGrassEnabled()) return true;
        if (hasSnowAbove && !Config.shortGrass.INSTANCE.getSnowEnabled()) return true;

        if (blockAbove.isOpaqueCube() || blocksShortGrassRendering(world, blockAbove, x, y + 1, z)) return true;

        // TODO: Generate color based on grass top texture for modded grass blocks
        // TODO: Implement generated grass IICons

        // Render short grass

        double shortGrassHeight = y + 1;

        TextureProvider texProvider;
        if (hasSnowAbove) {
            texProvider = shortGrassSnow;
            shortGrassHeight += SNOW_HEIGHT_OFFSET;
        } else {
            texProvider = shortGrass;
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        if (hasSnowAbove) {
            tessellator.setColorOpaque(255, 255, 255);
        } else {
            setGrassColor(world, tessellator, block, x, y, z);
        }

        int coordHash = MathUtils.hashCoords(x, y + 1, z);
        double heightScale = MathUtils.hashToRange(
            coordHash,
            Config.shortGrass.INSTANCE.getHeightMin(),
            Config.shortGrass.INSTANCE.getHeightMax());
        grassRenderer.betterfoliage$setShortVerticalGrassScale((float) heightScale);

        double hOffset = Config.shortGrass.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        grassRenderer.betterfoliage$setGrassRender(true);
        renderer.drawCrossedSquares(texProvider.getTextureForCoord(x, y, z), xOffset, shortGrassHeight, zOffset, 1F);
        grassRenderer.betterfoliage$setGrassRender(false);

        return true;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.INSTANCE.getEnabled() && ctx.getCameraDistance() < Config.shortGrass.INSTANCE.getDistance()
            && (Config.shortGrass.INSTANCE.getGrassEnabled() || Config.connectedGrass.INSTANCE.getEnabled())
            && Config.blocks.INSTANCE.getGrass()
                .matchesID(ctx.getBlock());
    }

    private void setGrassColor(IBlockAccess world, Tessellator tessellator, Block grassBlock, int x, int y, int z) {
        // TODO: consider custom grass blocks and colors here
        int color = world.getBiomeGenForCoords(x, z)
            .getBiomeGrassColor(x, y, z);
        tessellator.setColorOpaque_I(color);
    }

    // TODO Turn into configurable list
    private boolean blocksShortGrassRendering(IBlockAccess world, Block blockAbove, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return (blockAbove == Blocks.tallgrass && meta == 1) || (blockAbove == Blocks.double_plant && meta == 2);
    }
}
