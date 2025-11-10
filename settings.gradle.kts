
pluginManagement {
    repositories {
        maven {
            // RetroFuturaGradle
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
            mavenContent {
                includeGroup("com.gtnewhorizons")
                includeGroupByRegex("com\\.gtnewhorizons\\..+")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    plugins {
        kotlin("jvm") version "2.2.21"
    }
}

plugins {
    id("com.gtnewhorizons.gtnhsettingsconvention") version("1.0.48")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
