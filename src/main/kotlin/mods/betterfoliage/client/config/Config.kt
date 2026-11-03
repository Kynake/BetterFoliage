package mods.betterfoliage.client.config

import cpw.mods.fml.client.event.ConfigChangedEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.BetterFoliageMod
import mods.octarinecore.common.config.DelegatingConfig
import net.minecraft.client.Minecraft

// Config singleton
@Suppress("ClassName") // Ignored here because the class names are used as categories in the mod's configs
@SideOnly(Side.CLIENT)
object Config : DelegatingConfig(BetterFoliageMod.MOD_ID, BetterFoliageMod.DOMAIN) {

    // TODO: SPECIAL CASE Block
    object blocks {
//        val dirt = BlockMatcher(BetterFoliageMod.DOMAIN, "DirtDefault.cfg")
//        val grass = BlockMatcher(BetterFoliageMod.DOMAIN, "GrassDefault.cfg")
//         val leaves = BlockMatcher(BetterFoliageMod.DOMAIN, "LeavesDefault.cfg")
        // val fallingLeaves = BlockMatcher(BetterFoliageMod.DOMAIN, "FallingLeavesDefault.cfg")

        // val crops = BlockMatcher(BetterFoliageMod.DOMAIN, "CropDefault.cfg")
        val logs = BlockMatcher(BetterFoliageMod.DOMAIN, "LogDefault.cfg")
        val sand = BlockMatcher(BetterFoliageMod.DOMAIN, "SandDefault.cfg")
        val lilypad = BlockMatcher(BetterFoliageMod.DOMAIN, "LilypadDefault.cfg")
        val cactus = BlockMatcher(BetterFoliageMod.DOMAIN, "CactusDefault.cfg")
    }

    //    object hangingGrass {
    //        var enabled by featureEnable()
    //        var distance by distanceLimit()
    //        var size by double(min=0.25, max=1.5, default=0.75).lang("size")
    //        var separation by double(max=0.5, default=0.25)
    //    }

    override fun onChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        super.onChange(event)
        Minecraft.getMinecraft().renderGlobal.loadRenderers()
    }
}
