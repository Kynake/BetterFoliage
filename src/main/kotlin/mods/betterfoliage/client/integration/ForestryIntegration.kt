package mods.betterfoliage.client.integration

import mods.betterfoliage.client.Client
import mods.betterfoliage.client.texture.LeafRegistry.registerLeaf
import net.minecraftforge.client.event.TextureStitchEvent
import org.apache.logging.log4j.Level

object ForestryIntegration {
    fun registerLeafTextures(event: TextureStitchEvent.Pre) {
        if (!CompatibleMod.FORESTRY.isModLoaded()) return
        listOf("deciduous", "conifers", "jungle", "willow", "maple", "palm").forEach { leafType ->
            listOf("plain", "fancy", "changed").forEach { renderType ->
                val location = "${CompatibleMod.FORESTRY.modID}:leaves/$leafType.$renderType"
                val original = event.map.getTextureExtry(location)
                if (original != null) {
                    Client.log(Level.INFO, "Registering ${CompatibleMod.FORESTRY.modName} leaf texture: $location")
                    registerLeaf(event.map, original)
                }
            }
        }
    }
}
