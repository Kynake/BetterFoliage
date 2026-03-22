package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        boolean isConnected = Config.connectedGrass.INSTANCE.getEnabled();
        if(isConnected) {
            Block blockBelow = world.getBlock(x, y - 1, z);
            isConnected = Config.blocks.INSTANCE.getDirt().matchesID(blockBelow) ||
                Config.blocks.INSTANCE.getGrass().matchesID(blockBelow);
        }

        boolean renderResult;
        if (isConnected) {

            Block blockAbove = world.getBlock(x, y + 1, z);

            boolean isSnowed = Config.connectedGrass.INSTANCE.getSnowEnabled() && (
                blockAbove.getMaterial() == Material.snow || blockAbove.getMaterial() == Material.craftedSnow);

            if(isSnowed) {
                IIcon snowTop = blockAbove.getIcon(world, x, y + 1, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(snowTop);
                renderResult = renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();
            }
            else {
                IIcon grassTop = block.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(grassTop);

                IGrassColorOverride grassRenderer = (IGrassColorOverride) renderer;
                grassRenderer.betterfoliage$forceGrassColor(true);

                renderResult = renderer.renderStandardBlock(block, x, y, z);

                grassRenderer.betterfoliage$forceGrassColor(false);
                renderer.clearOverrideBlockTexture();
            }
        }
        else {
            renderResult = renderer.renderStandardBlock(block, x, y, z);
        }

        // TODO render short grass

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
