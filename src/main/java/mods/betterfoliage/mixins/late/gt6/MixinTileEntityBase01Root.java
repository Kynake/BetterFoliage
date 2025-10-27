package mods.betterfoliage.mixins.late.gt6;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import gregapi.tileentity.base.TileEntityBase01Root;
import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(TileEntityBase01Root.class)
public abstract class MixinTileEntityBase01Root {

    @WrapMethod(method = "shouldSideBeRendered", remap = false)
    private boolean betterfoliage$shouldSideBeRendered(byte side, Operation<Boolean> original) {
        TileEntity te = ((TileEntity) (Object) this);
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        return Hooks.overrideIsPartialBlock(
            original.call(side),
            te.getWorldObj(),
            te.xCoord + direction.offsetX,
            te.yCoord + direction.offsetY,
            te.zCoord + direction.offsetZ);
    }
}
