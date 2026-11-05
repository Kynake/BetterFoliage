package mods.betterfoliage.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import mods.betterfoliage.BetterFoliageMod;

public class ConfigCategoryBuilder {

    private static final int MAX_DESC_LENGTH = 4096;

    private final String category;
    private final Configuration backingConfigFile;
    private final List<String> propertyOrder = new ArrayList<>();

    /// Access the most recent property added to this category group, for setting custom attributes to it.
    public Property recentProp = null;

    public ConfigCategoryBuilder(final String category, final Configuration backingConfigFile) {
        this.category = category;
        this.backingConfigFile = backingConfigFile;
    }

    public boolean addProp(String name, boolean defaultValue) {
        Property prop = backingConfigFile.get(category, name, defaultValue);
        setGeneralProps(prop);
        return prop.getBoolean();
    }

    public int addProp(String name, int defaultValue) {
        Property prop = backingConfigFile.get(category, name, defaultValue);
        setGeneralProps(prop);
        return prop.getInt();
    }

    public int addProp(String name, int defaultValue, int min, int max) {
        Property prop = backingConfigFile.get(category, name, defaultValue, "", min, max);
        setGeneralProps(prop);
        return prop.getInt();
    }

    public double addProp(String name, double defaultValue, double min, double max) {
        Property prop = backingConfigFile.get(category, name, defaultValue, "", min, max);
        setGeneralProps(prop);
        return prop.getDouble();
    }

    public String[] addProp(String name) {
        Property prop = backingConfigFile.get(category, name, new String[] {});
        setGeneralProps(prop);
        return prop.getStringList();
    }

    public void finalizeCategory() {
        if (propertyOrder.isEmpty()) {
            BetterFoliageMod.log.warn("Config category {} is empty! Skipping creation...", category);
            return;
        }

        final ConfigCategory fileCategory = backingConfigFile.getCategory(category);

        fileCategory.setPropertyOrder(propertyOrder);

        final String langKey = getCategoryLangKey();
        fileCategory.setLanguageKey(langKey);

        final String comment = I18n.format(langKey + ".tooltip");
        fileCategory.setComment(parseFinalizedText(comment));
    }

    public void setRecentDescription(String langKey) {
        setDescription(langKey, recentProp);
    }

    private String getCategoryLangKey() {
        return BetterFoliageMod.DOMAIN + "." + category;
    }

    private String getPropLangKey(String propName) {
        return getCategoryLangKey() + "." + propName;
    }

    private void setGeneralProps(Property prop) {
        final String name = prop.getName();
        final String langKey = getPropLangKey(name);

        setDescription(langKey, prop);

        propertyOrder.add(name);
        recentProp = prop;
    }

    private void setDescription(String langKey, Property prop) {
        prop.setLanguageKey(langKey);
        String description = I18n.format(langKey + ".tooltip");

        final String defaults = getDefaultsText(prop);
        if (defaults != null) {
            description += Configuration.NEW_LINE + defaults;
        }

        prop.comment = parseFinalizedText(description);
    }

    /// Convert problematic characters in lang text to a format more friendly to config files
    private String parseFinalizedText(String text) {
        // Convert malformed newlines
        String res = text.replace("\\n", Configuration.NEW_LINE);

        // Strip color / formatting codes
        res = res.replaceAll("§[0-9a-fklmnor]", "");

        // Remove extra '\'
        res = res.replace("\\", "");

        // Limit description size just in case
        if (res.length() > MAX_DESC_LENGTH) {
            res = res.substring(0, MAX_DESC_LENGTH);
            BetterFoliageMod.log
                .warn("A config description was too long and has been limited to {} chars: {}", MAX_DESC_LENGTH, res);
        }

        return res;
    }

    private String getDefaultsText(Property prop) {
        if (prop.isList()) {
            // In this mod all lists are empty by default
            return null;
        }

        final String defaultValue = prop.getDefault();

        boolean hasMinMax = false;
        final String min = prop.getMinValue();
        final String max = prop.getMaxValue();

        try {
            switch (prop.getType()) {
                case INTEGER -> {
                    int minValue = Integer.parseInt(min);
                    if (minValue != Integer.MIN_VALUE) {
                        hasMinMax = true;
                        break;
                    }

                    int maxValue = Integer.parseInt(max);
                    if (maxValue != Integer.MAX_VALUE) {
                        hasMinMax = true;
                    }
                }
                case DOUBLE -> {
                    double minValue = Double.parseDouble(min);
                    if (minValue != Double.MIN_VALUE) {
                        hasMinMax = true;
                        break;
                    }

                    double maxValue = Double.parseDouble(max);
                    if (maxValue != Double.MAX_VALUE) {
                        hasMinMax = true;
                    }
                }

                case STRING, BOOLEAN -> {}

                default -> {
                    return null;
                }
            }
        } catch (NumberFormatException ignored) {}

        String res = hasMinMax ? I18n.format("fml.configgui.tooltip.defaultNumeric", defaultValue, min, max)
            : I18n.format("fml.configgui.tooltip.default", defaultValue);

        return res.replace("\\", "");
    }
}
