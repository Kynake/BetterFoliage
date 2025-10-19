package mods.betterfoliage.client.integration

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder
import cpw.mods.fml.common.Loader
import mods.octarinecore.metaprog.getJavaClass

enum class CompatibleMod(val modID: String?, val modName: String, val coreModClass: String?, val targetClass: String?) :
    IMod,
    ITargetMod {
    GT5U("gregtech", "GregTech", "gregtech.asm.GTCorePlugin", "gregtech.GTMod"),
    IC2("IC2", "IndustrialCraft 2", "ic2.core.coremod.IC2core", "ic2.core.IC2"),
    TFC("terrafirmacraft", "TerraFirmaCraft", "com.bioxx.tfc.TFCASMLoadingPlugin", "com.bioxx.tfc.TerraFirmaCraft"),
    ;

    private val modBuilder by lazy {
        TargetModBuilder().setCoreModClass(coreModClass).setTargetClass(targetClass).setModId(modID)!!
    }

    private var isLoaded = false

    override fun isModLoaded(): Boolean {
        if (isLoaded) return true

        isLoaded = modID?.let { Loader.isModLoaded(it) }
            ?: coreModClass?.let { getJavaClass(it) != null }
            ?: targetClass?.let { getJavaClass(it) != null }
            ?: false

        return isLoaded
    }

    override fun getBuilder() = modBuilder
}

interface IMod {
    fun isModLoaded(): Boolean
}
