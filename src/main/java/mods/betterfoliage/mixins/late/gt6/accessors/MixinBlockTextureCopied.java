package mods.betterfoliage.mixins.late.gt6.accessors;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureCopied;
import mods.betterfoliage.mixins.interfaces.gt6.accessors.IIconAccessor;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureCopied.class)
public abstract class MixinBlockTextureCopied implements IIconAccessor {

    @Shadow(remap = false)
    protected abstract IIcon getIcon(int side);

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        return getIcon(side);
    }
}
