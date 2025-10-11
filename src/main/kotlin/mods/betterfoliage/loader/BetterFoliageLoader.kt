package mods.betterfoliage.loader

import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import mods.octarinecore.metaprog.ASMPlugin

// TODO Replace with mixins
@IFMLLoadingPlugin.TransformerExclusions(
    "mods.betterfoliage.loader",
    "mods.octarinecore.metaprog",
    "kotlin",
    "mods.betterfoliage.kotlin")
class BetterFoliageLoader : ASMPlugin(BetterFoliageTransformer::class.java)
