package mods.betterfoliage.client.integration;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

import cpw.mods.fml.common.Loader;

public enum Mod implements ITargetMod {

    // spotless:off
    ABYSSALCRAFT("abyssalcraft", "AbyssalCraft", null, "com.shinoow.abyssalcraft.AbyssalCraft"),
    EFR("etfuturum", "Et Futurum Requiem", "ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins", "ganymedes01.etfuturum.EtFuturum"),
    FORESTRY("forestry", "Forestry", null, "forestry.Forestry"),
    GT5U("gregtech_nh", "GregTech GTNH", "gregtech.asm.GTCorePlugin", "gregtech.GTNHMod"),
    GT6("gregtech", "GregTech 6", "gregtech.asm.GT_ASM", "gregtech.GT6_Main"),
    IC2("IC2", "IndustrialCraft 2", "ic2.core.coremod.IC2core", "ic2.core.IC2"),
    LOTR("lotr", "The Lord of the Rings Mod: Legacy", "lotr.common.coremod.LOTRLoadingPlugin", "lotr.common.LOTRMod"),
    NATURA("Natura", "Natura", null, "mods.natura.Natura"),
    NOTFINE("notfine", "NotFine", "jss.notfine.mixinplugin", "jss.notfine.NotFine"),
    TFC("terrafirmacraft", "TerraFirmaCraft", "com.bioxx.tfc.TFCASMLoadingPlugin", "com.bioxx.tfc.TerraFirmaCraft"),
    TCON("TConstruct", "Tinker's Construct", null, "tconstruct.TConstruct"),
    // spotless:on
    ;

    public final String modID;
    public final String modName;
    public final String coreModClass;
    public final String targetClass;

    private TargetModBuilder builder = null;

    private boolean isChecked = false;
    private boolean isLoadedCache = true;

    Mod(String modID, String modName, String coreModClass, String targetClass) {
        this.modID = modID;
        this.modName = modName;
        this.coreModClass = coreModClass;
        this.targetClass = targetClass;
    }

    public boolean isLoaded() {
        if (isChecked) {
            return isLoadedCache;
        }

        synchronized (this) {
            // If a field is null it is ignored, otherwise it is checked and MUST be true.
            if (modID != null) {
                isLoadedCache = Loader.isModLoaded(modID);
            }

            if (isLoadedCache && coreModClass != null) {
                try {
                    Class.forName(coreModClass);
                    isLoadedCache = true;
                } catch (Throwable t) {
                    isLoadedCache = false;
                }
            }

            if (isLoadedCache && targetClass != null) {
                try {
                    Class.forName(targetClass);
                    isLoadedCache = true;
                } catch (Throwable t) {
                    isLoadedCache = false;
                }
            }

            isChecked = true;
            return isLoadedCache;
        }
    }

    @Override
    public @NotNull TargetModBuilder getBuilder() {
        if (builder == null) {
            builder = new TargetModBuilder().setCoreModClass(coreModClass)
                .setTargetClass(targetClass)
                .setModId(modID);
        }

        return builder;
    }
}
