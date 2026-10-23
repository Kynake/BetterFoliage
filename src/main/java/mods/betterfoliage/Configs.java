package mods.betterfoliage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

// TODO: Allow hot-reload
// TODO: Make configurable from in-game menu like the old configs
public class Configs {

    public static final String CONFIG_FILE_NAME = BetterFoliageMod.MOD_ID + "_LegacyEdition";

    public static boolean enabled = true;

    /// Initializes all static configs that should be read from a config file.
    public static void initializeFileConfigs(final Configuration config) {
        config.load();

        Property propEnabled = config.get(Configuration.CATEGORY_GENERAL, "enabled", true,
            "General on/off switch for all of the mod's functionalities");
        enabled = propEnabled.getBoolean();

        if (config.hasChanged()) {
            config.save();
        }
    }

    // TODO: Implement
    /// Initializes all static configs that are read from resource packs
    public static void InitializeResourceConfigs() {

    }

    public static File getConfigFile(final File configDir) {
        return new File(configDir, CONFIG_FILE_NAME + ".cfg");
    }
}
