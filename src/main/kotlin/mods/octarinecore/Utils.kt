@file:JvmName("Utils")
@file:Suppress("NOTHING_TO_INLINE")

package mods.octarinecore

/**
 * Call the supplied lambda and return its result, or the given default value if an exception is
 * thrown.
 */
fun <T> tryDefault(default: T, work: () -> T) = try {
    work()
} catch (e: Throwable) {
    default
}
