package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.IOffsetSpriteRenderer;

public class DirtGrassLogRenderer extends BlockRenderer {

    private static final ForgeDirection[] SIDES = { ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH,
        ForgeDirection.SOUTH };

    private static DirtGrassLogRenderer instance;

    public static DirtGrassLogRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering Connected Grass Log Renderer");
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

        for (final ForgeDirection side : SIDES) {
            int sideX = x + side.offsetX;
            int sideY = y + side.offsetY;
            int sideZ = z + side.offsetZ;
            if (GrassRenderer.isEligibleAsGrassBase(world, sideX, sideY, sideZ)) {
                IOffsetSpriteRenderer offsetRenderer = (IOffsetSpriteRenderer) renderer;
                Block sideBlock = world.getBlock(sideX, sideY, sideZ);

                offsetRenderer.betterfoliage$setSpriteOffset(side.offsetX, side.offsetY, side.offsetZ);
                boolean renderResult = RenderUtils.renderBaseBlock(x, y, z, sideBlock, renderer);
                offsetRenderer.betterfoliage$setSpriteOffset(0, 0, 0);

                return renderResult;
            }
        }

        return RenderUtils.renderBaseBlock(x, y, z, block, renderer);
    }
}
