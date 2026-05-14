package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.ClientRegistry;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.octarinecore.client.render.BlockContext;

public class LeafRenderer extends BlockRenderer {

    // Multiply Horizontal scale by this factor to ensure the horizontal scale equals the vertical one.
    private static final float HORIZONTAL_SCALE_FACTOR = 0.5f / 0.45f;

    // Original renderer scaled the vertical axis so that the squares o the diagonal look square instead of rectangular.
    private static final float VERTICAL_SCALE_FACTOR = 1.41f;

    private static LeafRenderer instance;

    public static LeafRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Leaf Renderer");
            instance = new LeafRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.leaves.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getLeaves()
            .matchesID(ctx.getBlock())
            && (!Config.leaves.INSTANCE.getSurfaceOnly()
                || isExposed(ctx.getWorld(), ctx.getBlock(), ctx.getX(), ctx.getY(), ctx.getZ()));
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

        IIcon keySprite = block.getIcon(world, x, y, z, ForgeDirection.DOWN.ordinal());
        LeafInfo leaf = ClientRegistry.getLeafRegistry()
            .getLeafForSprite(keySprite);

        if (leaf == null) return true;

        // Render Round Leaves
        float scale = (float) Config.leaves.INSTANCE.getSize();
        float verticalScale = scale * VERTICAL_SCALE_FACTOR;

        int color = block.colorMultiplier(world, x, y, z);
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;

        ICrossedSquaresRenderer leafRenderer = (ICrossedSquaresRenderer) renderer;

        leafRenderer.betterfoliage$setVerticalScale(verticalScale);
        leafRenderer.betterfoliage$setAORender(block, x, y, z, r, g, b);
        renderer.drawCrossedSquares(
            leaf.getSpriteForCoord(x, y, z),
            x,
            y + (1f - verticalScale) / 2f,
            z,
            scale * HORIZONTAL_SCALE_FACTOR);
        leafRenderer.betterfoliage$resetAORender();
        leafRenderer.betterfoliage$resetVerticalScale();

        return true;
    }

    private static boolean isExposed(IBlockAccess world, Block block, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            Block blockSide = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (!blocksLeafRendering(blockSide, block)) return true;
        }

        return false;
    }

    private static boolean blocksLeafRendering(Block visonBlocker, Block leaf) {
        return visonBlocker.isOpaqueCube() || visonBlocker == leaf;
    }
}
