package mods.betterfoliage.client.integration

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder
import cpw.mods.fml.common.Loader
import mods.octarinecore.metaprog.getJavaClass

enum class Mod(val modID: String?, val modName: String, val coreModClass: String?, val targetClass: String?) : ITargetMod {
    EFR("etfuturum", "Et Futurum Requiem", "ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins", "ganymedes01.etfuturum.EtFuturum"),
    FORESTRY("forestry", "Forestry", null, "forestry.Forestry"),
    GT5U("gregtech_nh", "GregTech GTNH", "gregtech.asm.GTCorePlugin", "gregtech.GTNHMod"),
    GT6("gregtech", "GregTech 6", "gregtech.asm.GT_ASM", "gregtech.GT6_Main"),
    IC2("IC2", "IndustrialCraft 2", "ic2.core.coremod.IC2core", "ic2.core.IC2"),
    NATURA("Natura", "Natura", null, "mods.natura.Natura"),
    NOTFINE("notfine", "NotFine", "jss.notfine.mixinplugin", "jss.notfine.NotFine"),
    TFC("terrafirmacraft", "TerraFirmaCraft", "com.bioxx.tfc.TFCASMLoadingPlugin", "com.bioxx.tfc.TerraFirmaCraft"),
    TCON("TConstruct", "Tinker's Construct", null, "tconstruct.TConstruct"),
    ;

    private val modBuilder by lazy {
        TargetModBuilder().setCoreModClass(coreModClass).setTargetClass(targetClass).setModId(modID)!!
    }

    private var isLoadedCache = false
    private var isChecked = false

    val isLoaded: Boolean
        get() {
            if (isChecked) return isLoadedCache
            isChecked = true

            // If a field is null it is ignored, otherwise it is checked and MUST be true.
            isLoadedCache = modID?.let { Loader.isModLoaded(it) } ?: true
            isLoadedCache = isLoadedCache && coreModClass?.let { getJavaClass(it) != null } ?: true
            isLoadedCache = isLoadedCache && targetClass?.let { getJavaClass(it) != null } ?: true

            return isLoadedCache
        }

    override fun getBuilder() = modBuilder
}
