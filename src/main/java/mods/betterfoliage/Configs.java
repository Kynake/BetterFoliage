package mods.betterfoliage;

import java.io.File;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;

// TODO: Make configurable from in-game menu like the old configs
public final class Configs {

    /// Configs

    // GENERAL
    public static boolean enabled = true;
    private static final String enabledGeneralDesc = "General on/off switch for all of the mod's functionalities";

    private static final String hOffsetDesc = "The distance this element is shifted horizontally, in blocks";
    private static final String vOffsetDesc = "The distance this element is shifted vertically, in blocks";
    private static final String sizeDesc = "Size of this element";
    private static final String heightMinDesc = "Minimum height of element";
    private static final String heightMaxDesc = "Maximum height of element";



    // NETHERRACK
    public static class Netherrack {

        public static boolean enabled = true;
        private static final String enabledDesc = "Should place hanging vines under netherrack blocks";

        public static float hOffset;
        public static float heightMin;
        public static float heightMax;
        public static float size;

        public static void initializeFileConfigs(final Configuration config, final String category) {
            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.2, hOffsetDesc, 0.0, 0.4);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "heightMin", 0.6, heightMinDesc, 0.1, 1.5);
            heightMin = (float) prop.getDouble();

            prop = config.get(category, "heightMax", 0.8, heightMaxDesc, 0.1, 1.5);
            heightMax = (float) prop.getDouble();

            prop = config.get(category, "size", 1, sizeDesc, 0.5, 1.5);
            size = (float) prop.getDouble();
        }
    }

    public static class RisingSouls {
        public static boolean enabled = true;
        private static final String enabledDesc = "Should emit rising soul particles from the top of soulsand blocks";

        public static double chance;
        private static final String chanceDesc = "Chance that a random tick of a soulsand block will spawn a particle";

        public static float speed;
        private static final String speedDesc = "Ascent speed of a soul particle, in blocks";

        public static float perturb;
        private static final String perturbDesc = "Magnitude of perturbation effect. Adds a corkscrew-like motion to the particle";

        public static float headSize;
        private static final String headSizeDesc = "Size of the soul particle";

        public static float trailSize;
        private static final String trailSizeDesc = "Initial size of the particle trail";

        public static float opacity;
        private static final String opacityDesc = "Opacity of the particle effect";

        public static float sizeDecay;
        private static final String sizeDecayDesc = "Trail particle size relative to its size in the previous tick";

        public static int maxLifetimeTicks;
        public static int minLifetimeTicks;
        private static final String maxLifetimeDesc = "Maximum lifetime of a particle in seconds. Minimum lifetime is 60% of this value";

        public static int trailDensity;
        private static final String trailDensityDesc = "Every Nth tick, add a particle to the trail";

        public static void initializeFileConfigs(final Configuration config, final String category) {
            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "chance", 0.02, chanceDesc, 0.001, 1.0);
            chance = prop.getDouble();

            prop = config.get(category, "speed", 0.1, speedDesc, 0.001, 1.0);
            speed = (float) prop.getDouble();

            prop = config.get(category, "perturb", 0.05, perturbDesc, 0.01, 0.25);
            perturb = (float) prop.getDouble();

            prop = config.get(category, "headSize", 1.0, headSizeDesc, 0.25, 1.5);
            headSize = (float) prop.getDouble();

            prop = config.get(category, "trailSize", 0.75, trailSizeDesc, 0.25, 1.5);
            trailSize = (float) prop.getDouble();

            prop = config.get(category, "opacity", 0.5, opacityDesc, 0.05, 1.0);
            opacity = (float) prop.getDouble();

            prop = config.get(category, "sizeDecay", 0.97, sizeDecayDesc, 0.5, 1.0);
            sizeDecay = (float) prop.getDouble();

            prop = config.get(category, "lifetime", 4.0, maxLifetimeDesc, 1.0, 15.0);
            double maxLifetime = prop.getDouble();
            maxLifetimeTicks = (int) (Math.round(maxLifetime * 20));
            minLifetimeTicks = (int) (Math.round(maxLifetime * 0.6 * 20));

            prop = config.get(category, "trailDensity", 3, trailDensityDesc, 1, 16);
            trailDensity = prop.getInt();
        }
    }





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
    public void onStitchEvent(final TextureStitchEvent.Pre event) {
        onConfigChange();
    }

    // TODO: Temporary, remove
    @SubscribeEvent
    public void onUseHoeDebugEvent(final UseHoeEvent event) {
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

        Property prop = config.get(Configuration.CATEGORY_GENERAL, "enabled", enabled, enabledGeneralDesc);
        enabled = prop.getBoolean();

        Netherrack.initializeFileConfigs(config, "netherrack");
        RisingSouls.initializeFileConfigs(config, "risingSoul");

        if (config.hasChanged()) {
            config.save();
        }
    }

    // TODO: Implement
    /// Initializes all static configs that are read from resource packs
    public static void InitializeResourceConfigs() {

    }
}
