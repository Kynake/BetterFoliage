package mods.betterfoliage.config.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.Configs;

public class ConfigsGui extends GuiConfig {

    public ConfigsGui(GuiScreen parent) {
        super(parent, getConfigCategories(), BetterFoliageMod.MOD_ID, false, false, Configs.CONFIG_FILE_NAME);
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigCategories() {
        List<IConfigElement> configElements = new ArrayList<>();

        final Configuration configFile = Configs.getAttachedConfigFile();
        for (final String categoryName : configFile.getCategoryNames()) {
            ConfigCategory category = configFile.getCategory(categoryName);
            configElements.add(new ConfigElement(category));
        }

        return configElements;
    }
}
