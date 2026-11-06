package mods.betterfoliage.config;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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
    private static final String heightMinDesc = "Minimum height of element";
    private static final String heightMaxDesc = "Maximum height of element";
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

            final double maxFadeout =  fallingLeaves.addProp("fadeoutTime", 1.0, 0.1, 10.0);
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

        public static boolean enabled = true;

        public static boolean renderStem = true;
        private static final String renderStemDesc = "Render extra textures in the main body of cacti";

        public static boolean renderArms = true;
        private static final String renderArmsDesc = "Render occasional arms in the sides of cactus blocks";

        public static float armOffset;
        private static final String armOffsetDesc = "Offset of the cactus arms along each axis of the cactus's side plane";

        public static float armSize;
        private static final String armSizeDesc = "Size of the cactus arms";

        public static float armChance;
        private static final String armChanceDesc = "Chance that each side of a cactus block will have an arm";

        public static float stemSizeVariation;
        private static final String stemSizeVariationDesc = "Amount of random variation on cactus stem size";

        public static void init(final Configuration config) {
            final String category = "cactus";

            Property prop = config.get(category, "renderStem", renderStem, renderStemDesc);
            renderStem = prop.getBoolean();

            prop = config.get(category, "renderArms", renderArms, renderArmsDesc);
            renderArms = prop.getBoolean();

            enabled = renderStem | renderArms;

            prop = config.get(category, "armOffset", 0.1, armOffsetDesc, 0.0, 0.5);
            armOffset = (float) prop.getDouble();

            prop = config.get(category, "armSize", 0.8, armSizeDesc, 0.5, 1.5);
            armSize = (float) prop.getDouble();

            prop = config.get(category, "armChance", 0.25, armChanceDesc, 0.0, 1.0);
            armChance = (float) prop.getDouble();

            prop = config.get(category, "stemSizeVariation", 0.1, stemSizeVariationDesc, 0.0, 0.5);
            stemSizeVariation = (float) prop.getDouble();
        }
    }

    public static class Reeds {

        private static final String category = "reeds";

        public static boolean enabled = true;
        private static final String enabledDesc = "Should render reeds on dirt blocks in shallow water";

        public static float hOffset;

        public static float heightMin;
        public static float heightMax;

        public static float size;

        public static float threshold;

        public static boolean mixTextures = true;
        private static final String mixTexturesDesc = "Allow mixing of two different textures for rendered reed";

        // TODO: Add shader wind support
        // public static boolean shaderWind = true;

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.2, hOffsetDesc, 0.0, 0.4);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "heightMin", 1.7, heightMinDesc, 1.5, 3.5);
            heightMin = (float) prop.getDouble();

            prop = config.get(category, "heightMax", 2.2, heightMaxDesc, 1.5, 3.5);
            heightMax = (float) prop.getDouble();

            prop = config.get(category, "size", 1.0, sizeDesc, 0.5, 1.5);
            size = (float) prop.getDouble();

            prop = config.get(category, "threshold", 0.5, thresholdDesc, 0.0, 1.0);
            threshold = (float) prop.getDouble();

            prop = config.get(category, "mixTextures", mixTextures, mixTexturesDesc);
            mixTextures = prop.getBoolean();

            // Biomes
            String[] biomeFilters = config.get(category, "biomes", new String[] {}, biomesDesc)
                .getStringList();

            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesReeds.cfg"));

            final BiomeGenBase[] gameBiomes = BiomeGenBase.getBiomeGenArray();
            for (int i = 0; i < biomes.length; i++) {
                if (biomes[i]) {
                    BiomeGenBase biome = gameBiomes[i];
                    BetterFoliageMod.log.info("Valid biome for {}: [{}] {}", category, i, biome.biomeName);
                }
            }
        }
    }

    public static class Algae {

        private static final String category = "algae";

        public static boolean enabled = true;
        private static final String enabledDesc = "Should render algae on dirt blocks in deep water";

        public static float hOffset;

        public static float size;

        public static float threshold;

        public static boolean mixTextures = true;
        private static final String mixTexturesDesc = "Allow mixing of two different textures for each rendered algae";

        // TODO: Add shader wind support
        // public static boolean shaderWind = true;

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.1, hOffsetDesc, 0.0, 0.5);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "size", 1.0, sizeDesc, 0.5, 1.5);
            size = (float) prop.getDouble();

            prop = config.get(category, "threshold", 0.5, thresholdDesc, 0.0, 1.0);
            threshold = (float) prop.getDouble();

            prop = config.get(category, "mixTextures", mixTextures, mixTexturesDesc);
            mixTextures = prop.getBoolean();

            // Biomes
            String[] biomeFilters = config.get(category, "biomes", new String[] {}, biomesDesc)
                .getStringList();

            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesAlgae.cfg"));

            final BiomeGenBase[] gameBiomes = BiomeGenBase.getBiomeGenArray();
            for (int i = 0; i < biomes.length; i++) {
                if (biomes[i]) {
                    BiomeGenBase biome = gameBiomes[i];
                    BetterFoliageMod.log.info("Valid biome for {}: [{}] {}", category, i, biome.biomeName);
                }
            }
        }
    }

    public static class Coral {

        private static final String category = "coral";

        public static boolean enabled;

        public static boolean renderCoral = true;
        private static final String renderCoralDesc = "Should render coral branches on underwater sand blocks";

        public static boolean renderCrust = true;
        private static final String renderCrustDesc = "Should render flat coral overlays on underwater sand blocks";

        public static boolean shallowWater = false;
        private static final String shallowWaterDesc = "Should coral also show up on shallow (1 block deep) water";

        public static float hOffset;
        public static float vOffset;

        public static float coralSize;
        private static final String coralSizeDesc = "Size of the coral branches that stick out";

        public static float crustSize;
        private static final String crustSizeDesc = "Size of the flat coral that covers the block";

        public static double chance;
        private static final String chanceDesc = "Chance that each face of a block will render coral";

        public static float threshold;

        public static boolean mixCoral = true;
        private static final String mixCoralDesc = "Allow mixing of two different textures for each rendered coral branch";

        public static boolean mixCrust = true;
        private static final String mixCrustDesc = "Allow use of different coral crust textures for each side of the host block";

        public static boolean[] biomes;

        public static void init(final Configuration config) {
            Property prop = config.get(category, "renderCoral", renderCoral, renderCoralDesc);
            renderCoral = prop.getBoolean();

            prop = config.get(category, "renderCrust", renderCrust, renderCrustDesc);
            renderCrust = prop.getBoolean();

            enabled = renderCoral | renderCrust;

            prop = config.get(category, "shallowWater", shallowWater, shallowWaterDesc);
            shallowWater = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.2, hOffsetDesc, 0.0, 0.4);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "vOffset", 0.1, vOffsetDesc, 0.0, 0.4);
            vOffset = (float) prop.getDouble();

            prop = config.get(category, "coralSize", 0.7, coralSizeDesc, 0.5, 1.5);
            coralSize = (float) prop.getDouble();

            prop = config.get(category, "crustSize", 1.4, crustSizeDesc, 0.5, 1.5);
            crustSize = (float) prop.getDouble();

            prop = config.get(category, "chance", 0.5, chanceDesc, 0.0, 1.0);
            chance = prop.getDouble();

            prop = config.get(category, "threshold", 0.75, thresholdDesc, 0.0, 1.0);
            threshold = (float) prop.getDouble();

            prop = config.get(category, "mixCoral", mixCoral, mixCoralDesc);
            mixCoral = prop.getBoolean();

            prop = config.get(category, "mixCrust", mixCrust, mixCrustDesc);
            mixCrust = prop.getBoolean();

            // Biomes
            String[] biomeFilters = config.get(category, "biomes", new String[] {}, biomesDesc)
                .getStringList();

            biomes = BiomeFilterSet
                .parseValidBiomeIDs(biomeFilters, new ResourceLocation(BetterFoliageMod.DOMAIN, "biomesCoral.cfg"));

            final BiomeGenBase[] gameBiomes = BiomeGenBase.getBiomeGenArray();
            for (int i = 0; i < biomes.length; i++) {
                if (biomes[i]) {
                    BiomeGenBase biome = gameBiomes[i];
                    BetterFoliageMod.log.info("Valid biome for {}: [{}] {}", category, i, biome.biomeName);
                }
            }
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
