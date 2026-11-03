package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.config.Configs;

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
        if (!(Configs.RoundLogs.connectGrass & LogRenderer.isRoundLog(world, x, y + 1, z))) {
            return false;
        }

        return Configs.Blocks.dirt.matches(world, x, y, z);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Block renderBlock = block;

        // East
        Block sideBlock = world.getBlock(x + 1, y, z);
        if (Configs.Blocks.grass.matches(sideBlock, world, x + 1, y, z)) {
            renderBlock = sideBlock;
        } else {
            // West
            sideBlock = world.getBlock(x - 1, y, z);
            if (Configs.Blocks.grass.matches(sideBlock, world, x + 1, y, z)) {
                renderBlock = sideBlock;
            } else {
                // North
                sideBlock = world.getBlock(x, y, z - 1);
                if (Configs.Blocks.grass.matches(sideBlock, world, x, y, z - 1)) {
                    renderBlock = sideBlock;
                } else {
                    // South
                    sideBlock = world.getBlock(x, y, z + 1);
                    if (Configs.Blocks.grass.matches(sideBlock, world, x, y, z + 1)) {
                        renderBlock = sideBlock;
                    }
                }
            }
        }

        return renderer.renderStandardBlock(renderBlock, x, y, z);
    }
}
