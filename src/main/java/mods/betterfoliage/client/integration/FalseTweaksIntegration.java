package mods.betterfoliage.client.integration;

import com.falsepattern.falsetweaks.config.ModuleConfig;

import mods.betterfoliage.utils.ClassUtils;

public class FalseTweaksIntegration {

    private static boolean isCached = false;
    private static boolean aoFixCache = false;

    public static boolean isAOFixEnabled() {
        if (isCached) return aoFixCache;

        // This runs too early to rely on Loader.isModLoaded(), so check the class directly
        aoFixCache = ClassUtils.isClassLoaded("com.falsepattern.falsetweaks.config.ModuleConfig") && ModuleConfig.aoFix;

        isCached = true;
        return aoFixCache;
    }

    public static boolean isAOFixDisabled() {
        return !isAOFixEnabled();
    }
}
