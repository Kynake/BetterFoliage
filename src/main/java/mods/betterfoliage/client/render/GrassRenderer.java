package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.resource.TextureSet;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassColorOverride;
import mods.octarinecore.client.render.BlockContext;

public class GrassRenderer extends BlockRenderer {

    // TODO: Reminder SPECIAL CASES to handle:
    // TFC
    // Primal (Frodo's mod) <--- New Compat

    private static GrassRenderer instance;

    // spotless:off
    private final TextureSet shortGrass = new TextureSet(
        BetterFoliageMod.LEGACY_DOMAIN, "textures/blocks/better_grass_long_", ".png");
    // spotless:on

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

        // Render grass block
        boolean isConnected = Config.connectedGrass.INSTANCE.getEnabled();
        if (isConnected) {
            Block blockBelow = world.getBlock(x, y - 1, z);
            isConnected = Config.blocks.INSTANCE.getDirt()
                .matchesID(blockBelow)
                || Config.blocks.INSTANCE.getGrass()
                    .matchesID(blockBelow);
        }

        Block blockAbove = world.getBlock(x, y + 1, z);
        boolean isSnowed = Config.connectedGrass.INSTANCE.getSnowEnabled() && Utils.isSnow(blockAbove);

        IGrassColorOverride grassRenderer = (IGrassColorOverride) renderer;

        boolean renderResult;
        if (isConnected) {
            if (isSnowed) {
                IIcon snowTop = blockAbove.getIcon(world, x, y + 1, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(snowTop);
                renderResult = renderer.renderStandardBlock(block, x, y, z);
                renderer.clearOverrideBlockTexture();
            } else {
                IIcon grassTop = block.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());
                renderer.setOverrideBlockTexture(grassTop);
                grassRenderer.betterfoliage$setGrassRender(true);

                renderResult = renderer.renderStandardBlock(block, x, y, z);

                grassRenderer.betterfoliage$setGrassRender(false);
                renderer.clearOverrideBlockTexture();
            }
        } else {
            renderResult = renderer.renderStandardBlock(block, x, y, z);
        }

        if (!renderResult) return false;

        if (!Config.shortGrass.INSTANCE.getGrassEnabled()) return true;
        if (isSnowed && !Config.shortGrass.INSTANCE.getSnowEnabled()) return true;

        // TODO: Maybe don't use tallgrass noise as position variation (else it stays at the same place as tallgrass)
        // TODO: Check quad scales (currently, texture is > 16px but renders same size as tallgrass)
        // ^ Might need custom render func

        // TODO: Don't render under full blocks
        // TODO: Don't render together with tall grass (<--- new feature)

        // TODO: Generate color based on grass top texture for modded grass blocks

        // Render short grass

        // TODO: Snow variations
        renderer.setOverrideBlockTexture(shortGrass.getTextureForLocation(x, y, z));
        grassRenderer.betterfoliage$setGrassRender(true);

        renderer.renderCrossedSquares(Blocks.tallgrass, x, y + 1, z);

        grassRenderer.betterfoliage$setGrassRender(false);
        renderer.clearOverrideBlockTexture();

        return true;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.INSTANCE.getEnabled() && ctx.getCameraDistance() < Config.shortGrass.INSTANCE.getDistance()
            && (Config.shortGrass.INSTANCE.getGrassEnabled() || Config.connectedGrass.INSTANCE.getEnabled())
            && Config.blocks.INSTANCE.getGrass()
                .matchesID(ctx.getBlock());
    }
}
