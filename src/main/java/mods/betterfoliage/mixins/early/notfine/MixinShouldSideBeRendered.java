package mods.betterfoliage.mixins.early.notfine;

import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import mods.betterfoliage.client.Hooks;

@SuppressWarnings("UnusedMixin")
@Mixin(
    priority = 1500,
    // spotless:off
    value = {
        BlockCactus.class,
        BlockCarpet.class,
        BlockEnchantmentTable.class,
        BlockFarmland.class,
        BlockSlab.class,
        BlockSnow.class,
    })
    // spotless:on
public abstract class MixinShouldSideBeRendered {

    // What: Invoke BF code to overrule render condition when NotFine / Angelica is present
    // Why: NotFine / Angelica overrides the default shouldRenderSide() method with custom versions
    // for specific blocks. This inhibits the default mixin that fixes
    // rendering for blocks next to rounded logs, resulting in holes in the world.
    @Dynamic("Added dynamically by NotFine / Angelica via Mixin")
    @ModifyReturnValue(method = { "shouldSideBeRendered", "func_149646_a" }, at = @At("RETURN"), remap = false)
    private boolean betterfoliage$shouldSideBeRenderedNotFineOverride(boolean original, IBlockAccess world, int x,
        int y, int z, int side) {
        return Hooks.overrideIsPartialBlock(original, world, x, y, z);
    }
}
