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
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

public class NetherrackRenderer extends BlockRenderer {

    private static NetherrackRenderer instance;

    private final ISpriteProvider netherrackVines = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_netherrack_",
        ".png");

    public static NetherrackRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Netherrack Renderer");
            instance = new NetherrackRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.netherrack.INSTANCE.getEnabled() && ctx.getBlock() == Blocks.netherrack;
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
        if (world.getBlock(x, y - 1, z)
            .isOpaqueCube()) return false;

        // Render vines
        int coordHash = MathUtils.hashCoords(x, y - 1, z, 29);
        double heightScale = MathUtils.hashToRange(
            coordHash,
            Config.netherrack.INSTANCE.getHeightMin(),
            Config.netherrack.INSTANCE.getHeightMax());

        double hOffset = Config.netherrack.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y - 1, z));
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        // TODO: Add config for mixing two sprites on the same crossed square
        IIcon sprite = netherrackVines.getSpriteForCoord(x, y - 1, z);

        ICrossedSquaresRenderer vinesRenderer = (ICrossedSquaresRenderer) renderer;

        vinesRenderer.betterfoliage$setVerticalScale((float) heightScale);
        renderer.drawCrossedSquares(
            sprite,
            xOffset,
            y - heightScale,
            zOffset,
            (float) Config.netherrack.INSTANCE.getSize());
        vinesRenderer.betterfoliage$resetVerticalScale();

        return true;
    }
}
