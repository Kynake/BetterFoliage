package mods.betterfoliage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.StringUtils;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.ResourceUtils;

public class BlockFilterSet {

    private final Map<Long, Boolean> blockCache;
    private final List<BlockFilterRule> rules;

    private boolean isAlteringCache = false;

    public static BlockFilterSet parseBlockFilterSet(final String[] userConfigs, final ResourceLocation location) {
        List<BlockFilterRule> rules = new ArrayList<>();

        // Get filters from player's config file (highest priority)
        List<String> blockingFilters = new ArrayList<>();
        List<String> standardFilters = new ArrayList<>();

        if (userConfigs != null) {
            for (final String config : userConfigs) {
                if (config == null) {
                    continue;
                }

                String trim = config.trim();
                if (trim.startsWith("-")) {
                    blockingFilters.add(trim);
                } else {
                    standardFilters.add(trim);
                }
            }
        }

        for (final String blockingFilter : blockingFilters) {
            BlockFilterRule rule = BlockFilterRule.parseFilter(blockingFilter);
            if (rule != null) {
                rules.add(rule);
            }
        }

        for (final String standardFilter : standardFilters) {
            BlockFilterRule rule = BlockFilterRule.parseFilter(standardFilter);
            if (rule != null) {
                rules.add(rule);
            }
        }

        // Get filters from resourcepacks (in order of priority)
        final List<String> combinedResourceFilters = ResourceUtils.assembleFiltersByResource(location);
        for (final String filter : combinedResourceFilters) {
            BlockFilterRule rule = BlockFilterRule.parseFilter(filter);
            if (rule != null) {
                rules.add(rule);
            }
        }

        return new BlockFilterSet(rules);
    }

    private BlockFilterSet(final List<BlockFilterRule> rules) {
        this.rules = rules;
        blockCache = new HashMap<>();
    }

    public boolean matches(final Block block, final IBlockAccess world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        return matches(block, metadata);
    }

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

                BetterFoliageMod.log.info(
                    "Added new block to filter cache: <{}{}> = {}",
                    Block.blockRegistry.getNameForObject(block),
                    (metadata != -1 ? ":" + metadata : ""),
                    matches);

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

    public boolean matchesNoMeta(final Block block) {
        for (BlockFilterRule rule : rules) {
            if (rule.matchesGeneral(block)) {
                return !rule.isBlockingRule;

            }
        }

        return false;
    }

    private static class BlockFilterRule {

        public final boolean isBlockingRule;

        // Filter by block
        public final Block block;

        public final int blockMetadata;

        // Filter by class
        public final Class<?> blockClass;

        public static BlockFilterRule parseFilter(String filter) {
            if (ResourceUtils.isCommentOrEmpty(filter)) {
                return null;
            }

            boolean isBlocking = false;

            if (filter.startsWith("-")) {
                isBlocking = true;
                if (filter.length() == 1) {
                    return null;
                }

                filter = filter.substring(1);
            }

            // Class filter
            if (!filter.startsWith("<")) {
                try {
                    final Class<?> biomeClass = Class.forName(filter);
                    return new BlockFilterRule(isBlocking, biomeClass);
                } catch (Throwable ignored) {
                    // Class not found, rule won't ever be valid
                    return null;
                }
            }

            // Block filter
            if (!filter.endsWith(">")) {
                return null;
            }

            filter = filter.substring(1, filter.length() - 1);
            String[] parts = StringUtils.split(filter, ":");

            // Valid formats:
            // <mod:block:metadata>
            // <mod:block> - Applies for all block metas
            // <block:metadata> - Assumes "mod" is "minecraft"
            if (parts.length < 2 || parts.length > 3 || parts[0].isEmpty() || parts[1].isEmpty()) {
                return null;
            }

            String mod;
            String blockName;
            int metadata;

            if (parts.length == 3) {
                mod = parts[0];
                blockName = parts[1];
                try {
                    metadata = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            } else {
                try {
                    metadata = Integer.parseInt(parts[1]);
                    // Is <block:metadata> format
                    mod = "minecraft";
                    blockName = parts[0];
                } catch (NumberFormatException ignored) {
                    // Is <mod:block> format
                    mod = parts[0];
                    blockName = parts[1];
                    metadata = -1;
                }
            }

            final Block block = (Block) Block.blockRegistry.getObject(mod + ":" + blockName);

            return new BlockFilterRule(isBlocking, block, metadata);
        }

        private BlockFilterRule(final boolean isBlocking, final Block block, final int blockMetadata) {
            this.isBlockingRule = isBlocking;
            this.block = block;
            this.blockMetadata = blockMetadata;
            this.blockClass = null;
        }

        private BlockFilterRule(final boolean isBlocking, final Class<?> blockClass) {
            this.isBlockingRule = isBlocking;
            this.block = null;
            this.blockMetadata = -1;
            this.blockClass = blockClass;
        }

        public boolean matchesBlock(final Block block, final int metadata) {
            if (blockClass != null) {
                return blockClass.isAssignableFrom(block.getClass());
            }

            return this.block == block && (blockMetadata == -1 || blockMetadata == metadata);
        }

        public boolean matchesGeneral(final Block block) {
            if (!isGeneralRule()) {
                return false;
            }

            if (blockClass != null) {
                return blockClass.isAssignableFrom(block.getClass());
            }

            return this.block == block;
        }

        public boolean isGeneralRule() {
            return blockMetadata == -1;
        }
    }
}
