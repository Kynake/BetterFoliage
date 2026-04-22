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
import mods.betterfoliage.client.render.Utils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

public class MyceliumRenderer extends BlockRenderer {

    // TODO Reminder special cases:
    // Grass shader wind

    private static MyceliumRenderer instance;

    private final ISpriteProvider myceliumGrass = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_mycel_",
        ".png");

    public static MyceliumRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! MyceliumRenderer");
            instance = new MyceliumRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.shortGrass.INSTANCE.getMyceliumEnabled() && ctx.getBlock() == Blocks.mycelium;
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

        Block blockAbove = world.getBlock(x, y + 1, z);
        boolean hasSnowAbove = Utils.isSnow(blockAbove);

        if (hasSnowAbove && !Config.connectedGrass.INSTANCE.getSnowEnabled()) return renderResult;
        if (blockAbove.isOpaqueCube()) return renderResult;

        // Render short mycelium
        float myceliumHeight = y + 1;
        if (hasSnowAbove) {
            myceliumHeight += GrassRenderer.SNOW_HEIGHT_OFFSET;
        }

        int coordHash = MathUtils.hashCoords(x, y + 1, z);
        double heightScale = MathUtils.hashToRange(
            coordHash,
            Config.shortGrass.INSTANCE.getHeightMin(),
            Config.shortGrass.INSTANCE.getHeightMax());

        double hOffset = Config.shortGrass.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        IIcon sprite = myceliumGrass.getSpriteForCoord(x, y + 1, z);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        ICrossedSquaresRenderer myceliumRenderer = (ICrossedSquaresRenderer) renderer;
        myceliumRenderer.betterfoliage$setVerticalScale((float) heightScale);
        myceliumRenderer.betterfoliage$setIsRenderingCrossedSquares(true);

        renderer.drawCrossedSquares(sprite, xOffset, myceliumHeight, zOffset, GrassRenderer.SHORT_GRASS_SCALE);

        myceliumRenderer.betterfoliage$setIsRenderingCrossedSquares(false);

        return true;
    }
}
