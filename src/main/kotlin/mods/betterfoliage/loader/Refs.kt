package mods.betterfoliage.loader

import mods.octarinecore.metaprog.ClassRef
import mods.octarinecore.metaprog.FieldRef
import mods.octarinecore.metaprog.MethodRef

/** Singleton object holding references to foreign code elements. */
object Refs {
    // Java
    val List = ClassRef("java.util.List")

    // Minecraft
    val IBlockAccess = ClassRef("net.minecraft.world.IBlockAccess", "ahl")

    val Block = ClassRef("net.minecraft.block.Block", "aji")

    val RenderBlocks = ClassRef("net.minecraft.client.renderer.RenderBlocks", "blm")

    val IIcon = ClassRef("net.minecraft.util.IIcon", "rf")

    // Shaders mod
    @Deprecated("ShadersMod support is deprecated")
    val Shaders = ClassRef("shadersmodcore.client.Shaders")

    @Deprecated("ShadersMod support is deprecated")
    val pushEntity =
        MethodRef(
            Shaders,
            "pushEntity",
            ClassRef.void,
            RenderBlocks,
            Block,
            ClassRef.int,
            ClassRef.int,
            ClassRef.int,
        )
    val pushEntity_I = MethodRef(Shaders, "pushEntity", ClassRef.void, ClassRef.int)
    val popEntity = MethodRef(Shaders, "popEntity", ClassRef.void)

    val ShadersModIntegration =
        ClassRef("mods.betterfoliage.client.integration.ShadersModIntegration")
    val getBlockIdOverride =
        MethodRef(ShadersModIntegration, "getBlockIdOverride", ClassRef.int, ClassRef.int, Block)

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

    // Colored Lights Core
    val CLCLoadingPlugin = ClassRef("coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin")
}
