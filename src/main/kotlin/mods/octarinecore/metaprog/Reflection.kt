// TODO: Try to stop using reflection (hopefully all of it, otherwise just migrate this class to java)
@file:JvmName("Reflection")

package mods.octarinecore.metaprog

import mods.octarinecore.tryDefault

/** Get a Java class with the given name. */
fun getJavaClass(name: String) = tryDefault(null) { Class.forName(name) }

/** Get the field with the given name and type using reflection. */
inline fun <reified T> Any.reflectField(field: String): T? = tryDefault(null) { this.javaClass.getDeclaredField(field) }
    ?.let {
        it.isAccessible = true
        it.get(this) as T
    }

/** Get the static field with the given name and type using reflection. */
inline fun <reified T> Class<*>.reflectStaticField(field: String): T? = tryDefault(null) { this.getDeclaredField(field) }
    ?.let {
        it.isAccessible = true
        it.get(null) as T
    }

/**
 * Get all nested _object_s of this _object_ with reflection.
 *
 * @return [Pair]s of (name, instance)
 */
val Any.reflectNestedObjects: List<Pair<String, Any>>
    get() =
        this.javaClass.declaredClasses
            .map { tryDefault(null) { it.name.split("$")[1] to it.getField("INSTANCE").get(null) } }
            .filterNotNull()

/**
 * Get all fields of this instance that match (or subclass) any of the given classes.
 *
 * @param[types] classes to look for
 * @return [Pair]s of (field name, instance)
 */
fun Any.reflectFieldsOfType(vararg types: Class<*>) = this.javaClass.declaredFields
    .filter { field -> types.any { it.isAssignableFrom(field.type) } }.mapNotNull { field ->
        field.name to
            field.let {
                it.isAccessible = true
                it.get(this)
            }
    }
