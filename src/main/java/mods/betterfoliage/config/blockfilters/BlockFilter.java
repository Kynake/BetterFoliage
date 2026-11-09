package mods.betterfoliage.config.blockfilters;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.Loader;
import mods.betterfoliage.client.resource.ResourceUtils;

public abstract class BlockFilter {

    public static BlockFilter parseBlockFilterSet(final String[] userConfigs, final ResourceLocation location) {
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

    public static BlockFilter createSingleClassFilter(Class<?> filterClass) {
        return new BlockFilterSingleClass(filterClass);
    }

    public boolean matches(final IBlockAccess world, final int x, final int y, final int z) {
        return matches(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }

    public boolean matches(final Block block, final IBlockAccess world, final int x, final int y, final int z) {
        return matches(block, world.getBlockMetadata(x, y, z));
    }

    public abstract boolean matches(final Block block, final int metadata);

    public abstract boolean matchesNoMeta(final Block block);

    protected static class BlockFilterRule {

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

            // Mod not present
            if (!(mod.equals("minecraft") || Loader.isModLoaded(mod))) {
                return null;
            }

            final Block block = (Block) Block.blockRegistry.getObject(mod + ":" + blockName);

            // Mod is present, but object not found
            if (block == Blocks.air) {
                return null;
            }

            return new BlockFilterRule(isBlocking, block, metadata);
        }

        public BlockFilterRule(final boolean isBlocking, final Block block, final int blockMetadata) {
            this.isBlockingRule = isBlocking;
            this.block = block;
            this.blockMetadata = blockMetadata;
            this.blockClass = null;
        }

        public BlockFilterRule(final boolean isBlocking, final Class<?> blockClass) {
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
