package mods.betterfoliage.client.render;

import mods.betterfoliage.utils.MathUtils;
import net.minecraft.client.renderer.Tessellator;

public final class ShadingInfo {

    private float r;
    private float g;
    private float b;

    private int pureColor;
    private int brightness;

    private float ambientOcclusion;

    public ShadingInfo() {
        updateValues(0xFF, 0xFF, 1f);
    }

    public ShadingInfo(final int brightness, final int color, final float ao) {
        updateValues(brightness, color, ao);
    }

    /// Apply without updating
    private static void applyShading(final Tessellator tess, final int brightness, final int color, final float ao) {
        final float r = ao * (float) (color >> 16 & 0xFF) / 255.0f;
        final float g = ao * (float) (color >> 8 & 0xFF) / 255.0f;
        final float b = ao * (float) (color & 0xFF) / 255.0f;

        tess.setColorOpaque_F(r, g, b);
        tess.setBrightness(brightness);
    }


    public void applyShading() {
        applyShading(Tessellator.instance);
    }

    public void applyShading(final Tessellator tess) {
        tess.setColorOpaque_F(r, g, b);
        tess.setBrightness(brightness);
    }

    public static void applyFlatDiagonalShading(final Tessellator tess, final ShadingInfo clock, final ShadingInfo counter,
        final ShadingInfo front, final ShadingInfo back) {

        final float avgAO = MathUtils.lerp(clock.ambientOcclusion, counter.ambientOcclusion, 0.5f);

        int maxBrightness = clock.brightness;
        if (counter.brightness > maxBrightness) {
            maxBrightness = counter.brightness;
        }
        if (front.brightness > maxBrightness) {
            maxBrightness = front.brightness;
        }
        if (back.brightness > maxBrightness) {
            maxBrightness = back.brightness;
        }

        int avgColor = clock.pureColor;
        if (avgColor != counter.pureColor) {
            avgColor = RenderUtils.lerpARGB(clock.pureColor, counter.pureColor, 0.5f);
        }

        applyShading(tess, maxBrightness, avgColor, avgAO);
    }

    public void applyAveragedShading(final Tessellator tess, final ShadingInfo other, float ratio) {
        final float avgAO = MathUtils.lerp(ambientOcclusion, other.ambientOcclusion, ratio);

        int avgColor = pureColor;
        if (avgColor != other.pureColor) {
            avgColor = RenderUtils.lerpARGB(pureColor, other.pureColor, ratio);
        }

        applyShading(tess, brightness, avgColor, avgAO);
    }

    /// Update internal values
    public void updateAverageShadingInfo(final ShadingInfo other, final float ratio) {
        ambientOcclusion = MathUtils.lerp(ambientOcclusion, other.ambientOcclusion, ratio);

        if (pureColor != other.pureColor) {
            pureColor = RenderUtils.lerpARGB(pureColor, other.pureColor, ratio);
        }

        setRGB(pureColor);
    }

    public void updateValues(final int brightness, final int color, final float ao) {
        this.ambientOcclusion = ao;
        this.brightness = brightness;
        setRGB(color);
    }

    private void setRGB(final int color) {
        pureColor = color;
        r = ambientOcclusion * (float) (color >> 16 & 0xFF) / 255.0f;
        g = ambientOcclusion * (float) (color >> 8 & 0xFF) / 255.0f;
        b = ambientOcclusion * (float) (color & 0xFF) / 255.0f;
    }
}
