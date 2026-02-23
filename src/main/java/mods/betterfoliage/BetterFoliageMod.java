package mods.betterfoliage;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import mods.betterfoliage.client.Client;
import mods.betterfoliage.client.config.Config;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(
    modid = BetterFoliageMod.MOD_ID,
    name = BetterFoliageMod.MOD_NAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*",
    guiFactory = "mods.betterfoliage.client.gui.ConfigGuiFactory",
    dependencies = "after:angelica;after:notfine;"
)
public class BetterFoliageMod {
    public static final String MOD_ID = "BetterFoliage";
    public static final String MOD_NAME = "Better Foliage";
    public static final String DOMAIN = "betterfoliage";
    public static final String LEGACY_DOMAIN = "bettergrassandleaves";

    public static Logger log = null;

    private static BetterFoliageMod instance;

    @Mod.InstanceFactory
    public static BetterFoliageMod instanceFactory() {
        if(instance == null) {
            instance = new BetterFoliageMod();
        }

        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        Configuration config = new Configuration(event.getSuggestedConfigurationFile(), null, true);
        Config.INSTANCE.attach(config);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {

            // Load bearing log. without this the mod doesn't load!
            // TODO stop disguising initialization in a message log
            Client.INSTANCE.log(Level.INFO, "BetterFoliage initialized");
        }
    }

    /** Mod is cosmetic only, always allow connection. */
    @NetworkCheckHandler
    public boolean checkVersion(Map<String, String> mods, Side side)  {
        return true;
    }
}
