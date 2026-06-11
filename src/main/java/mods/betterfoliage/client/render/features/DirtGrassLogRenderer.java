package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.BlockMatcher;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.octarinecore.client.render.BlockContext;

public class DirtGrassLogRenderer extends BlockRenderer {

    private static DirtGrassLogRenderer instance;

    public static DirtGrassLogRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Connected Grass Log Renderer");
            instance = new DirtGrassLogRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Config.roundLogs.INSTANCE.getEnabled() || !Config.roundLogs.INSTANCE.getConnectGrass()) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) {
            return false;
        }

        return Config.blocks.INSTANCE.getLogs()
            .matchesID(world.getBlock(x, y + 1, z));
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Block renderBlock = block;

        BlockMatcher grass = Config.blocks.INSTANCE.getGrass();

        // East
        Block sideBlock = world.getBlock(x + 1, y, z);
        if (grass.matchesID(sideBlock)) {
            renderBlock = sideBlock;
        } else {
            // West
            sideBlock = world.getBlock(x - 1, y, z);
            if (grass.matchesID(sideBlock)) {
                renderBlock = sideBlock;
            } else {
                // North
                sideBlock = world.getBlock(x, y, z - 1);
                if (grass.matchesID(sideBlock)) {
                    renderBlock = sideBlock;
                } else {
                    // South
                    sideBlock = world.getBlock(x, y, z + 1);
                    if (grass.matchesID(sideBlock)) {
                        renderBlock = sideBlock;
                    }
                }
            }
        }

        return renderer.renderStandardBlock(renderBlock, x, y, z);
    }
}
