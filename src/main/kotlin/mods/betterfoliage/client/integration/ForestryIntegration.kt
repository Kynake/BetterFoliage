package mods.betterfoliage.client.integration

import mods.betterfoliage.client.Client
import mods.betterfoliage.client.texture.LeafRegistry.registerLeaf
import net.minecraftforge.client.event.TextureStitchEvent
import org.apache.logging.log4j.Level

object ForestryIntegration {
    fun registerLeafTextures(event: TextureStitchEvent.Pre) {
        if (!Mod.FORESTRY.isLoaded) return
        listOf("deciduous", "conifers", "jungle", "willow", "maple", "palm").forEach { leafType ->
            listOf("plain", "fancy", "changed").forEach { renderType ->
                val location = "${Mod.FORESTRY.modID}:leaves/$leafType.$renderType"
                val original = event.map.getTextureExtry(location)
                if (original != null) {
                    Client.log(Level.INFO, "Registering ${Mod.FORESTRY.modName} leaf texture: $location")
                    registerLeaf(event.map, original)
                }
            }
        }
    }
}
