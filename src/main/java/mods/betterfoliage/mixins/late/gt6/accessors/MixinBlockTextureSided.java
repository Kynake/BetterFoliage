package mods.betterfoliage.mixins.late.gt6.accessors;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.BlockTextureSided;
import gregapi.render.IIconContainer;
import mods.betterfoliage.mixins.interfaces.gt6.accessors.IIconAccessor;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockTextureSided.class)
public abstract class MixinBlockTextureSided implements IIconAccessor {

    @Final
    @Shadow(remap = false)
    private IIconContainer[] mIconContainers;

    @Unique
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterfoliage$getIconForSide(int side) {
        if (mIconContainers == null || mIconContainers.length == 0 || side >= mIconContainers.length) return null;
        IIconContainer container = mIconContainers[side];
        if (container == null || container.getIconPasses() <= 0) return null;
        return container.getIcon(0);
    }
}
