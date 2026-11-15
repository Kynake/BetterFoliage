package mods.betterfoliage.client.integration;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.registries.GrassRegistry;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.config.blockfilters.BlockFilter;
import mods.betterfoliage.mixins.interfaces.gt6.ITreeHoleMTE;

public class GT6Integration {

    public static final BlockFilter treeHole;
    private static final String[] leafTypes = { "BLUEMAHOE", "BLUESPRUCE", "BLUESPRUCE_XMAS", "CINNAMON", "COCONUT",
        "HAZEL", "MAPLE", "MAPLE_BROWN", "MAPLE_ORANGE", "MAPLE_RED", "MAPLE_YELLOW", "RAINBOWOOD", "RUBBER",
        "WILLOW" };

    private static final String[] grassTypes = { "BROWN", "DARK", "LIGHT", "MEDIUM", "NORMAL", "YELLOW" };

    static {
        BetterFoliageMod.log.info("Initializing mod integration: {}", Mod.GT6.modName);
        if (Mod.GT6.isLoaded()) {
            treeHole = new BlockFilterTreeHole();
        } else {
            BetterFoliageMod.log.error("Tried to access GT6Integration class without {} present!", Mod.GT6.modName);
            treeHole = null;
        }
    }

    public static void registerGT6Leaves(TextureMap atlas, LeafRegistry registry) {
        for (final String leafType : leafTypes) {
            String[] renderTypes = { "", "OPAQUE_" };
            for (final String renderType : renderTypes) {
                String location = Mod.GT6.modID + ":iconsets/LEAVES_" + renderType + leafType;
                IIcon original = atlas.getTextureExtry(location);
                if (original != null) {
                    BetterFoliageMod.log.info("Registering {} leaf texture: {}", Mod.GT6.modName, location);
                    registry.registerLeaf(atlas, original);
                }
            }
        }
    }

    public static void registerGT6Grass(TextureMap atlas, GrassRegistry registry) {
        for (final String type : grassTypes) {
            String location = Mod.GT6.modID + ":iconsets/GRASSBLOCK_TOP_" + type;
            IIcon original = atlas.getTextureExtry(location);
            if (original != null) {
                BetterFoliageMod.log.info("Registering {} grass texture: {}", Mod.GT6.modName, location);
                registry.registerGrass(original);
            }
        }
    }

    public static IIcon getTreeHoleSpriteForSide(IBlockAccess world, int x, int y, int z, int side) {
        ITreeHoleMTE treeHole = (ITreeHoleMTE) world.getTileEntity(x, y, z);
        return treeHole.betterFoliage$getSpriteForSide(side);
    }

    public static IIcon getTreeHoleDefaultSprite(IBlockAccess world, int x, int y, int z) {
        ITreeHoleMTE treeHole = (ITreeHoleMTE) world.getTileEntity(x, y, z);
        return treeHole.betterFoliage$getDefaultSprite();
    }

    private static class BlockFilterTreeHole extends BlockFilter {

        @Override
        public boolean matches(final IBlockAccess world, final int x, final int y, final int z) {
            return matches(world.getBlock(x, y, z), world, x, y, z);
        }

        @Override
        public boolean matches(final Block block, final IBlockAccess world, final int x, final int y, final int z) {
            return block instanceof MultiTileEntityBlock && world.getTileEntity(x, y, z) instanceof ITreeHoleMTE;
        }

        @Override
        public boolean matches(Block block, int metadata) {
            return logUseError();
        }

        @Override
        public boolean matchesNoMeta(Block block) {
            return logUseError();
        }

        private static boolean logUseError() {
            BetterFoliageMod.log.error(
                "Don't use this method, use matches(Block, IBlockAccess, x, y, z) instead. BlockFilterTreeHole needs a world reference to get the block's TileEntity");
            return false;
        }
    }
}
