package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.render.BlockContext;

public class GrassRenderer extends BlockRenderer {

    private static GrassRenderer instance;

    public static boolean forceBlockColor = false;

    public static GrassRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! GrassRenderer");
            instance = new GrassRenderer();
        }

        return instance;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        IIcon grassTop = block.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());

        forceBlockColor = true;
        renderer.setOverrideBlockTexture(grassTop);

        boolean renderResult = renderer.renderStandardBlock(block, x, y, z);

        forceBlockColor = false;
        renderer.clearOverrideBlockTexture();

        return renderResult;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.INSTANCE.getEnabled() && ctx.getCameraDistance() < Config.shortGrass.INSTANCE.getDistance()
            && (Config.shortGrass.INSTANCE.getGrassEnabled() || Config.connectedGrass.INSTANCE.getEnabled())
            && Config.blocks.INSTANCE.getGrass()
                .matchesID(ctx.getBlock());
    }
}
