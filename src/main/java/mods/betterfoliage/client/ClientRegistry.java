package mods.betterfoliage.client;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.GrassRenderer;
import mods.octarinecore.client.render.BlockContext;

public class ClientRegistry {

    private static BlockRenderer[] blockRenderers;

    // TODO: consider removing if no needs found
    public static void preInit() {
        BetterFoliageMod.log.info("New ClientRegistry preInit()");
    }

    // TODO: consider removing if no needs found
    public static void init() {
        BetterFoliageMod.log.info("New ClientRegistry init()");

    }

    public static void postInit() {
        BetterFoliageMod.log.info("New ClientRegistry postInit()");
        initBlockRenderers();
    }

    private static void initBlockRenderers() {
        blockRenderers = new BlockRenderer[] { GrassRenderer.getInstance() };
    }

    public static BlockRenderer getEligibleBlockRenderer(BlockContext ctx) {
        if (blockRenderers != null) {
            for (BlockRenderer blockRenderer : blockRenderers) {
                if (blockRenderer.isEligible(ctx)) {
                    return blockRenderer;
                }
            }
        }
        return null;
    }
}
