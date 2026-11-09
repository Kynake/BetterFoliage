package mods.betterfoliage.config.blockfilters;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockFilterSingleClass extends BlockFilter {

    private final BlockFilterRule classRule;
    private final Map<Block, Boolean> blockCache;

    private boolean isAlteringCache = false;

    protected BlockFilterSingleClass(Class<?> filterClass) {
        classRule = new BlockFilterRule(false, filterClass);
        blockCache = new HashMap<>();
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
        if (isAlteringCache) {
            synchronized (blockCache) {
                if (blockCache.containsKey(block)) {
                    return blockCache.get(block);
                }
            }
        } else {
            if (blockCache.containsKey(block)) {
                return blockCache.get(block);
            }
        }

        final boolean matches = classRule.matchesGeneral(block);
        synchronized (blockCache) {
            isAlteringCache = true;
            blockCache.put(block, matches);
            isAlteringCache = false;
        }

        return matches;
    }
}
