package mods.betterfoliage.mixins.late.falsetweaks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.falsepattern.falsetweaks.modules.ao.AmbientOcclusionRenderer;
import com.falsepattern.falsetweaks.modules.triangulator.renderblocks.Facing;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.features.LogRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSideSpritesRenderer;
import mods.betterfoliage.mixins.interfaces.minecraft.IGrassBlockRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(AmbientOcclusionRenderer.class)
public abstract class MixinAmbientOcclusionRenderer implements IGrassBlockRenderer, ICustomSideSpritesRenderer {

    @Unique
    private static final int betterfoliage$noAOFloatBits = Float.floatToRawIntBits(1f);

    @Unique
    private ISpriteProvider betterfoliage$customSpriteProvider = null;

    @Unique
    private boolean betterfoliage$isRenderingGrass = false;

    @Override
    public void betterfoliage$setSpriteProvider(ISpriteProvider provider) {
        betterfoliage$customSpriteProvider = provider;
    }

    @Override
    public void betterfoliage$resetSpriteProvider() {
        betterfoliage$customSpriteProvider = null;
    }

    @Override
    public void betterfoliage$setGrassRender(boolean isRenderingGrass) {
        betterfoliage$isRenderingGrass = isRenderingGrass;
    }

    /// Custom Sprite
    @ModifyVariable(
        method = "doRenderFace",
        name = "icon",
        argsOnly = true,
        at = @At("HEAD"),
        remap = false,
        require = 1)
    private IIcon betterfoliage$overrideQuadSprite(IIcon original, RenderBlocks rb, Block block, int x, int y, int z,
        Facing facing) {
        if (betterfoliage$customSpriteProvider == null) {
            return original;
        }

        return betterfoliage$customSpriteProvider.getSpriteForCoord(x, y, z, facing.face.ordinal());
    }

    /// Grass Renderer
    @ModifyVariable(method = "dispatchRender", name = "icon", at = @At("STORE"), remap = false, require = 1)
    private IIcon betterfoliage$overrideGrassSide(IIcon original, RenderBlocks rb, Facing facing, Block block, int x,
        int y, int z, boolean useColor, float r, float g, float b) {
        if (betterfoliage$isRenderingGrass && facing.face != Facing.Direction.FACE_YNEG
            && facing.face != Facing.Direction.FACE_YPOS) {
            rb.colorRedTopLeft *= r;
            rb.colorGreenTopLeft *= g;
            rb.colorBlueTopLeft *= b;

            rb.colorRedBottomLeft *= r;
            rb.colorGreenBottomLeft *= g;
            rb.colorBlueBottomLeft *= b;

            rb.colorRedBottomRight *= r;
            rb.colorGreenBottomRight *= g;
            rb.colorBlueBottomRight *= b;

            rb.colorRedTopRight *= r;
            rb.colorGreenTopRight *= g;
            rb.colorBlueTopRight *= b;
        }

        return original;
    }

    /// Log Shadowing Fix
    @ModifyExpressionValue(
        method = "fetchLightingData",
        at = @At(value = "INVOKE", target = "Ljava/lang/Float;floatToRawIntBits(F)I"),
        remap = false,
        require = 1)
    private static int betterfoliage$overrideNeighborAO(int original, @Local(name = "world") IBlockAccess world,
        @Local(name = "xNear") int xNear, @Local(name = "yNear") int yNear, @Local(name = "zNear") int zNear,
        @Local(name = "xOff") int x, @Local(name = "yOff") int y, @Local(name = "zOff") int z) {
        if ((xNear != 1 || yNear != 1 || zNear != 1) && LogRenderer.isRoundLog(world, x, y, z)) {
            // Round logs should cast no AO into their neighbors
            return betterfoliage$noAOFloatBits;
        }

        return original;
    }
}
