package mods.betterfoliage.utils;

import javax.annotation.Nonnull;

public class ClassUtils {

    public static boolean isClassLoaded(@Nonnull String targetClass) {
        try {
            Class.forName(targetClass);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
