package mods.betterfoliage.config;

import java.io.File;

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

// TODO: Make configurable from in-game menu like the old configs
public final class Configs {

    /// Configs

    // GENERAL
    public static boolean enabled = true;
    private static final String enabledGeneralDesc = "General on/off switch for all of the mod's functionalities";

    // Mod uses a set number as random seed because the world seed can't be accessed Client-side in a server.
    public static int seed = 402653189;
    private static final String seedDesc = """
        Primary mod seed used as the base for pseudo-random placement of features.
        Allows features to be consistent across different players.
        Change it to a secret number to make it harder for someone to brute force your coordinates from a screenshot, if you care about that.
        Default:\s"""
        + seed;

    private static final String hOffsetDesc = "The distance this element is shifted horizontally, in blocks";
    private static final String vOffsetDesc = "The distance this element is shifted vertically, in blocks";
    private static final String sizeDesc = "Size of this element";
    private static final String heightMinDesc = "Minimum height of element";
    private static final String heightMaxDesc = "Maximum height of element";
    private static final String thresholdDesc = "Chance that an eligible block will have this feature. Used as a cutoff value in a 3D noise generator";
    private static final String biomesDesc = "A list of biomes filters that determine if this feature is allowed to appear in this biome or not."
        + "\nCheck the wiki for more details on how these filters work";

    public static class ConnectedGrass {

        public static boolean enabled = true;
        private static final String enabledDesc = "Use grass top texture on all sides if a dirt block is beneath it";

        public static boolean snowEnabled = false;
        private static final String snowEnabledDesc = "If a connected grass block is covered by snow, use the snow's texture for the grass sides instead of the grass top texture";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "connectedGrass";

            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "snowEnabled", snowEnabled, snowEnabledDesc);
            snowEnabled = prop.getBoolean();
        }
    }

    public static class ShortGrass {

        public static boolean grassEnabled = true;
        private static final String grassEnabledDesc = "Render tufts of grass on top of appropriate blocks";

        public static boolean myceliumEnabled = true;
        private static final String myceliumEnabledDesc = "Render tufts of mycelium on top of mycelium blocks";

        public static boolean snowEnabled = true;
        private static final String snowEnabledDesc = "Render snow tinted short grass if the block is covered in snow";

        public static float hOffset;

        public static float heightMin;

        public static float heightMax;

        public static float size;

        public static boolean mixTextures = true;
        private static final String mixTexturesDesc = "Allow mixing of two different textures for each tuft of grass/mycelium in each block";

        public static boolean useGenerated = false;
        private static final String useGeneratedDesc = "Generate Short Grass textures from existing grass foliage textures";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "shortGrass";

            Property prop = config.get(category, "grassEnabled", grassEnabled, grassEnabledDesc);
            grassEnabled = prop.getBoolean();

            prop = config.get(category, "myceliumEnabled", myceliumEnabled, myceliumEnabledDesc);
            myceliumEnabled = prop.getBoolean();

            prop = config.get(category, "snowEnabled", snowEnabled, snowEnabledDesc);
            snowEnabled = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.2, hOffsetDesc, 0.0, 0.4);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "heightMin", 0.6, heightMinDesc, 0.1, 2.5);
            heightMin = (float) prop.getDouble();

            prop = config.get(category, "heightMax", 0.6, heightMaxDesc, 0.1, 2.5);
            heightMax = (float) prop.getDouble();

            prop = config.get(category, "size", 1.0, sizeDesc, 0.5, 1.5);
            size = (float) prop.getDouble();

            prop = config.get(category, "mixTextures", mixTextures, mixTexturesDesc);
            mixTextures = prop.getBoolean();

            prop = config.get(category, "useGenerated", useGenerated, useGeneratedDesc);
            useGenerated = prop.getBoolean();
        }
    }

    public static class RoundLogs {

        public static boolean enabled = false;
        private static final String enabledDesc = "Render logs with rounded corners";

        public static double radiusSmall;
        private static final String radiusSmallDesc = """
            Chamfer radius for non connected logs. Some interesting values include:
            Default: 0.25
            Equally sided octagon: 0.292893218813 (This value won't line up with the default texture's pixel grid)
            Closest value that lines up with the pixel grid: 0.3125""";

        public static double radiusLarge;
        private static final String radiusLargeDesc = "Chamfer radius for logs connected to other blocks";

        public static boolean connectToSolids = false;
        private static final String connectToSolidsDesc = "Should round logs connect to other non-log solid blocks";

        public static boolean connectLenient = true;
        private static final String connectLenientDesc = "Allow logs to connect in an L shape, not requiring a full 2x2 grid";

        public static boolean connectPerpendicular = true;
        private static final String connectPerpendicularDesc = "Connect perpendicular logs by rendering an end cap from one round log at the same coordinates as the other";

        public static boolean connectGrass = true;
        private static final String connectGrassDesc = "Render a grass block under round logs instead of dirt if there another grass block nearby";

        public static double zProtection;
        private static final String zProtectionDesc = """
            How much to shrink the radius of perpendicular end cap connectors to prevent z-fighting.
            Higher values will mean that the end cap connectors will have less visible distortion from up close, but may flicker (z-fight) when viewed from afar.
            This value should be kept as high as possible without noticing any visual glitches.""";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "roundLogs";

            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "radiusSmall", 0.25, radiusSmallDesc, 0.0, 0.5);
            radiusSmall = prop.getDouble();

            prop = config.get(category, "radiusLarge", 0.5, radiusLargeDesc, 0.0, 0.5);
            radiusLarge = prop.getDouble();

            prop = config.get(category, "connectToSolids", connectToSolids, connectToSolidsDesc);
            connectToSolids = prop.getBoolean();

            prop = config.get(category, "connectLenient", connectLenient, connectLenientDesc);
            connectLenient = prop.getBoolean();

            prop = config.get(category, "connectPerpendicular", connectPerpendicular, connectPerpendicularDesc);
            connectPerpendicular = prop.getBoolean();

            prop = config.get(category, "connectGrass", connectGrass, connectGrassDesc);
            connectGrass = prop.getBoolean();

            prop = config.get(category, "zProtection", 0.99, zProtectionDesc, 0.9, 1.0);
            zProtection = prop.getDouble();
        }
    }

    public static class ExtraLeaves {

        public static boolean enabled = true;
        private static final String enabledDesc = "Render extra round leaves on leaf blocks";

        public static boolean snowEnabled = true;
        private static final String snowEnabledDesc = "render snow overlay on extra leaves with snow layer on top";

        public static boolean surfaceOnly = true;
        private static final String surfaceOnlyDesc = "Extra leaves will only be rendered on blocks with at least one exposed side";

        public static float hOffset;
        public static float vOffset;
        public static float size;

        public static boolean dense = false;
        private static final String denseDesc = "Render extra leaves for all three axes of a leaf block, not just the vertical axis";

        public static boolean renderBaseBlock = true;
        private static final String renderBaseBlockDesc = "Should the base leaf block be rendered too. Works best with Dense mode enabled";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "extraLeaves";

            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "snowEnabled", snowEnabled, snowEnabledDesc);
            snowEnabled = prop.getBoolean();

            prop = config.get(category, "surfaceOnly", surfaceOnly, surfaceOnlyDesc);
            surfaceOnly = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.2, hOffsetDesc, 0.0, 0.4);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "vOffset", 0.1, vOffsetDesc, 0.0, 0.4);
            vOffset = (float) prop.getDouble();

            prop = config.get(category, "size", 1.4, sizeDesc, 0.75, 2.5);
            size = (float) prop.getDouble();

            prop = config.get(category, "dense", dense, denseDesc);
            dense = prop.getBoolean();

            prop = config.get(category, "renderBaseBlock", renderBaseBlock, renderBaseBlockDesc);
            renderBaseBlock = prop.getBoolean();
        }
    }

    public static class FallingLeaves {

        public static boolean enabled = true;
        private static final String enabledDesc = "Should emit falling leaf particles from the bottom of leaf blocks";

        public static float speed;
        private static final String speedDesc = "Overall leaf particle speed";

        public static float rotationSpeed;
        private static final String rotationSpeedDesc = "Leaf particle rotation speed";

        public static float groundDampening;
        private static final String groundDampeningDesc = "dampening value of the \"settling\" effect to apply to the leaf when hitting the ground";

        public static double windStrength;
        private static final String windStrengthDesc = "Magnitude of wind effects in good weather (spread of normal distribution centered on 0)";

        public static double stormStrength;
        private static final String stormStrengthDesc = "Additional magnitude of wind effects in rainy weather (spread of normal distribution centered on 0)";

        public static float size;

        public static double chance;
        private static final String chanceDesc = "Chance that a random tick of a leaf block will spawn a particle";

        public static float perturb;
        private static final String perturbDesc = "Magnitude of perturbation effect. Adds a corkscrew-like motion to the particle synchronized to its rotation";

        public static int maxLifetimeTicks;
        public static int minLifetimeTicks;
        private static final String maxLifetimeDesc = "Maximum lifetime of particle in seconds. Minimum lifetime is 60% of this value";

        public static int maxFadeoutTicks;
        private static final String maxFadeoutDesc = "How long the leaf fadeout should last, in seconds. Will be less than it's total lifetime when spawned";

        public static boolean fadeoutOnGroundHit = false;
        private static final String fadeoutOnGroundHitDesc = "Should the leaf immediately start to fade out when hitting the ground";

        public static boolean opacityHack = true;
        private static final String opacityHackDesc = "Stop transparent blocks obscuring particles even when particle is in front. WARNING: May cause glitches.";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "fallingLeaves";

            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "speed", 0.05, speedDesc, 0.01, 0.15);
            speed = (float) prop.getDouble();

            prop = config.get(category, "rotationSpeed", 1.0, rotationSpeedDesc, 0.0, 5.0);
            rotationSpeed = (float) prop.getDouble();

            prop = config.get(category, "groundDampening", 0.7, groundDampeningDesc, 0.0, 1.0);
            groundDampening = (float) prop.getDouble();

            prop = config.get(category, "windStrength", 0.5, windStrengthDesc, 0.1, 2.0);
            windStrength = prop.getDouble();

            prop = config.get(category, "stormStrength", 0.8, stormStrengthDesc, 0.1, 2.0);
            stormStrength = prop.getDouble();

            prop = config.get(category, "size", 0.075, sizeDesc, 0.025, 0.15);
            size = (float) prop.getDouble();

            prop = config.get(category, "chance", 0.05, chanceDesc, 0.01, 1.0);
            chance = prop.getDouble();

            prop = config.get(category, "perturb", 0.25, perturbDesc, 0.01, 1.0);
            perturb = (float) prop.getDouble();

            prop = config.get(category, "lifetime", 5.0, maxLifetimeDesc, 1.0, 15.0);
            double maxLifetime = prop.getDouble();
            maxLifetimeTicks = (int) Math.round(maxLifetime * 20);
            minLifetimeTicks = (int) Math.round(maxLifetime * 0.6 * 20);

            prop = config.get(category, "fadeoutTime", 1.0, maxFadeoutDesc, 0.1, 10.0);
            maxFadeoutTicks = (int) Math.round(prop.getDouble() * 20);

            prop = config.get(category, "fadeoutOnGroundHit", fadeoutOnGroundHit, fadeoutOnGroundHitDesc);
            fadeoutOnGroundHit = prop.getBoolean();

            prop = config.get(category, "opacityHack", opacityHack, opacityHackDesc);
            opacityHack = prop.getBoolean();
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

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "risingSoul";

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
            maxLifetimeTicks = (int) Math.round(maxLifetime * 20);
            minLifetimeTicks = (int) Math.round(maxLifetime * 0.6 * 20);

            prop = config.get(category, "trailDensity", 3, trailDensityDesc, 1, 16);
            trailDensity = prop.getInt();
        }
    }

    public static class Netherrack {

        public static boolean enabled = true;
        private static final String enabledDesc = "Should place hanging vines under netherrack blocks";

        public static float hOffset;
        public static float heightMin;
        public static float heightMax;
        public static float size;

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "netherrack";

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

    public static class Lilypad {

        public static boolean enabled = true;
        private static final String enabledDesc = "Should draw flowers and roots above and bellow lilypads";

        public static float hOffset;

        public static double flowerChance;
        private static final String flowerChanceDesc = "Chance that a lilypad will have a flower on top";

        public static float flowerSize;
        private static final String flowerSizeDesc = "Size of the lilypad flowers";

        public static void initializeFileConfigs(final Configuration config) {
            final String category = "lilypad";

            Property prop = config.get(category, "enabled", enabled, enabledDesc);
            enabled = prop.getBoolean();

            prop = config.get(category, "hOffset", 0.1, hOffsetDesc, 0.0, 0.25);
            hOffset = (float) prop.getDouble();

            prop = config.get(category, "flowerChance", 0.25, flowerChanceDesc, 0.0, 1.0);
            flowerChance = prop.getDouble();

            prop = config.get(category, "flowerSize", 0.5, flowerSizeDesc, 0.1, 1.0);
            flowerSize = (float) prop.getDouble();
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

        public static void initializeFileConfigs(final Configuration config) {
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

        public static boolean[] biomes;

        public static void initializeFileConfigs(final Configuration config) {
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
        }

        public static void postInit(final Configuration config) {
            String[] biomeFilters = config.get(category, "biomes", new String[] {}, biomesDesc)
                .getStringList();

            biomes = BiomeConfigParser
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

        private static final String category = "reeds";

        public static boolean enabled = true;
        private static final String enabledDesc = "Should render algae on dirt blocks in deep water";

        public static float hOffset;

        public static float size;

        public static float threshold;

        public static boolean mixTextures = true;
        private static final String mixTexturesDesc = "Allow mixing of two different textures for each rendered algae";

        public static boolean[] biomes;

        public static void initializeFileConfigs(final Configuration config) {
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
        }

        public static void postInit(final Configuration config) {
            String[] biomeFilters = config.get(category, "biomes", new String[] {}, biomesDesc)
                .getStringList();

            biomes = BiomeConfigParser
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

    private static final String CONFIG_FILE_NAME = BetterFoliageMod.MOD_ID + "_LegacyEdition";

    private static Configs instance = null;

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

    /// Fist time initializer. Only runs once when opening the game
    public static void preInit(final File configDir) {
        final File configFile = new File(configDir, CONFIG_FILE_NAME + ".cfg");
        Configuration configs = new Configuration(configFile, null, true);

        if (instance == null) {
            instance = new Configs();
        }

        instance.attachedConfig = configs;

        initializeFileConfigs(configs);
    }

    public static void postInit() {
        final Configuration config = instance.attachedConfig;

        if (config == null) {
            return;
        }

        Reeds.postInit(config);
        Algae.postInit(config);

        if (config.hasChanged()) {
            config.save();
        }
    }

    /// Reinitialize configs after a change or reload while the game is already running
    private void onConfigChange() {
        if (attachedConfig == null) {
            return;
        }

        initializeFileConfigs(attachedConfig);

        postInit();
    }

    /// Initializes all static configs that should be read from a config file.
    public static void initializeFileConfigs(final Configuration config) {
        config.load();

        Property prop = config.get(Configuration.CATEGORY_GENERAL, "enabled", enabled, enabledGeneralDesc);
        enabled = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "seed", seed, seedDesc);
        seed = prop.getInt();

        ConnectedGrass.initializeFileConfigs(config);
        ShortGrass.initializeFileConfigs(config);
        RoundLogs.initializeFileConfigs(config);
        ExtraLeaves.initializeFileConfigs(config);
        FallingLeaves.initializeFileConfigs(config);
        RisingSouls.initializeFileConfigs(config);
        Netherrack.initializeFileConfigs(config);
        Lilypad.initializeFileConfigs(config);
        Cactus.initializeFileConfigs(config);
        Reeds.initializeFileConfigs(config);
        Algae.initializeFileConfigs(config);
    }
}
