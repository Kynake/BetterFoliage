package mods.octarinecore.client.resource

import cpw.mods.fml.client.event.ConfigChangedEvent
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import mods.octarinecore.client.render.Model
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent

// ============================
// Resource types
// ============================

// Something register textures for fetching IIcons
interface IStitchListener {
    fun onStitch(atlas: IIconRegister)
}

// Generate models variants vectors etc
interface IConfigChangeListener {
    fun onConfigChange()
}

// Gets world seed for SimplexNoise
interface IWorldLoadListener {
    fun onWorldLoad(world: World)
}

/**
 * Base class for declarative resource handling.
 *
 * Resources are automatically reloaded/recalculated when the appropriate events are fired.
 *
 * @param[modId] mod ID associated with this handler (used to filter config change events)
 */
open class ResourceHandler(val modId: String) {

    val resources = mutableListOf<Any>()
    open fun afterStitch() {}

    // ============================
    // Self-registration
    // ============================
    init {
        MinecraftForge.EVENT_BUS.register(this)
        FMLCommonHandler.instance().bus().register(this)
    }

    // ============================
    // Resource declarations
    // ============================
    fun model(init: Model.() -> Unit) = ModelHolder(init).apply { resources.add(this) }

    // ============================
    // Event registration
    // ============================
    @SubscribeEvent
    fun onStitch(event: TextureStitchEvent.Pre) {
        if (event.map.textureType == 0) {
            resources.forEach { (it as? IStitchListener)?.onStitch(event.map) }
            afterStitch()
        }
    }

    @SubscribeEvent
    fun handleConfigChange(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID == modId) resources.forEach { (it as? IConfigChangeListener)?.onConfigChange() }
    }

    @SubscribeEvent
    fun handleWorldLoad(event: WorldEvent.Load) = resources.forEach { (it as? IWorldLoadListener)?.onWorldLoad(event.world) }
}

// ============================
// Resource container classes
// ============================
class ModelHolder(val init: Model.() -> Unit) : IConfigChangeListener {
    var model: Model = Model().apply(init)
    override fun onConfigChange() {
        model = Model().apply(init)
    }
}
