package mods.betterfoliage.config.blockfilters;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockFilterSingleClass extends BlockFilter {

    private final Class<?> filterClass;

    protected BlockFilterSingleClass(Class<?> filterClass) {
        this.filterClass = filterClass;
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
        return filterClass.isInstance(block);
    }
}
