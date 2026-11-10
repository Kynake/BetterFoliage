package mods.betterfoliage.mixins.late.tfc;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;

import com.bioxx.tfc.Blocks.Flora.BlockLogHoriz2;
import com.bioxx.tfc.Blocks.Flora.BlockLogNatural;
import com.bioxx.tfc.Blocks.Flora.BlockLogNatural2;
import com.bioxx.tfc.api.TFCBlocks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.client.integration.TFCIntegration;
import mods.betterfoliage.config.Configs;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockLogHoriz2.class)
public abstract class MixinBlockLogHoriz2 extends MixinBlockLogHoriz {

    @WrapMethod(method = "getIcon")
    private IIcon betterfoliage$unrotateHorizontalLogSprites(int side, int meta, Operation<IIcon> original) {
        if (!Configs.RoundLogs.enabled) {
            return original.call(side, meta);
        }

        final BlockLogNatural logs = (BlockLogNatural2) TFCBlocks.logNatural2;
        int index = (meta & 7) + offset;
        if (index >= logs.sideIcons.length) {
            index = 0;
        }

        return TFCIntegration.unrotateHorizontalLogSprites(side, meta, index, logs.sideIcons, logs.innerIcons);
    }
}
