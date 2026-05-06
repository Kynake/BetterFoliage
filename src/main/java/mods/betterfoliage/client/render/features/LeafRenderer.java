package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.ClientRegistry;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.octarinecore.client.render.BlockContext;

public class LeafRenderer extends BlockRenderer {

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
        // ICrossedSquaresRenderer leafRenderer = (ICrossedSquaresRenderer) renderer;

        double scale = Config.leaves.INSTANCE.getSize();

        Tessellator tessellator = Tessellator.instance;

        // TODO Refactor rendering to use AO:
        // AO from leaf block OR maybe,
        // AO from each corner of surrounding blocks, if it appears to look good (and fixes the black leaves issue)
        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y, z));

        renderer.drawCrossedSquares(leaf.getSpriteForCoord(x, y, z), x, y - scale / 4.0, z, (float) scale);

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
