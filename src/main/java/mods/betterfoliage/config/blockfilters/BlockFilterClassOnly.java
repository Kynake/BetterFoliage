package mods.betterfoliage.config.blockfilters;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockFilterClassOnly extends BlockFilter {

    private final BlockFilterRule[] classRules;
    private final Map<Block, Boolean> blockCache;

    private boolean isAlteringCache = false;

    protected BlockFilterClassOnly(Class<?>... filterClasses) {
        classRules = new BlockFilterRule[filterClasses.length];
        for (int i = 0; i < filterClasses.length; i++) {
            classRules[i] = new BlockFilterRule(false, filterClasses[i]);
        }

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

        for (BlockFilterRule classRule : classRules) {
            final boolean matches = classRule.matchesGeneral(block);
            if (matches) {
                synchronized (blockCache) {
                    isAlteringCache = true;
                    blockCache.put(block, true);
                    isAlteringCache = false;
                }

                return true;
            }
        }

        synchronized (blockCache) {
            isAlteringCache = true;
            blockCache.put(block, false);
            isAlteringCache = false;
        }

        return false;
    }
}
