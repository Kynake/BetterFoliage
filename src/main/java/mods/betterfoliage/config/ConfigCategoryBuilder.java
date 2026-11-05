package mods.betterfoliage.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import mods.betterfoliage.BetterFoliageMod;

public class ConfigCategoryBuilder {

    private final String category;
    private final Configuration backingConfigFile;
    private final List<String> propertyOrder = new ArrayList<>();

    private Property recentProp = null;

    public ConfigCategoryBuilder(final String category, final Configuration backingConfigFile) {
        this.category = category;
        this.backingConfigFile = backingConfigFile;
    }

    public boolean addProp(String name, boolean defaultValue) {
        Property prop = backingConfigFile.get(category, name, defaultValue, getPropDescription(name));
        setGeneralProps(prop);
        return prop.getBoolean();
    }

    public int addProp(String name, int defaultValue) {
        Property prop = backingConfigFile.get(category, name, defaultValue, getPropDescription(name));
        setGeneralProps(prop);
        return prop.getInt();
    }

    public int addProp(String name, int defaultValue, int min, int max) {
        Property prop = backingConfigFile.get(category, name, defaultValue, getPropDescription(name), min, max);
        setGeneralProps(prop);
        return prop.getInt();
    }

    public double addProp(String name, double defaultValue, double min, double max) {
        Property prop = backingConfigFile.get(category, name, defaultValue, getPropDescription(name), min, max);
        setGeneralProps(prop);
        return prop.getDouble();
    }

    public String[] addProp(String name) {
        Property prop = backingConfigFile.get(category, name, new String[] {}, getPropDescription(name));
        setGeneralProps(prop);
        return prop.getStringList();
    }

    /// Get the most recent property added to this category group, for setting custom attributes to it.
    public Property getRecentProperty() {
        return recentProp;
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

    private String getCategoryLangKey() {
        return BetterFoliageMod.DOMAIN + "." + category;
    }

    private String getPropLangKey(String propName) {
        return getCategoryLangKey() + "." + propName;
    }

    private String getPropDescription(String propName) {
        final String langKey = getPropLangKey(propName);
        final String langValue = I18n.format(langKey + ".tooltip");
        return parseFinalizedText(langValue);
    }

    private void setGeneralProps(Property prop) {
        final String name = prop.getName();
        prop.setLanguageKey(getPropLangKey(name));
        propertyOrder.add(name);
        recentProp = prop;
    }

    /// Convert problematic characters in lang text to a format more friendly to config files
    private String parseFinalizedText(String text) {
        // Convert malformed newlines
        String res = text.replace("\\n", Configuration.NEW_LINE);

        // Strip color / formatting codes
        res = res.replaceAll("§[0-9a-fklmnor]", "");

        return res;
    }
}
