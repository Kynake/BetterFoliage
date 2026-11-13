package mods.betterfoliage.mixins.late.tfc;

import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.bioxx.tfc.Blocks.Flora.BlockLogHoriz;
import com.bioxx.tfc.Blocks.Flora.BlockLogNatural;
import com.bioxx.tfc.api.TFCBlocks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mods.betterfoliage.client.integration.TFCIntegration;
import mods.betterfoliage.config.Configs;

@SuppressWarnings("UnusedMixin")
@Mixin(BlockLogHoriz.class)
public abstract class MixinBlockLogHoriz {

    @Shadow(remap = false)
    protected int offset;

    @WrapMethod(method = "getIcon", require = 1)
    private IIcon betterfoliage$unrotateHorizontalLogSprites(int side, int meta, Operation<IIcon> original) {
        if (!Configs.RoundLogs.enabled) {
            return original.call(side, meta);
        }

        final BlockLogNatural logs = (BlockLogNatural) TFCBlocks.logNatural;
        final int index = (meta & 7) + offset;

        return TFCIntegration.unrotateHorizontalLogSprites(side, meta, index, logs.sideIcons, logs.innerIcons);
    }
}
