package mods.betterfoliage.client.integration

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder
import cpw.mods.fml.common.Loader
import mods.octarinecore.metaprog.getJavaClass

enum class CompatibleMod(val modID: String?, val modName: String, val coreModClass: String?, val targetClass: String?) :
    IMod,
    ITargetMod {
    FORESTRY("forestry", "Forestry", null, "forestry.Forestry"),
    GT5U("gregtech_nh", "GregTech GTNH", "gregtech.asm.GTCorePlugin", "gregtech.GTNHMod"),
    GT6("gregtech", "GregTech 6", "gregtech.asm.GT_ASM", "gregtech.GT6_Main"),
    IC2("IC2", "IndustrialCraft 2", "ic2.core.coremod.IC2core", "ic2.core.IC2"),
    NATURA("Natura", "Natura", null, "mods.natura.Natura"),
    TFC("terrafirmacraft", "TerraFirmaCraft", "com.bioxx.tfc.TFCASMLoadingPlugin", "com.bioxx.tfc.TerraFirmaCraft"),
    ;

    private val modBuilder by lazy {
        TargetModBuilder().setCoreModClass(coreModClass).setTargetClass(targetClass).setModId(modID)!!
    }

    private var isLoaded = false
    private var isChecked = false

    override fun isModLoaded(): Boolean {
        if (isChecked) return isLoaded
        isChecked = true

        // If a field is null it is ignored, otherwise it is checked and MUST be true.
        isLoaded = modID?.let { Loader.isModLoaded(it) } ?: true
        isLoaded = isLoaded && coreModClass?.let { getJavaClass(it) != null } ?: true
        isLoaded = isLoaded && targetClass?.let { getJavaClass(it) != null } ?: true

        return isLoaded
    }

    override fun getBuilder() = modBuilder
}

interface IMod {
    fun isModLoaded(): Boolean
}
