package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase06Covers;
import mods.betterfoliage.mixins.interfaces.gt6.ITexture2Getter;

@SuppressWarnings("UnusedMixin")
@Mixin(TileEntityBase06Covers.class)
public abstract class MixinTileEntityBase06Covers_Texture2Getter implements ITexture2Getter {

    @Shadow(remap = false)
    public abstract ITexture getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender);

    @Override
    @SideOnly(Side.CLIENT)
    public ITexture betterfoliage$getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender) {
        return getTexture2(block, renderPass, side, sidesToRender);
    }
}
