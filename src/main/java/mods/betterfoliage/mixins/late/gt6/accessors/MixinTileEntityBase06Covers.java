package mods.betterfoliage.mixins.late.gt6.accessors;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase06Covers;
import mods.betterfoliage.mixins.interfaces.gt6.accessors.ITexture2Accessor;

@SuppressWarnings("UnusedMixin")
@Mixin(TileEntityBase06Covers.class)
public abstract class MixinTileEntityBase06Covers implements ITexture2Accessor {

    @Shadow(remap = false)
    public abstract ITexture getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender);

    @Override
    @SideOnly(Side.CLIENT)
    public ITexture betterfoliage$getTexture2(Block block, int renderPass, byte side, boolean[] sidesToRender) {
        return getTexture2(block, renderPass, side, sidesToRender);
    }
}
