package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IRendererByType;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

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
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        return Configs.ShortGrass.myceliumEnabled && world.getBlock(x, y, z) == Blocks.mycelium;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        final IRendererByType baseRenderer = (IRendererByType) renderer;
        boolean renderResult = baseRenderer.betterfoliage$renderBaseBlock(block, x, y, z);

        Block blockAbove = world.getBlock(x, y + 1, z);
        boolean hasSnowAbove = BlockUtils.isSnow(blockAbove);

        if (hasSnowAbove && !Configs.ConnectedGrass.snowEnabled) return renderResult;
        if (blockAbove.isOpaqueCube()) return renderResult;

        // Render short mycelium
        float myceliumHeight = y + 1;
        if (hasSnowAbove) {
            myceliumHeight += GrassRenderer.SNOW_HEIGHT_OFFSET;
        }

        int coordHash = MathUtils.hashCoords(x, y + 1, z);
        float heightScale = MathUtils
            .hashToRange(coordHash, Configs.ShortGrass.heightMin, Configs.ShortGrass.heightMax);

        float hOffset = Configs.ShortGrass.hOffset;
        double xOffset = (double) x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = (double) z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y + 1, z));
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        final ICrossedSquaresRenderer myceliumRenderer = (ICrossedSquaresRenderer) renderer;
        myceliumRenderer.betterfoliage$setVerticalScale(heightScale);

        IIcon sprite = myceliumGrass.getSpriteForCoord(x, y + 1, z);
        if (Configs.ShortGrass.mixTextures) {
            IIcon secondSprite = myceliumGrass.getSpriteForCoord(x, y + 1, z, 6);

            myceliumRenderer.betterfoliage$setSecondSprite(secondSprite);
            renderer.drawCrossedSquares(sprite, xOffset, myceliumHeight, zOffset, Configs.ShortGrass.size);
            myceliumRenderer.betterfoliage$setSecondSprite(null);
        } else {
            renderer.drawCrossedSquares(sprite, xOffset, myceliumHeight, zOffset, Configs.ShortGrass.size);
        }

        myceliumRenderer.betterfoliage$resetVerticalScale();

        return true;
    }
}
