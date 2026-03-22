package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassColorOverride;
import mods.octarinecore.client.render.BlockContext;

public class GrassRenderer extends BlockRenderer {

    private static GrassRenderer instance;

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

        IGrassColorOverride grassRenderer = (IGrassColorOverride) renderer;

        grassRenderer.betterfoliage$forceGrassColor(true);
        renderer.setOverrideBlockTexture(grassTop);

        boolean renderResult = renderer.renderStandardBlock(block, x, y, z);

        grassRenderer.betterfoliage$forceGrassColor(false);
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
