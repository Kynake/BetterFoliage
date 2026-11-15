package mods.betterfoliage.config.blockfilters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;

public class BlockFilterSet extends BlockFilter {

    private final Map<Long, Boolean> blockCache;
    private final List<BlockFilterRule> rules;

    public final boolean requiresResourceReload;
    private boolean isAlteringCache = false;

    protected BlockFilterSet(final List<BlockFilterRule> rules, BlockFilterSet previous) {
        this.rules = rules;
        blockCache = new HashMap<>();
        requiresResourceReload = isDifferentRuleOrderFromPrevious(previous);
    }

    @Override
    public boolean matches(final Block block, final int metadata) {
        final int blockID = Block.blockRegistry.getIDForObject(block);
        final Long metaKey = (((long) blockID) << Integer.SIZE) | (metadata & 0xFFFFFFFFL);

        if (isAlteringCache) {
            synchronized (blockCache) {
                if (blockCache.containsKey(metaKey)) {
                    return blockCache.get(metaKey);
                }
            }
        } else {
            if (blockCache.containsKey(metaKey)) {
                return blockCache.get(metaKey);
            }
        }

        for (BlockFilterRule rule : rules) {
            if (rule.matchesBlock(block, metadata)) {
                final boolean matches = !rule.isBlockingRule;

                synchronized (blockCache) {
                    isAlteringCache = true;
                    blockCache.put(metaKey, matches);
                    isAlteringCache = false;
                }

                return matches;
            }
        }

        synchronized (blockCache) {
            isAlteringCache = true;
            blockCache.put(metaKey, false);
            isAlteringCache = false;
        }

        return false;
    }

    @Override
    public boolean matchesNoMeta(final Block block) {
        for (BlockFilterRule rule : rules) {
            if (rule.matchesBlockAnyMeta(block)) {
                return !rule.isBlockingRule;
            }
        }

        return false;
    }

    private boolean isDifferentRuleOrderFromPrevious(BlockFilterSet prev) {
        if (prev == null) return false;
        if (prev.rules == null || rules.size() > prev.rules.size()) return true;

        int offset = 0;
        for (int i = 0; i < rules.size(); i++) {

            BlockFilterRule rule = rules.get(i);

            int prevIndex = i + offset;
            while (prevIndex < prev.rules.size()) {
                BlockFilterRule prevRule = prev.rules.get(prevIndex);
                if (rule.equalsRule(prevRule)) {
                    break;
                }

                offset++;
                prevIndex++;
            }

            if (prevIndex >= prev.rules.size()) {
                // Did not match any previous rule
                return true;
            }
        }

        return false;
    }
}
