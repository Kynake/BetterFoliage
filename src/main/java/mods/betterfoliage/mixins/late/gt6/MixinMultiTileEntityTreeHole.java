package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.render.ITexture;
import gregapi.tileentity.misc.MultiTileEntityTreeHole;
import mods.betterfoliage.mixins.interfaces.IGT6IconGetter;
import mods.betterfoliage.mixins.interfaces.IGT6TextureGetter;
import mods.betterfoliage.mixins.interfaces.IGT6TreeHoleMTE;

@SuppressWarnings("UnusedMixin")
@Mixin(MultiTileEntityTreeHole.class)
public abstract class MixinMultiTileEntityTreeHole implements IGT6TreeHoleMTE {

    @Unique
    private static final boolean[] betterfoliage$allSidesRender = { true, true, true, true, true, true };

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon betterFoliage$getTextureForSide(ForgeDirection side) {
        Block block = ((TileEntity) (Object) this).getBlockType();
        ITexture texture = ((IGT6TextureGetter) this)
            .betterfoliage$getTexture(block, 0, (byte) side.ordinal(), betterfoliage$allSidesRender);
        return ((IGT6IconGetter) texture).betterfoliage$getIconForSide(side.ordinal());
    }
}
