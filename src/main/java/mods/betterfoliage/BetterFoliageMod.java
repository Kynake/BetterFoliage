package mods.betterfoliage;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import mods.betterfoliage.client.ClientRegistry;
import mods.betterfoliage.config.Configs;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*",
    guiFactory = "mods.betterfoliage.config.gui.ConfigsGuiFactory",
    dependencies = "after:angelica;after:notfine;")
public class BetterFoliageMod {

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

        if (event.getSide() != Side.CLIENT) {
            log.error(
                "Trying to load {} in a non-client environment! This is a client-side only mod, so it will do nothing!",
                Tags.MOD_ID);
            return;
        }

        Configs.preInit(event.getModConfigurationDirectory());

        ClientRegistry.preInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide() != Side.CLIENT) {
            return;
        }

        Configs.postInit();
        ClientRegistry.postInit();
    }

    /** Mod is cosmetic only, always allow connection. */
    @NetworkCheckHandler
    public boolean checkVersion(Map<String, String> mods, Side side) {
        return true;
    }
}
