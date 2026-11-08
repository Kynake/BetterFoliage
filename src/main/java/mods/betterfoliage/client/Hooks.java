package mods.betterfoliage.client;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.features.LogRenderer;
import mods.betterfoliage.client.render.particles.LeafParticleRenderer;
import mods.betterfoliage.client.render.particles.SoulParticleRenderer;
import mods.betterfoliage.config.Configs;

@SideOnly(Side.CLIENT)
public class Hooks {

    // What: Invoke BF code to overrule the return value of Block.getRenderType()
    // Why: This allows us to use custom block renderers for any block,
    // without touching block code
    public static int getRenderTypeOverride(IBlockAccess world, int x, int y, int z, int original) {
        if (!Configs.enabled) return original;

        BlockRenderer renderer = ClientRegistry.getEligibleBlockRenderer(world, x, y, z);

        return renderer != null ? renderer.getRenderId() : original;
    }

    public static boolean overrideIsPartialBlock(IBlockAccess world, int x, int y, int z, boolean isPartial) {
        return !overrideIsOpaqueCube(world, x, y, z, !isPartial);
    }

    // What: Wrap external calls to isOpaqueCube to signal that a block is not an opaque cube
    // Why: Allows us to make other mods think that log blocks are non-solid without messing with the
    // isOpaqueBlock() method itself, which could affect gameplay (e.g. Can redstone be placed on top).
    // Used by rounded logs to prevent the adjacent blocks' side from being invisible,
    // and to fix lighting glitches when using Angelica.
    public static boolean overrideIsOpaqueCube(IBlockAccess world, int x, int y, int z, boolean isOpaque) {
        if (!isOpaque || LogRenderer.isRoundLog(world, x, y, z)) {
            return false;
        }

        // Leaves are never opaque when not rendering the base block
        return (!Configs.ExtraLeaves.enabled | Configs.ExtraLeaves.renderBaseBlock)
            || !Configs.Blocks.extraLeaves.matches(world, x, y, z);
    }

    // What: Invoke BF code for every random display tick
    // Why: Allows us to inject our own particles, without touching block code
    public static void onRandomDisplayTick(Block block, World world, int x, int y, int z) {
        if (!Configs.enabled) return;

        if (Configs.RisingSouls.enabled && Math.random() < Configs.RisingSouls.chance
            && world.isAirBlock(x, y + 1, z)
            && Configs.Blocks.soulsand.matches(block, world, x, y, z)) {
            SoulParticleRenderer.spawnSoulParticle(world, x, y, z);
        }

        if (Configs.FallingLeaves.enabled && Math.random() < Configs.FallingLeaves.chance
            && world.isAirBlock(x, y - 1, z)
            && Configs.Blocks.fallingLeaves.matches(block, world, x, y, z)) {
            LeafParticleRenderer.spawnLeafParticle(world, x, y, z);
        }
    }
}
