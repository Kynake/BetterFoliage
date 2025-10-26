package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase06Covers;
import mods.betterfoliage.mixins.interfaces.IGT6TextureGetter;

@SuppressWarnings("UnusedMixin")
@Mixin(TileEntityBase06Covers.class)
public abstract class MixinTileEntityBase06Covers_TextureGetter implements IGT6TextureGetter {

    @Shadow(remap = false)
    public abstract ITexture getTexture(Block block, int renderPass, byte side, boolean[] sidesToRender);

    @Override
    @SideOnly(Side.CLIENT)
    public ITexture betterfoliage$getTexture(Block block, int renderPass, byte side, boolean[] sidesToRender) {
        return getTexture(block, renderPass, side, sidesToRender);
    }
}
