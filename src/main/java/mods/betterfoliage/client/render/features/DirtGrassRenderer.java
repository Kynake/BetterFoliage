package mods.betterfoliage.client.render.features;

import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.render.BlockContext;

public class DirtGrassRenderer extends BlockRenderer {

    private static DirtGrassRenderer instance;

    public static DirtGrassRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Connected Grass Renderer");
            instance = new DirtGrassRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext context) {
        if (!Config.INSTANCE.getEnabled() || !Config.connectedGrass.INSTANCE.getEnabled()) {
            return false;
        }

        Block block = context.getBlock();
        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(block)) {
            return false;
        }

        Block blockAbove = context.block(0, 1, 0);
        if (!Config.blocks.INSTANCE.getGrass()
            .matchesID(blockAbove)) {
            return false;
        }

        // If snow grass is disabled, don't render if the grass block above is rendering normally
        if (!Config.connectedGrass.INSTANCE.getSnowEnabled()) {
            Block blockTwoAbove = context.block(0, 2, 0);
            return !Utils.isSnow(blockTwoAbove);
        }

        return true;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // TODO: fix not rendering snow grass if snow 2 blocks above
        // (needs offset block access when getting IIcon (mixin?))
        Block grassAbove = world.getBlock(x, y + 1, z);
        return renderer.renderStandardBlock(grassAbove, x, y, z);
    }
}
