
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

minecraft {
    injectedTags.put("MOD_ID", project.property("modId")!!)
    injectedTags.put("MOD_NAME", project.property("modName")!!)
    injectedTags.put("MOD_DOMAIN", (project.property("modId")!! as String).lowercase())
    injectedTags.put("MOD_LEGACY_DOMAIN", project.property("modLegacyDomain")!!)
}
