package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IOffsetSpriteRenderer;
import mods.betterfoliage.utils.BlockUtils;
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
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        if (!Config.connectedGrass.INSTANCE.getEnabled()) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getDirt()
            .matchesID(world.getBlock(x, y, z))) {
            return false;
        }

        if (!Config.blocks.INSTANCE.getGrass()
            .matchesID(world.getBlock(x, y + 1, z))) {
            return false;
        }

        // If snow grass is disabled, don't render if the grass block above is rendering normally
        if (!Config.connectedGrass.INSTANCE.getSnowEnabled()) {
            return !BlockUtils.isSnow(world.getBlock(x, y + 2, z));
        }

        return true;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        Block grassAbove = world.getBlock(x, y + 1, z);

        IOffsetSpriteRenderer offsetRenderer = (IOffsetSpriteRenderer) renderer;

        offsetRenderer.betterfoliage$setSpriteOffset(0, 1, 0);
        boolean renderResult = renderer.renderStandardBlock(grassAbove, x, y, z);
        offsetRenderer.betterfoliage$setSpriteOffset(0, 0, 0);

        return renderResult;
    }
}
