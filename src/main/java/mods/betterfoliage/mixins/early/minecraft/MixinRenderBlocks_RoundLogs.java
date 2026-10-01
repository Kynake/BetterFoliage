package mods.betterfoliage.mixins.early.minecraft;

import mods.betterfoliage.client.render.ShadingInfo;
import mods.betterfoliage.mixins.interfaces.minecraft.IRoundLogRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public class MixinRenderBlocks_RoundLogs implements IRoundLogRenderer {

    @Unique
    private ShadingInfo[] betterfoliage$roundContainers;

    @Override
    public ShadingInfo[] betterfoliage$getShadingContainers() {
        if (betterfoliage$roundContainers == null) {
            betterfoliage$roundContainers = new ShadingInfo[2];
            betterfoliage$roundContainers[0] = new ShadingInfo();
            betterfoliage$roundContainers[1] = new ShadingInfo();
        }

        return betterfoliage$roundContainers;
    }
}
