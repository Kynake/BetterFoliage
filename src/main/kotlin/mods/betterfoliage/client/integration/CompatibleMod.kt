package mods.betterfoliage.client.integration

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder
import cpw.mods.fml.common.Loader
import mods.octarinecore.metaprog.getJavaClass

enum class CompatibleMod(val modID: String?, val coreModClass: String?, val targetClass: String?) :
    IMod,
    ITargetMod {
    GT5U("gregtech", "gregtech.asm.GTCorePlugin", null),
    ;

    private val builder = TargetModBuilder().setCoreModClass(coreModClass).setTargetClass(targetClass).setModId(modID)!!
    private var isLoaded = false

    override fun isModLoaded(): Boolean {
        if (isLoaded) return true

        isLoaded = modID?.let { Loader.isModLoaded(it) }
            ?: coreModClass?.let { getJavaClass(it) != null }
            ?: targetClass?.let { getJavaClass(it) != null }
            ?: false

        return isLoaded
    }

    override fun getBuilder() = builder
}

interface IMod {
    fun isModLoaded(): Boolean
}
