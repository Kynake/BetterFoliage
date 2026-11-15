package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import mods.betterfoliage.client.render.ShadingInfo;
import mods.betterfoliage.mixins.interfaces.minecraft.IRoundLogRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_RoundLogs implements IRoundLogRenderer {

    @Unique
    private ShadingInfo[] betterfoliage$roundContainers;

    @Override
    public ShadingInfo[] betterfoliage$getShadingContainers() {
        if (betterfoliage$roundContainers == null) {
            betterfoliage$roundContainers = new ShadingInfo[8];

            // One for each face of a log corner
            betterfoliage$roundContainers[0] = new ShadingInfo(); // Front
            betterfoliage$roundContainers[1] = new ShadingInfo(); // Back
            betterfoliage$roundContainers[2] = new ShadingInfo(); // Clockwise
            betterfoliage$roundContainers[3] = new ShadingInfo(); // Counterclockwise

            // Extra buffers for diagonals
            betterfoliage$roundContainers[4] = new ShadingInfo();
            betterfoliage$roundContainers[5] = new ShadingInfo();
            betterfoliage$roundContainers[6] = new ShadingInfo();
            betterfoliage$roundContainers[7] = new ShadingInfo();
        }

        return betterfoliage$roundContainers;
    }
}
