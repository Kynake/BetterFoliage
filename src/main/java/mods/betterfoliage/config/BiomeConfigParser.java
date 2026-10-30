package mods.betterfoliage.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import mods.betterfoliage.BetterFoliageMod;

public class BiomeConfigParser {

    public static int[] parseValidBiomeIDs(final String[] userConfigs, final String resourceConfigsPath) {
        List<BiomeFilterRule> rules = new ArrayList<>();

        // Get filter from config file
        for (final String config : userConfigs) {
            BiomeFilterRule rule = BiomeFilterRule.parseFilter(config);
            if (rule != null) {
                rules.add(rule);
            }
        }

        rules.sort(null);

        // TODO: open all resource files with path and add them to rules list in order

        List<Integer> validBiomes = new ArrayList<>();

        final BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        for (final BiomeGenBase biome : biomes) {
            if (biome == null) {
                continue;
            }

            for (final BiomeFilterRule rule : rules) {
                if (rule.matchesBiome(biome)) {
                    if (!rule.isBlockingRule) {
                        validBiomes.add(biome.biomeID);
                    }
                    break;
                }
            }
        }

        int[] res = new int[validBiomes.size()];
        for (int i = 0; i < validBiomes.size(); i++) {
            res[i] = validBiomes.get(i);
        }

        return res;
    }

    private static class BiomeFilterRule implements Comparable<BiomeFilterRule> {

        public final boolean isBlockingRule;

        public final int biomeID;

        public final Class<?> biomeClass;

        public final String biomeName;

        public final boolean useRain;
        public final boolean canRain;

        public final boolean useSnow;
        public final boolean canSnow;

        public final boolean useMinTemperature;
        public final float minTemperature;

        public final boolean useMaxTemperature;
        public final float maxTemperature;

        public final boolean useMinHumidity;
        public final float minHumidity;

        public final boolean useMaxHumidity;
        public final float maxHumidity;

        public static BiomeFilterRule parseFilter(String filter) {
            if (filter == null) {
                return null;
            }

            filter = filter.trim();

            if (filter.isEmpty() || StringUtils.startsWithAny(filter, "#", "//", "!")) {
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

            try {
                int biomeID = Integer.parseInt(filter);
                if (biomeID >= 0) {
                    return new BiomeFilterRule(isBlocking, biomeID);
                }
            } catch (NumberFormatException ignored) {}

            String[] parts = StringUtils.split(filter, ":");

            if (parts.length < 1 || parts[0].isEmpty()) {
                return null;
            }

            final Class<?> biomeClass;
            try {
                biomeClass = Class.forName(parts[0]);
            } catch (Throwable ignored) {
                // Class not found, rule won't ever be valid
                return null;
            }

            String biomeName = null;

            boolean useRain = false;
            boolean useSnow = false;

            boolean canRain = false;
            boolean canSnow = false;

            boolean useMinTemp = false;
            boolean useMaxTemp = false;

            float minTemp = 0;
            float maxTemp = 0;

            boolean useMinRain = false;
            boolean useMaxRain = false;

            float minRain = 0;
            float maxRain = 0;

            for (int i = 1; i < parts.length; i++) {
                if (parts[i].isEmpty()) {
                    BetterFoliageMod.log.error("Empty sub-filter found while parsing biome filter: {} ", filter);
                    return null; // No empty subfilters allowed
                }

                String subFilter = parts[i].substring(1);

                switch (parts[i].charAt(0)) {
                    case 'n': // biomeName
                        biomeName = subFilter;
                        continue;

                    case 'r': // canRain
                        if (subFilter.equalsIgnoreCase("true")) {
                            useRain = true;
                            canRain = true;
                            continue;
                        } else if (subFilter.equalsIgnoreCase("false")) {
                            useRain = true;
                            canRain = false;
                            continue;
                        }
                        BetterFoliageMod.log.error(
                            "Invalid boolean when parsing biome canRain, values must be 'true' or 'false': {} ({})",
                            subFilter,
                            biomeClass.getCanonicalName());
                        return null;

                    case 's': // canSnow
                        if (subFilter.equalsIgnoreCase("true")) {
                            useSnow = true;
                            canSnow = true;
                            continue;
                        } else if (subFilter.equalsIgnoreCase("false")) {
                            useSnow = true;
                            canSnow = false;
                            continue;
                        }
                        BetterFoliageMod.log.error(
                            "Invalid boolean when parsing biome canSnow, values must be 'true' or 'false': {} ({})",
                            subFilter,
                            biomeClass.getCanonicalName());
                        return null;

                    case 't': // minTemperature
                        try {
                            minTemp = Float.parseFloat(subFilter);
                            useMinTemp = true;
                        } catch (NumberFormatException e) {
                            BetterFoliageMod.log.error(
                                "Invalid number when parsing biome minTemperature: {} ({})",
                                subFilter,
                                biomeClass.getCanonicalName());
                            return null;
                        }
                        continue;

                    case 'T': // maxTemperature
                        try {
                            maxTemp = Float.parseFloat(subFilter);
                            useMaxTemp = true;
                        } catch (NumberFormatException e) {
                            BetterFoliageMod.log.error(
                                "Invalid number when parsing biome maxTemperature: {} ({})",
                                subFilter,
                                biomeClass.getCanonicalName());
                            return null;
                        }
                        continue;

                    case 'h': // minHumidity
                        try {
                            minRain = Float.parseFloat(subFilter);
                            useMinRain = true;
                        } catch (NumberFormatException e) {
                            BetterFoliageMod.log.error(
                                "Invalid number when parsing biome minHumidity: {} ({})",
                                subFilter,
                                biomeClass.getCanonicalName());
                            return null;
                        }
                        continue;

                    case 'H': // maxHumidity
                        try {
                            maxRain = Float.parseFloat(subFilter);
                            useMaxRain = true;
                        } catch (NumberFormatException e) {
                            BetterFoliageMod.log.error(
                                "Invalid number when parsing biome maxHumidity: {} ({})",
                                subFilter,
                                biomeClass.getCanonicalName());
                            return null;
                        }
                        continue;

                    default:
                        BetterFoliageMod.log.error(
                            "Unknown biome subfilter encountered. filter: {}, content: {}",
                            parts[i].charAt(0),
                            subFilter);
                        return null;
                }
            }

            return new BiomeFilterRule(
                isBlocking,
                biomeClass,
                biomeName,
                useRain,
                canRain,
                useSnow,
                canSnow,
                useMinTemp,
                useMaxTemp,
                minTemp,
                maxTemp,
                useMinRain,
                useMaxRain,
                minRain,
                maxRain);
        }

        private BiomeFilterRule(boolean isBlocking, Class<?> biomeClass, String biomeName, boolean useRain,
            boolean canRain, boolean useSnow, boolean canSnow, boolean useMinTemp, boolean useMaxTemp, float minTemp,
            float maxTemp, boolean useMinRain, boolean useMaxRain, float minRain, float maxRain) {
            this.isBlockingRule = isBlocking;
            this.biomeID = -1;

            this.biomeClass = biomeClass;
            this.biomeName = biomeName;

            this.useRain = useRain;
            this.canRain = canRain;

            this.useSnow = useSnow;
            this.canSnow = canSnow;

            this.useMinTemperature = useMinTemp;
            this.useMaxTemperature = useMaxTemp;

            this.minTemperature = minTemp;
            this.maxTemperature = maxTemp;

            this.useMinHumidity = useMinRain;
            this.useMaxHumidity = useMaxRain;

            this.minHumidity = minRain;
            this.maxHumidity = maxRain;
        }

        private BiomeFilterRule(boolean isBlocking, int biomeID) {
            this.isBlockingRule = isBlocking;
            this.biomeID = biomeID;

            this.biomeClass = null;
            this.biomeName = null;

            this.useRain = false;
            this.canRain = false;

            this.useSnow = false;
            this.canSnow = false;

            this.useMinTemperature = false;
            this.useMaxTemperature = false;

            this.minTemperature = 0;
            this.maxTemperature = 0;

            this.useMinHumidity = false;
            this.useMaxHumidity = false;

            this.minHumidity = 0;
            this.maxHumidity = 0;
        }

        public boolean matchesBiome(final BiomeGenBase biome) {
            // ID
            if (biomeID >= 0) {
                return biomeID == biome.biomeID;
            }

            // All other options require a valid class
            if (biomeClass == null) {
                return false;
            }

            if (!biomeClass.isAssignableFrom(biome.getClass())) {
                return false;
            }

            // Name
            if (biomeName != null && !biome.biomeName.equals(biomeName)) {
                return false;
            }

            // Snow
            final boolean enableSnow = biome.getEnableSnow();
            if (useSnow && (canSnow != enableSnow)) {
                return false;
            }

            // Rain

            // Roundabout way of accessing biome.enableRain (which is protected)
            final boolean enableRain = !enableSnow && biome.canSpawnLightningBolt();

            if (useRain && (canRain != enableRain)) {
                return false;
            }

            // Temperature
            if (useMinTemperature && biome.temperature < minTemperature) {
                return false;
            }

            if (useMaxTemperature && biome.temperature > maxTemperature) {
                return false;
            }

            // Rainfall
            if (useMinHumidity && biome.rainfall < minHumidity) {
                return false;
            }

            if (useMaxHumidity && biome.rainfall > maxHumidity) {
                return false;
            }

            return true;
        }

        @Override
        /// For sorting, blocking rules have priority over non-blocking ones.
        public int compareTo(@NotNull BiomeFilterRule other) {
            if (isBlockingRule == other.isBlockingRule) {
                return 0;
            }

            return isBlockingRule ? -1 : 1;
        }
    }
}
