@file:JvmName("Utils")

package mods.octarinecore.client.resource

import mods.octarinecore.tryDefault
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

/** Concise getter for the Minecraft resource manager. */
val resourceManager: SimpleReloadableResourceManager
    get() = Minecraft.getMinecraft().resourceManager as SimpleReloadableResourceManager

/** Append a string to the [ResourceLocation]'s path. */
operator fun ResourceLocation.plus(str: String) = ResourceLocation(resourceDomain, resourcePath + str)

/** Index operator to get a resource. */
operator fun IResourceManager.get(domain: String, path: String): IResource? = get(ResourceLocation(domain, path))

/** Index operator to get a resource. */
operator fun IResourceManager.get(location: ResourceLocation): IResource? = tryDefault(null) { getResource(location) }

/** Get the lines of a text resource. */
fun IResource.getLines(): List<String> {
    val result = arrayListOf<String>()
    inputStream.bufferedReader().useLines { it.forEach { result.add(it) } }
    return result
}

/** Index operator to get the RGB value of a pixel. */
operator fun BufferedImage.get(x: Int, y: Int) = this.getRGB(x, y)

/** Index operator to set the RGB value of a pixel. */
operator fun BufferedImage.set(x: Int, y: Int, value: Int) = this.setRGB(x, y, value)
