package mods.betterfoliage.loader

import mods.octarinecore.metaprog.ClassRef
import mods.octarinecore.metaprog.FieldRef
import mods.octarinecore.metaprog.MethodRef

// TODO Remove this class once CTM support is refactored, as it all relates to it in some way
/** Singleton object holding references to foreign code elements. */
object Refs {
    // Minecraft
    val IBlockAccess = ClassRef("net.minecraft.world.IBlockAccess", "ahl")

    val Block = ClassRef("net.minecraft.block.Block", "aji")

    val IIcon = ClassRef("net.minecraft.util.IIcon", "rf")

    // Optifine
    val ConnectedTextures = ClassRef("ConnectedTextures")
    val getConnectedTexture =
        MethodRef(
            ConnectedTextures,
            "getConnectedTexture",
            IIcon,
            IBlockAccess,
            Block,
            ClassRef.int,
            ClassRef.int,
            ClassRef.int,
            ClassRef.int,
            IIcon,
        )
    val CTblockProperties = FieldRef(ConnectedTextures, "blockProperties", null)
    val CTtileProperties = FieldRef(ConnectedTextures, "tileProperties", null)

    val ConnectedProperties = ClassRef("ConnectedProperties")
    val CPmatchBlocks = FieldRef(ConnectedProperties, "matchBlocks", null)
    val CPmatchTileIcons = FieldRef(ConnectedProperties, "matchTileIcons", null)
    val CPtileIcons = FieldRef(ConnectedProperties, "tileIcons", null)
}
