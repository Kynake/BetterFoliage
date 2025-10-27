package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureFluid;
import mods.betterfoliage.mixins.interfaces.gt6.IIconGetter;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureFluid.class)
public abstract class MixinBlockTextureFluid_IconGetter implements IIconGetter {

    @Shadow(remap = false)
    protected abstract IIcon getIcon(int side);

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        return getIcon(side);
    }
}
