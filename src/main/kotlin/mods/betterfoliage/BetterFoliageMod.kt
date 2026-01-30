package mods.betterfoliage

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkCheckHandler
import cpw.mods.fml.relauncher.Side
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

@Mod(
    modid = BetterFoliageMod.MOD_ID,
    name = BetterFoliageMod.MOD_NAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    guiFactory = "mods.betterfoliage.client.gui.ConfigGuiFactory",
    dependencies = "after:angelica;after:notfine;",
)
object BetterFoliageMod {

    const val MOD_ID = "BetterFoliage"
    const val MOD_NAME = "Better Foliage"
    const val DOMAIN = "betterfoliage"
    const val LEGACY_DOMAIN = "bettergrassandleaves"

    var log: Logger? = null
    var config: Configuration? = null

    // the fun never stops with the fun factory! :)
    @JvmStatic
    @Mod.InstanceFactory
    fun factory() = this

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        log = event.modLog
        config = Configuration(event.suggestedConfigurationFile, null, true)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) {
            Config.attach(config!!)
            Client.log(Level.INFO, "BetterFoliage initialized")
        }
    }

    /** Mod is cosmetic only, always allow connection. */
    @NetworkCheckHandler fun checkVersion(mods: Map<String, String>, side: Side) = true
}
