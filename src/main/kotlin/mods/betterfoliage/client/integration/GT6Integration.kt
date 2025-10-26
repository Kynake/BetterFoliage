package mods.betterfoliage.client.integration

import gregapi.block.multitileentity.MultiTileEntityBlock
import gregapi.tileentity.misc.MultiTileEntityTreeHole
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.texture.LeafRegistry.registerLeaf
import mods.betterfoliage.mixins.interfaces.IGT6TreeHoleMTE
import mods.octarinecore.client.render.BlockContext
import mods.octarinecore.client.render.Int3
import net.minecraft.util.IIcon
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.util.ForgeDirection
import org.apache.logging.log4j.Level

object GT6Integration {

    fun registerLeafTextures(event: TextureStitchEvent.Pre) {
        if (!Mod.GT6.isLoaded) return
        listOf("BLUEMAHOE", "BLUESPRUCE", "BLUESPRUCE_XMAS", "CINNAMON", "COCONUT", "HAZEL", "MAPLE", "MAPLE_BROWN", "MAPLE_ORANGE", "MAPLE_RED", "MAPLE_YELLOW", "RAINBOWOOD", "RUBBER", "WILLOW").forEach { leafType ->
            listOf("", "OPAQUE_").forEach { renderTypePrefix ->
                val location = "${Mod.GT6.modID}:iconsets/LEAVES_$renderTypePrefix$leafType"
                val original = event.map.getTextureExtry(location)
                if (original != null) {
                    Client.log(Level.INFO, "Registering ${Mod.GT6.modName} leaf texture: $location")
                    registerLeaf(event.map, original)
                }
            }
        }
    }

    fun allowSpecialGT6LogRender(ctx: BlockContext): Boolean {
        if (!Mod.GT6.isLoaded) return true
        if (ctx.block !is MultiTileEntityBlock) return true

        // Is MTE, but not a log block (we don't want to make round a normal GT6 machine, after all)
        if (ctx.tileEntity !is MultiTileEntityTreeHole) return false

        return true
    }

    fun getGT6LogMTEIcon(ctx: BlockContext, face: ForgeDirection, offset: Int3): IIcon? {
        if (!Mod.GT6.isLoaded) return null
        if (ctx.block(offset) !is MultiTileEntityBlock) return null

        val treeHole = ctx.tileEntity(offset)
        if (treeHole !is IGT6TreeHoleMTE) return null

        return treeHole.`betterFoliage$getTextureForSide`(face)
    }
}
