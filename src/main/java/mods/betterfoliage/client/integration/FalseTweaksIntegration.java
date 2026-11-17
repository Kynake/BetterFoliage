package mods.betterfoliage.client.integration;

import com.falsepattern.falsetweaks.config.ModuleConfig;

public class FalseTweaksIntegration {

    public static boolean isAOFixEnabled() {
        return Mod.FALSETWEAKS.isLoaded() && ModuleConfig.aoFix;
    }

    public static boolean isAOFixDisabled() {
        return !isAOFixEnabled();
    }
}
