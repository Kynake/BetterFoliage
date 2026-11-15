package mods.betterfoliage.config.blockfilters;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockFilterClassOnly extends BlockFilter {

    private final Class<?>[] filterClasses;

    protected BlockFilterClassOnly(Class<?>... filterClasses) {
        this.filterClasses = filterClasses;
    }

    @Override
    public boolean matches(final IBlockAccess world, final int x, final int y, final int z) {
        return matchesNoMeta(world.getBlock(x, y, z));
    }

    @Override
    public boolean matches(final Block block, final IBlockAccess world, final int x, final int y, final int z) {
        return matchesNoMeta(block);
    }

    @Override
    public boolean matches(Block block, int metadata) {
        return matchesNoMeta(block);
    }

    @Override
    public boolean matchesNoMeta(Block block) {
        for (Class<?> filterClass : filterClasses) {
            if (filterClass.isInstance(block)) return true;
        }

        return false;
    }
}
