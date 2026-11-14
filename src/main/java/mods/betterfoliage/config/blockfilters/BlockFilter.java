package mods.betterfoliage.config.blockfilters;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.Loader;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.ResourceUtils;

public abstract class BlockFilter {

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

    public static BlockFilter createClassOnlyFilter(Class<?>... filterClasses) {
        if (filterClasses.length > 1) {
            return new BlockFilterClassOnly(filterClasses);
        }
        return new BlockFilterSingleClass(filterClasses[0]);
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
                    BetterFoliageMod.log
                        .error("Unable to parse Block Filter entry. Only contains a '-' character", filter);
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
                    BetterFoliageMod.log
                        .info("Skipping block filter, class not found (probably a missing mod): {}", filter);
                    return null;
                }
            }

            // Block filter
            if (!filter.endsWith(">")) {
                BetterFoliageMod.log.error("Unable to parse Block Filter entry, must end with '>': <{}", filter);
                return null;
            }

            filter = filter.substring(1, filter.length() - 1);
            String[] parts = StringUtils.split(filter, ":");

            // Valid formats:
            // <mod:block:metadata>
            // <mod:block> - Applies for all block metas
            // <block:metadata> - Assumes "mod" is "minecraft"
            if (parts.length < 2 || parts.length > 3 || parts[0].isEmpty() || parts[1].isEmpty()) {
                BetterFoliageMod.log.error("Unable to parse Block Filter entry: <{}>", filter);
                return null;
            }

            String mod;
            String blockName;
            int metadata;

            if (parts.length == 3) {
                mod = parts[0];
                blockName = parts[1];
                try {
                    metadata = Math.max(Integer.parseInt(parts[2]), 0);
                } catch (NumberFormatException ignored) {
                    BetterFoliageMod.log.error("Unable to parse metadata from Block Filter entry: <{}>", filter);
                    return null;
                }
            } else {
                try {
                    metadata = Math.max(Integer.parseInt(parts[1]), 0);
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
                BetterFoliageMod.log.info("Skipping block filter, mod {} not found: <{}>", mod, filter);
                return null;
            }

            final Block block = (Block) Block.blockRegistry.getObject(mod + ":" + blockName);

            // Mod is present, but object not found
            if (block == Blocks.air) {
                BetterFoliageMod.log.error(
                    "Unable to generate Block Filter for mod {}, could not find the block {}: <{}>",
                    mod,
                    blockName,
                    filter);
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
            if (blockMetadata != -1 && blockMetadata != metadata) {
                return false;
            }

            return matchesBlockAnyMeta(block);
        }

        public boolean matchesBlockAnyMeta(final Block block) {
            return blockClass != null ? blockClass.isAssignableFrom(block.getClass()) : this.block == block;
        }
    }
}
