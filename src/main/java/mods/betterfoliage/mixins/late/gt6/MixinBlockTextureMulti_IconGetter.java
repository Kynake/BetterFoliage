package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import mods.betterfoliage.mixins.interfaces.IGT6IconGetter;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureMulti.class)
public abstract class MixinBlockTextureMulti_IconGetter implements IGT6IconGetter {

    @Final
    @Shadow(remap = false)
    private ITexture[] mTextures;

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        if (mTextures == null || mTextures.length == 0) return null;

        IGT6IconGetter texture = (IGT6IconGetter) mTextures[0];
        return texture.betterfoliage$getIconForSide(side);
    }
}
