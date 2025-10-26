package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureDefault;
import gregapi.render.IIconContainer;
import mods.betterfoliage.mixins.interfaces.IGT6IconGetter;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureDefault.class)
public abstract class MixinBlockTextureDefault_IconGetter implements IGT6IconGetter {

    @Final
    @Shadow(remap = false)
    private IIconContainer mIconContainer;

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        if (mIconContainer == null || mIconContainer.getIconPasses() <= 0) return null;
        return mIconContainer.getIcon(side);
    }
}
