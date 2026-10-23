package mods.betterfoliage;

import java.io.File;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

// TODO: Make configurable from in-game menu like the old configs
public final class Configs {

    /// Configs

    public static boolean enabled = true;

    ///

    private static final String CONFIG_FILE_NAME = BetterFoliageMod.MOD_ID + "_LegacyEdition";

    private Configuration attachedConfig = null;

    private Configs() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(BetterFoliageMod.MOD_ID)) {
            onConfigChange();
        }
    }

    @SubscribeEvent
    public void onStitchEvent(TextureStitchEvent.Pre event) {
        onConfigChange();
    }

    private void onConfigChange() {
        if (attachedConfig != null) {
            initializeFileConfigs(attachedConfig);
        }
    }

    public static void init(final File configDir) {
        final File configFile = new File(configDir, CONFIG_FILE_NAME + ".cfg");
        Configuration configs = new Configuration(configFile, null, true);

        Configs instance = new Configs();
        instance.attachedConfig = configs;

        initializeFileConfigs(configs);
    }

    /// Initializes all static configs that should be read from a config file.
    public static void initializeFileConfigs(final Configuration config) {
        config.load();

        Property propEnabled = config.get(
            Configuration.CATEGORY_GENERAL,
            "enabled",
            true,
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
}
