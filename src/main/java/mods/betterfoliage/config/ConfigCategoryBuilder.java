package mods.betterfoliage.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

public class ConfigCategoryBuilder {

    public final String category;
    private final Configuration backingConfig;
    private final List<String> propertyOrder = new ArrayList<>();

    public ConfigCategoryBuilder(final String category, final Configuration backingConfig) {
        this.category = category;
        this.backingConfig = backingConfig;
    }

    public boolean addConfig(String name, boolean defaultValue, String langKey) {
        return false;
    }

    public void finalizeCategory() {

    }
}
