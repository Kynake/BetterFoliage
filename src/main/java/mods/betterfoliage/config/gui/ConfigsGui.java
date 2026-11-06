package mods.betterfoliage.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.ConfigCategoryBuilder;
import mods.betterfoliage.config.Configs;

public class ConfigsGui extends GuiConfig {

    public ConfigsGui(GuiScreen parent) {
        super(parent, getConfigCategories(), BetterFoliageMod.MOD_ID, false, false, BetterFoliageMod.MOD_NAME);
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigCategories() {
        List<IConfigElement> configElements = new ArrayList<>();

        final Configuration configFile = Configs.getAttachedConfigFile();
        for (final String categoryName : ConfigCategoryBuilder.getOrderedCategories()) {
            final ConfigCategory category = configFile.getCategory(categoryName);

            if (categoryName.equals(Configuration.CATEGORY_GENERAL)) {
                // Flatten general settings into primary config screen.
                for (final Property generalConfig : category.getOrderedValues()) {
                    if (generalConfig.showInGui()) {
                        configElements.add(new ConfigElement(generalConfig));
                    }
                }
                continue;
            }

            configElements.add(new ConfigElement(category));
        }

        return configElements;
    }
}
