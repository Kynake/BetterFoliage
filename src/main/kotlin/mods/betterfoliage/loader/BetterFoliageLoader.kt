package mods.betterfoliage.loader

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader
import com.gtnewhorizon.gtnhmixins.builders.IMixins
import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import mods.betterfoliage.mixins.Mixins
import mods.octarinecore.metaprog.ASMPlugin

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions(
    "mods.betterfoliage.loader",
    "mods.octarinecore.metaprog",
    "kotlin",
    "mods.betterfoliage.kotlin",
)
class BetterFoliageLoader :
    // TODO replace transformer with mixins (Work-In-Progress)
    ASMPlugin(BetterFoliageTransformer::class.java),
    IEarlyMixinLoader {
    override fun getMixinConfig() = "mixins.BetterFoliage.early.json"
    override fun getMixins(loadedCoreMods: Set<String>) = IMixins.getEarlyMixins(Mixins::class.java, loadedCoreMods)!!
}
