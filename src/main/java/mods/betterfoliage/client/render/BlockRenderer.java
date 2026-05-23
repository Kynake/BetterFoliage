package mods.betterfoliage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.octarinecore.client.render.BlockContext;

@SideOnly(Side.CLIENT)
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
}
