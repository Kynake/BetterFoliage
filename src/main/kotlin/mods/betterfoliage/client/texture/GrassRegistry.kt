package mods.betterfoliage.client.texture

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.betterfoliage.client.integration.OptifineCTM
import mods.octarinecore.client.render.HSB
import mods.octarinecore.client.resource.averageColor
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import org.apache.logging.log4j.Level.DEBUG
import org.apache.logging.log4j.Level.INFO

const val DEFAULT_GRASS_COLOR = 0

/** Rendering-related information for a grass block. */
class GrassInfo(
    /** Top texture of the grass block. */
    val grassTopTexture: TextureAtlasSprite,

    /**
     * Color to use for Short Grass rendering instead of the biome color.
     *
     * Value is null if the texture is mostly grey (the saturation of its average color is under a
     * configurable limit), the average color of the texture (significantly brightened) otherwise.
     */
    val overrideColor: Int?,
)

/** Collects and manages rendering-related information for grass blocks. */
@SideOnly(Side.CLIENT)
object GrassRegistry {

    val grass: MutableMap<IIcon, GrassInfo> = hashMapOf()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun handleTextureReload(event: TextureStitchEvent.Pre) {
        if (event.map.textureType != 0) return
        grass.clear()
        Client.log(INFO, "Inspecting grass textures")

        Block.blockRegistry.forEach { block ->
            if (Config.blocks.grass.matchesClass(block as Block)) {
                block.registerBlockIcons { location ->
                    val original = event.map.getTextureExtry(location)
                    Client.log(DEBUG, "Found grass texture: $location")
                    registerGrass(event.map, original)

                    if (OptifineCTM.isAvailable) {
                        OptifineCTM.getAllCTM(original).let { ctmIcons ->
                            if (ctmIcons.isNotEmpty()) {
                                Client.log(INFO, "Found ${ctmIcons.size} CTM variants for texture ${original.iconName}")
                                ctmIcons.forEach { registerGrass(event.map, it as TextureAtlasSprite) }
                            }
                        }
                    }

                    return@registerBlockIcons original
                }

                if (OptifineCTM.isAvailable) {
                    OptifineCTM.getAllCTM(block).let { ctmIcons ->
                        if (ctmIcons.isNotEmpty()) {
                            Client.log(
                                INFO,
                                "Found ${ctmIcons.size} CTM variants for block ${Block.getIdFromBlock(block)}",
                            )
                            ctmIcons.forEach { registerGrass(event.map, it as TextureAtlasSprite) }
                        }
                    }
                }
            }
        }
    }

    fun registerGrass(atlas: TextureMap, icon: TextureAtlasSprite) {
        val hsb = HSB.fromColor(icon.averageColor ?: DEFAULT_GRASS_COLOR)
        val overrideColor =
            if (hsb.saturation > Config.shortGrass.saturationThreshold) {
                hsb.copy(brightness = 0.8f).asColor
            } else {
                null
            }
        grass.put(icon, GrassInfo(icon, overrideColor))
    }
}
