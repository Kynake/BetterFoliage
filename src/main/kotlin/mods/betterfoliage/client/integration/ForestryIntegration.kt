package mods.betterfoliage.client.integration

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraftforge.client.event.TextureStitchEvent

@SideOnly(Side.CLIENT)
object ForestryIntegration {
    fun registerLeafTextures(event: TextureStitchEvent.Pre) {
        // TODO reimplement this fix
//        if (!Mod.FORESTRY.isLoaded) return
//        listOf("deciduous", "conifers", "jungle", "willow", "maple", "palm").forEach { leafType ->
//            listOf("plain", "fancy", "changed").forEach { renderType ->
//                val location = "${Mod.FORESTRY.modID}:leaves/$leafType.$renderType"
//                val original = event.map.getTextureExtry(location)
//                if (original != null) {
//                    Client.log(Level.INFO, "Registering ${Mod.FORESTRY.modName} leaf texture: $location")
//                    registerLeaf(event.map, original)
//                }
//            }
//        }
    }
}
