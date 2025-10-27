package mods.betterfoliage.mixins.late.gt6.accessors;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import mods.betterfoliage.mixins.interfaces.gt6.accessors.IIconAccessor;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureMulti.class)
public abstract class MixinBlockTextureMulti implements IIconAccessor {

    @Final
    @Shadow(remap = false)
    private ITexture[] mTextures;

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        if (mTextures == null || mTextures.length == 0) return null;

        IIconAccessor texture = (IIconAccessor) mTextures[0];
        return texture.betterfoliage$getIconForSide(side);
    }
}
