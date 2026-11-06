package mods.betterfoliage.config;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.betterfoliage.BetterFoliageMod;

public final class Configs {

    public static boolean enabled;

    // Mod uses a set number as random seed because the world seed can't be accessed Client-side in a server.
    public static int seed;

    private static final String hOffsetDesc = "The distance this element is shifted horizontally, in blocks";
    private static final String vOffsetDesc = "The distance this element is shifted vertically, in blocks";
    private static final String sizeDesc = "Size of this element";
    private static final String thresholdDesc = "Chance that an eligible block will have this feature. Used as a cutoff value in a 3D noise generator";
    private static final String biomesDesc = "A list of biomes filters that determine if this feature is allowed to appear in this biome or not."
        + "\nCheck the wiki for more details on how these filters work";

    public static class Blocks {

        public static BlockFilterSet dirt;
        public static BlockFilterSet grass;
        public static BlockFilterSet obstructShortGrass;
        public static BlockFilterSet extraLeaves;
        public static BlockFilterSet fallingLeaves;
        public static BlockFilterSet logs;
        public static BlockFilterSet sand;
        public static BlockFilterSet lilypad;
        public static BlockFilterSet cactus;
        public static BlockFilterSet soulsand;

        public static void init(final Configuration config) {
            // Build category
            final ConfigCategoryBuilder blocks = new ConfigCategoryBuilder("blocks", config);
            final String[] dirtFilters = blocks.addProp("dirt");
            final String[] grassFilters = blocks.addProp("grass");
            final String[] obstructShortGrassFilters = blocks.addProp("obstructShortGrass");
            final String[] extraLeavesFilters = blocks.addProp("extraLeaves");
            final String[] fallingLeavesFilters = blocks.addProp("fallingLeaves");
            final String[] logsFilters = blocks.addProp("logs");
            final String[] sandFilters = blocks.addProp("sand");
            final String[] lilypadFilters = blocks.addProp("lilypad");
            final String[] cactusFilters = blocks.addProp("cactus");
            final String[] soulsandFilters = blocks.addProp("soulsand");
            blocks.finalizeCategory();

            // Parse each String[] into a BlockFilterSet
            dirt = BlockFilterSet
                .parseBlockFilterSet(dirtFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksDirt.cfg"));

            grass = BlockFilterSet
                .parseBlockFilterSet(grassFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksGrass.cfg"));

            obstructShortGrass = BlockFilterSet.parseBlockFilterSet(
                obstructShortGrassFilters,
                new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksObstructShortGrass.cfg"));

            extraLeaves = BlockFilterSet.parseBlockFilterSet(
                extraLeavesFilters,
                new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksExtraLeaves.cfg"));

            fallingLeaves = BlockFilterSet.parseBlockFilterSet(
                fallingLeavesFilters,
                new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksFallingLeaves.cfg"));

            logs = BlockFilterSet
                .parseBlockFilterSet(logsFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksLogs.cfg"));

            sand = BlockFilterSet
                .parseBlockFilterSet(sandFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksSand.cfg"));

            lilypad = BlockFilterSet.parseBlockFilterSet(
                lilypadFilters,
                new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksLilypad.cfg"));

            cactus = BlockFilterSet
                .parseBlockFilterSet(cactusFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksCactus.cfg"));

            soulsand = BlockFilterSet.parseBlockFilterSet(
                soulsandFilters,
                new ResourceLocation(BetterFoliageMod.DOMAIN, "blocksSoulSand.cfg"));
        }
    }

    public static class ConnectedGrass {

        public static boolean enabled;
        public static boolean snowEnabled;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder connectedGrass = new ConfigCategoryBuilder("connectedGrass", config);

            enabled = connectedGrass.addProp("enabled", true);
            connectedGrass.setRecentDescription("betterfoliage.enabled");

            snowEnabled = connectedGrass.addProp("snowEnabled", true);

            connectedGrass.finalizeCategory();
        }
    }

    public static class ShortGrass {

        public static boolean grassEnabled;
        public static boolean myceliumEnabled;
        public static boolean snowEnabled;
        public static float hOffset;
        public static float heightMin;
        public static float heightMax;
        public static float size;
        public static boolean mixTextures;
        public static boolean useGenerated;

        // TODO: Add shader wind support
        // public static boolean shaderWind = true;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder shortGrass = new ConfigCategoryBuilder("shortGrass", config);
            grassEnabled = shortGrass.addProp("grassEnabled", true);
            myceliumEnabled = shortGrass.addProp("myceliumEnabled", true);
            snowEnabled = shortGrass.addProp("snowEnabled", true);

            hOffset = (float) shortGrass.addProp("hOffset", 0.2, 0.0, 0.4);
            shortGrass.setRecentDescription("betterfoliage.hOffset");

            heightMin = (float) shortGrass.addProp("heightMin", 0.6, 0.1, 2.5);
            shortGrass.setRecentDescription("betterfoliage.heightMin");

            heightMax = (float) shortGrass.addProp("heightMax", 0.8, 0.1, 2.5);
            shortGrass.setRecentDescription("betterfoliage.heightMax");

            size = (float) shortGrass.addProp("size", 1.0, 0.5, 1.5);
            shortGrass.setRecentDescription("betterfoliage.size");

            mixTextures = shortGrass.addProp("mixTextures", true);
            useGenerated = shortGrass.addProp("useGenerated", false);

            shortGrass.finalizeCategory();
        }
    }

    public static class RoundLogs {

        public static boolean enabled;
        public static double radiusSmall;
        public static double radiusLarge;
        public static boolean connectToSolids;
        public static boolean connectLenient;
        public static boolean connectPerpendicular;
        public static boolean connectGrass;
        public static double zProtection;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder roundLogs = new ConfigCategoryBuilder("roundLogs", config);

            enabled = roundLogs.addProp("enabled", false);
            roundLogs.setRecentDescription("betterfoliage.enabled");

            radiusSmall = roundLogs.addProp("radiusSmall", 0.25, 0.0, 0.5);
            radiusLarge = roundLogs.addProp("radiusLarge", 0.5, 0.0, 0.5);
            connectToSolids = roundLogs.addProp("connectToSolids", false);
            connectLenient = roundLogs.addProp("connectLenient", true);
            connectPerpendicular = roundLogs.addProp("connectPerpendicular", true);
            connectGrass = roundLogs.addProp("connectGrass", true);
            zProtection = roundLogs.addProp("zProtection", 0.99, 0.9, 1.0);

            roundLogs.finalizeCategory();
        }
    }

    public static class ExtraLeaves {

        public static boolean enabled;
        public static boolean snowEnabled;
        public static boolean surfaceOnly;
        public static float hOffset;
        public static float vOffset;
        public static float size;
        public static boolean dense;
        public static boolean renderBaseBlock;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder extraLeaves = new ConfigCategoryBuilder("extraLeaves", config);

            enabled = extraLeaves.addProp("enabled", true);
            extraLeaves.setRecentDescription("betterfoliage.enabled");

            snowEnabled = extraLeaves.addProp("snowEnabled", true);
            surfaceOnly = extraLeaves.addProp("surfaceOnly", true);

            hOffset = (float) extraLeaves.addProp("hOffset", 0.2, 0.0, 0.4);
            extraLeaves.setRecentDescription("betterfoliage.hOffset");

            vOffset = (float) extraLeaves.addProp("vOffset", 0.1, 0.0, 0.4);
            extraLeaves.setRecentDescription("betterfoliage.vOffset");

            size = (float) extraLeaves.addProp("size", 1.4, 0.75, 2.5);
            extraLeaves.setRecentDescription("betterfoliage.size");

            dense = extraLeaves.addProp("dense", false);
            renderBaseBlock = extraLeaves.addProp("renderBaseBlock", true);

            extraLeaves.finalizeCategory();
        }
    }

    public static class FallingLeaves {

        public static boolean enabled;
        public static float speed;
        public static float rotationSpeed;
        public static float groundDampening;
        public static double windStrength;
        public static double stormStrength;
        public static float size;
        public static double chance;
        public static float perturb;
        public static int maxLifetimeTicks;
        public static int minLifetimeTicks;
        public static int maxFadeoutTicks;
        public static boolean fadeoutOnGroundHit;
        public static boolean opacityHack;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder fallingLeaves = new ConfigCategoryBuilder("fallingLeaves", config);

            enabled = fallingLeaves.addProp("enabled", true);
            fallingLeaves.setRecentDescription("betterfoliage.enabled");

            speed = (float) fallingLeaves.addProp("speed", 0.05, 0.01, 0.15);
            rotationSpeed = (float) fallingLeaves.addProp("rotationSpeed", 1.0, 0.0, 5.0);
            groundDampening = (float) fallingLeaves.addProp("groundDampening", 0.7, 0.0, 1.0);
            windStrength = fallingLeaves.addProp("windStrength", 0.5, 0.1, 2.0);
            stormStrength = fallingLeaves.addProp("stormStrength", 0.8, 0.1, 2.0);

            size = (float) fallingLeaves.addProp("size", 0.7, 0.0, 1.0);
            fallingLeaves.setRecentDescription("betterfoliage.size");

            chance = (float) fallingLeaves.addProp("chance", 0.05, 0.001, 1.0);
            perturb = (float) fallingLeaves.addProp("perturb", 0.25, 0.01, 1.0);

            final double maxLifetime = fallingLeaves.addProp("maxLifetime", 5.0, 1.0, 60.0);
            fallingLeaves.setRecentDescription("betterfoliage.maxLifetime");
            maxLifetimeTicks = (int) Math.round(maxLifetime * 20);
            minLifetimeTicks = (int) Math.round(maxLifetime * 0.6 * 20);

            final double maxFadeout = fallingLeaves.addProp("fadeoutTime", 1.0, 0.1, 10.0);
            maxFadeoutTicks = (int) Math.round(maxFadeout * 20);

            fadeoutOnGroundHit = fallingLeaves.addProp("fadeoutOnGroundHit", false);
            opacityHack = fallingLeaves.addProp("opacityHack", true);

            fallingLeaves.finalizeCategory();
        }
    }

    public static class RisingSouls {

        public static boolean enabled;
        public static float speed;
        public static double chance;
        public static float perturb;
        public static float headSize;
        public static float trailSize;
        public static int trailDensity;
        public static float sizeDecay;
        public static float opacity;
        public static int maxLifetimeTicks;
        public static int minLifetimeTicks;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder risingSouls = new ConfigCategoryBuilder("risingSouls", config);

            enabled = risingSouls.addProp("enabled", true);
            risingSouls.setRecentDescription("betterfoliage.enabled");

            speed = (float) risingSouls.addProp("speed", 0.1, 0.001, 1.0);
            chance = risingSouls.addProp("chance", 0.02, 0.001, 1.0);

            perturb = (float) risingSouls.addProp("perturb", 0.05, 0.01, 0.25);
            headSize = (float) risingSouls.addProp("headSize", 1.0, 0.25, 1.5);
            trailSize = (float) risingSouls.addProp("trailSize", 0.75, 0.25, 1.5);
            trailDensity = risingSouls.addProp("trailDensity", 3, 1, 16);
            sizeDecay = (float) risingSouls.addProp("sizeDecay", 0.97, 0.5, 1.0);
            opacity = (float) risingSouls.addProp("opacity", 0.5, 0.01, 1.0);

            double maxLifetime = risingSouls.addProp("maxLifetime", 4.0, 1.0, 15.0);
            risingSouls.setRecentDescription("betterfoliage.maxLifetime");
            maxLifetimeTicks = (int) Math.round(maxLifetime * 20);
            minLifetimeTicks = (int) Math.round(maxLifetime * 0.6 * 20);

            risingSouls.finalizeCategory();
        }
    }

    public static class Netherrack {

        public static boolean enabled;
        public static float hOffset;
        public static float heightMin;
        public static float heightMax;
        public static float size;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder netherrack = new ConfigCategoryBuilder("netherrack", config);

            enabled = netherrack.addProp("enabled", true);
            netherrack.setRecentDescription("betterfoliage.enabled");

            hOffset = (float) netherrack.addProp("hOffset", 0.2, 0.0, 0.4);
            netherrack.setRecentDescription("betterfoliage.hOffset");

            heightMin = (float) netherrack.addProp("heightMin", 0.6, 0.1, 1.5);
            netherrack.setRecentDescription("betterfoliage.heightMin");

            heightMax = (float) netherrack.addProp("heightMax", 0.8, 0.1, 1.5);
            netherrack.setRecentDescription("betterfoliage.heightMax");

            size = (float) netherrack.addProp("size", 1.0, 0.5, 1.5);
            netherrack.setRecentDescription("betterfoliage.size");

            netherrack.finalizeCategory();
        }
    }

    public static class Lilypad {

        public static boolean enabled;
        public static float hOffset;
        public static double flowerChance;
        public static float flowerSize;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder lilypad = new ConfigCategoryBuilder("lilypad", config);

            enabled = lilypad.addProp("enabled", true);
            lilypad.setRecentDescription("betterfoliage.enabled");

            hOffset = (float) lilypad.addProp("hOffset", 0.1, 0.0, 0.25);
            lilypad.setRecentDescription("betterfoliage.hOffset");

            flowerChance = lilypad.addProp("flowerChance", 0.25, 0.0, 1.0);
            flowerSize = (float) lilypad.addProp("flowerSize", 0.5, 0.1, 1.0);

            lilypad.finalizeCategory();
        }
    }

    public static class Cactus {

        public static boolean enabled;
        public static boolean renderStem;
        public static boolean renderArms;
        public static float armOffset;
        public static float armSize;
        public static float armChance;
        public static float stemSizeVariation;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder cactus = new ConfigCategoryBuilder("cactus", config);

            renderStem = cactus.addProp("renderStem", true);
            renderArms = cactus.addProp("renderArms", true);

            enabled = renderStem | renderArms;

            armOffset = (float) cactus.addProp("armOffset", 0.1, 0.0, 0.5);
            armSize = (float) cactus.addProp("armSize", 0.8, 0.5, 1.5);
            armChance = (float) cactus.addProp("armChance", 0.25, 0.0, 1.0);
            stemSizeVariation = (float) cactus.addProp("stemSizeVariation", 0.1, 0.0, 0.5);

            cactus.finalizeCategory();
        }
    }

    public static class Reeds {

        public static boolean enabled;
        public static float hOffset;
        public static float heightMin;
        public static float heightMax;
        public static float size;
        public static float threshold;
        public static boolean mixTextures;

        // TODO: Add shader wind support
        // public static boolean shaderWind = true;

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder reeds = new ConfigCategoryBuilder("reeds", config);

            enabled = reeds.addProp("enabled", true);
            reeds.setRecentDescription("betterfoliage.enabled");

            hOffset = (float) reeds.addProp("hOffset", 0.2, 0.0, 0.4);
            reeds.setRecentDescription("betterfoliage.hOffset");

            heightMin = (float) reeds.addProp("heightMin", 1.7, 1.5, 3.5);
            reeds.setRecentDescription("betterfoliage.heightMin");

            heightMax = (float) reeds.addProp("heightMax", 2.2, 1.5, 3.5);
            reeds.setRecentDescription("betterfoliage.heightMax");

            size = (float) reeds.addProp("size", 1.0, 0.5, 1.5);
            reeds.setRecentDescription("betterfoliage.size");

            threshold = (float) reeds.addProp("threshold", 0.5, 0.0, 1.0);
            reeds.setRecentDescription("betterfoliage.threshold");

            mixTextures = reeds.addProp("mixTextures", true);

            final String[] biomeFilters = reeds.addProp("biomes");
            reeds.setRecentDescription("betterfoliage.biomes");
            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesReeds.cfg"));

            reeds.finalizeCategory();
        }
    }

    public static class Algae {

        public static boolean enabled;
        public static float hOffset;
        public static float size;
        public static float threshold;
        public static boolean mixTextures;

        // TODO: Add shader wind support
        // public static boolean shaderWind = true;

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder algae = new ConfigCategoryBuilder("algae", config);

            enabled = algae.addProp("enabled", true);
            algae.setRecentDescription("betterfoliage.enabled");

            hOffset = (float) algae.addProp("hOffset", 0.1, 0.0, 0.5);
            algae.setRecentDescription("betterfoliage.hOffset");

            size = (float) algae.addProp("size", 1.0, 0.5, 1.5);
            algae.setRecentDescription("betterfoliage.size");

            threshold = (float) algae.addProp("threshold", 0.5, 0.0, 1.0);
            algae.setRecentDescription("betterfoliage.threshold");

            mixTextures = algae.addProp("mixTextures", true);

            final String[] biomeFilters = algae.addProp("biomes");
            algae.setRecentDescription("betterfoliage.biomes");
            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesAlgae.cfg"));

            algae.finalizeCategory();
        }
    }

    public static class Coral {

        public static boolean enabled;
        public static boolean renderCoral;
        public static boolean renderCrust;
        public static boolean shallowWater;
        public static float hOffset;
        public static float vOffset;
        public static float coralSize;
        public static float crustSize;
        public static double chance;
        public static float threshold;
        public static boolean mixCoral;
        public static boolean mixCrust;

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            final ConfigCategoryBuilder coral = new ConfigCategoryBuilder("coral", config);

            renderCoral = coral.addProp("renderCoral", true);
            renderCrust = coral.addProp("renderCrust", true);

            enabled = renderCoral | renderCrust;

            shallowWater = coral.addProp("shallowWater", false);

            hOffset = (float) coral.addProp("hOffset", 0.2, 0.0, 0.4);
            coral.setRecentDescription("betterfoliage.hOffset");

            vOffset = (float) coral.addProp("vOffset", 0.1, 0.0, 0.4);
            coral.setRecentDescription("betterfoliage.vOffset");

            coralSize = (float) coral.addProp("coralSize", 0.7, 0.5, 1.5);
            crustSize = (float) coral.addProp("crustSize", 1.4, 0.5, 1.5);
            chance = coral.addProp("chance", 0.5, 0.0, 1.0);

            threshold = (float) coral.addProp("threshold", 0.75, 0.0, 1.0);
            coral.setRecentDescription("betterfoliage.threshold");

            mixCoral = coral.addProp("mixCoral", true);
            mixCrust = coral.addProp("mixCrust", true);

            final String[] biomeFilters = coral.addProp("biomes");
            coral.setRecentDescription("betterfoliage.biomes");
            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesCoral.cfg"));

            coral.finalizeCategory();
        }
    }

    public static final String CONFIG_FILE_NAME = BetterFoliageMod.MOD_ID + "_LegacyEdition";

    private static Configs instance = null;

    private Configuration attachedConfig = null;

    private Configs() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    public static Configuration getAttachedConfigFile() {
        return instance.attachedConfig;
    }

    @SubscribeEvent
    public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(BetterFoliageMod.MOD_ID)) {
            onConfigChange();
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }

    @SubscribeEvent
    public void onStitchEvent(final TextureStitchEvent.Pre event) {
        // Only reload configs via F3 + T if the player is in-game (aka not the Main Menu)
        if (Minecraft.getMinecraft().theWorld != null) {
            onConfigChange();
        }
    }

    /// Reinitialize configs after a change or reload while the game is already running
    private void onConfigChange() {
        postInit();
    }

    /// Fist time initializer. Only runs once when opening the game
    public static void preInit(final File configDir) {
        final File configFile = new File(configDir, CONFIG_FILE_NAME + ".cfg");
        Configuration configs = new Configuration(configFile, null, true);

        if (instance == null) {
            instance = new Configs();
        }

        instance.attachedConfig = configs;
    }

    /// Runs on post init startup and whenever configs are reloaded while the game is open
    public static void postInit() {
        final Configuration config = instance.attachedConfig;
        if (config == null) {
            return;
        }

        if (!config.hasChanged()) {
            config.load();
        }

        ConfigCategoryBuilder general = new ConfigCategoryBuilder(Configuration.CATEGORY_GENERAL, config);
        enabled = general.addProp("enabled", true);
        seed = general.addProp("seed", 402653189);
        general.recentProp.setShowInGui(false);
        general.finalizeCategory();

        Blocks.init(config);
        ConnectedGrass.init(config);
        ShortGrass.init(config);
        RoundLogs.init(config);
        ExtraLeaves.init(config);
        FallingLeaves.init(config);
        RisingSouls.init(config);
        Netherrack.init(config);
        Lilypad.init(config);
        Cactus.init(config);
        Reeds.init(config);
        Algae.init(config);
        Coral.init(config);

        if (config.hasChanged()) {
            config.save();
        }
    }
}
