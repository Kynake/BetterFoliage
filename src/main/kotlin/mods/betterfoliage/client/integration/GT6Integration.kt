package mods.betterfoliage.client.integration

import mods.betterfoliage.client.Client
import mods.betterfoliage.client.texture.LeafRegistry.registerLeaf
import net.minecraftforge.client.event.TextureStitchEvent
import org.apache.logging.log4j.Level

object GT6Integration {

    fun registerLeafTextures(event: TextureStitchEvent.Pre) {
        if (!CompatibleMod.GT6.isModLoaded()) return
        listOf("BLUEMAHOE", "BLUESPRUCE", "BLUESPRUCE_XMAS", "CINNAMON", "COCONUT", "HAZEL", "MAPLE", "MAPLE_BROWN", "MAPLE_ORANGE", "MAPLE_RED", "MAPLE_YELLOW", "RAINBOWOOD", "RUBBER", "WILLOW").forEach { leafType ->
            listOf("", "OPAQUE_").forEach { renderTypePrefix ->
                val location = "${CompatibleMod.GT6.modID}:iconsets/LEAVES_$renderTypePrefix$leafType"
                val original = event.map.getTextureExtry(location)
                if (original != null) {
                    Client.log(Level.INFO, "Registering ${CompatibleMod.GT6.modName} leaf texture: $location")
                    registerLeaf(event.map, original)
                }
            }
        }
    }
}
