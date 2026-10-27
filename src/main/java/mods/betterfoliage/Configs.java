package mods.betterfoliage;

import java.io.File;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.UseHoeEvent;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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

        prop = config.get(Configuration.CATEGORY_GENERAL, "seed", seed, seedDesc);
        seed = prop.getInt();

        ConnectedGrass.initializeFileConfigs(config);
        ShortGrass.initializeFileConfigs(config);
        ExtraLeaves.initializeFileConfigs(config);
        FallingLeaves.initializeFileConfigs(config);
        RisingSouls.initializeFileConfigs(config);
        Netherrack.initializeFileConfigs(config);
        Lilypad.initializeFileConfigs(config);
        Cactus.initializeFileConfigs(config);

        if (config.hasChanged()) {
            config.save();
        }
    }

    // TODO: Implement
    /// Initializes all static configs that are read from resource packs
    public static void InitializeResourceConfigs() {

    }
}
