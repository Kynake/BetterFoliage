package mods.betterfoliage;

import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.client.ClientRegistry;
import mods.betterfoliage.client.config.Config;

@Mod(
    modid = BetterFoliageMod.MOD_ID,
    name = BetterFoliageMod.MOD_NAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*",
    guiFactory = "mods.betterfoliage.client.gui.ConfigGuiFactory",
    dependencies = "after:angelica;after:notfine;")
public class BetterFoliageMod {

    public static final String MOD_ID = "BetterFoliage";
    public static final String MOD_NAME = "Better Foliage";
    public static final String DOMAIN = "betterfoliage";
    public static final String LEGACY_DOMAIN = "bettergrassandleaves";

    public static Logger log = null;

    private static BetterFoliageMod instance;

    @Mod.InstanceFactory
    public static BetterFoliageMod instanceFactory() {
        if (instance == null) {
            instance = new BetterFoliageMod();
        }

        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        Configuration config = new Configuration(event.getSuggestedConfigurationFile(), null, true);
        Config.INSTANCE.attach(config);
        if (event.getSide() == Side.CLIENT) {
            ClientRegistry.preInit();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ClientRegistry.init();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {

            // Load bearing log. without this the mod doesn't load!
            // TODO stop disguising initialization in a message log
            Client.INSTANCE.log(Level.INFO, "BetterFoliage initialized");

            ClientRegistry.postInit();
        }
    }

    /** Mod is cosmetic only, always allow connection. */
    @NetworkCheckHandler
    public boolean checkVersion(Map<String, String> mods, Side side) {
        return true;
    }
}
