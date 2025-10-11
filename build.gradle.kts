
plugins {
    id("com.gtnewhorizons.gtnhconvention")
    kotlin("jvm")
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
kotlin {
    jvmToolchain(8)
}
