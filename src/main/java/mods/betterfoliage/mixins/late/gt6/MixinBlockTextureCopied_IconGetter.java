package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureCopied;
import mods.betterfoliage.mixins.interfaces.IGT6IconGetter;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureCopied.class)
public abstract class MixinBlockTextureCopied_IconGetter implements IGT6IconGetter {

    @Shadow(remap = false)
    protected abstract IIcon getIcon(int side);

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        return getIcon(side);
    }
}
