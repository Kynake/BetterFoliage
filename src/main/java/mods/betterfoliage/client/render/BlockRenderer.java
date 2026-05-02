package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mods.octarinecore.client.render.BlockContext;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;

    protected BlockRenderer() {
        super();
        renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this);
    }

    @Override
    public final void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public final boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }

    public abstract boolean isEligible(BlockContext context);

    protected final void setColorMultiplierBySide(Tessellator tessellator, int side) {
        switch (side) {
            case 0:
                tessellator.setColorOpaque_F(0.5f, 0.5f, 0.5f);
                break;

            case 2, 3:
                tessellator.setColorOpaque_F(0.8f, 0.8f, 0.8f);
                break;

            case 4, 5:
                tessellator.setColorOpaque_F(0.6f, 0.6f, 0.6f);
                break;

            default:
                tessellator.setColorRGBA(0xFF, 0xFF, 0xFF, 0xFF);
        }
    }
}
