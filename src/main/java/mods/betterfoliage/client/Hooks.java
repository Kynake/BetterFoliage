package mods.betterfoliage.client;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.EntityRisingSoulFX;
import mods.betterfoliage.client.render.particles.LeafParticleRenderer;
import mods.octarinecore.client.render.AbstractBlockRenderingHandler;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.RendererHolder;

@SideOnly(Side.CLIENT)
public class Hooks {

    // What: Invoke BF code to overrule the return value of Block.getRenderType()
    // Why: This allows us to use custom block renderers for any block,
    // without touching block code
    public static int getRenderTypeOverride(IBlockAccess blockAccess, int x, int y, int z, int original) {
        if (!Config.INSTANCE.getEnabled()) return original;

        BlockContext ctx = RendererHolder.getBlockContext();
        ctx.set(blockAccess, x, y, z);

        // Look for new renderers first
        BlockRenderer renderer = ClientRegistry.getEligibleBlockRenderer(ctx);
        if (renderer != null) return renderer.getRenderId();

        // TODO remove eventually
        // Only then use legacy renderers
        for (AbstractBlockRenderingHandler legacyRenderer : Client.INSTANCE.getRenderers()) {
            if (legacyRenderer.isEligible(ctx)) return legacyRenderer.getRenderId();
        }

        return original;
    }

    // What: Invoke BF code to make block non-solid when the game otherwise considers it to be.
    // Why: Allows us to make log blocks non-solid without messing with isOpaqueBlock(),
    // which would result in side effects for gameplay (e.g. Can redstone be placed on top).
    // Used by rounded logs to prevent the adjacent blocks' side from being invisible
    public static boolean overrideIsPartialBlock(IBlockAccess blockAccess, int x, int y, int z, boolean isPartial) {
        return isPartial || (Config.INSTANCE.getEnabled() && Config.roundLogs.INSTANCE.getEnabled()
            && Config.blocks.INSTANCE.getLogs()
                .matchesID(blockAccess.getBlock(x, y, z)));
    }

    // What: Invoke BF code to overrule AO transparency value
    // Why: Allows us to have light behave properly on non-solid log blocks without
    // messing with isOpaqueBlock(), which could have gameplay effects
    public static float getAmbientOcclusionLightValueOverride(Block block, float original) {
        return Config.INSTANCE.getEnabled() && Config.roundLogs.INSTANCE.getEnabled()
            && Config.blocks.INSTANCE.getLogs()
                .matchesID(block) ? Config.roundLogs.INSTANCE.getDimming() : original;
    }

    // What: Invoke BF code to override block.useNeighborBrightness
    // Why: Allows us to have light behave properly on non-solid log blocks
    public static boolean getUseNeighborBrightnessOverride(Block block, boolean original) {
        return original || (Config.INSTANCE.getEnabled() && Config.roundLogs.INSTANCE.getEnabled()
            && Config.blocks.INSTANCE.getLogs()
                .matchesID(block));
    }

    // What: Invoke BF code for every random display tick
    // Why: Allows us to inject our own particles, without touching block code
    public static void onRandomDisplayTick(Block block, World world, int x, int y, int z) {
        if (!Config.INSTANCE.getEnabled()) return;

        if (Config.risingSoul.INSTANCE.getEnabled() && block == Blocks.soul_sand
            && Math.random() < Config.risingSoul.INSTANCE.getChance()
            && world.isAirBlock(x, y + 1, z)) {
            EntityRisingSoulFX soul = new EntityRisingSoulFX(world, x, y, z);
            soul.addIfValid();
        }

        if (Config.fallingLeaves.INSTANCE.getEnabled() && Math.random() < Config.fallingLeaves.INSTANCE.getChance()
            && world.isAirBlock(x, y - 1, z)
            && Config.blocks.INSTANCE.getFallingLeaves()
                .matchesID(block)) {
            LeafParticleRenderer.spawnLeafParticle(world, x, y, z);
        }
    }
}
