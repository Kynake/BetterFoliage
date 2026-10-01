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

    public void applyShading() {
        applyShading(Tessellator.instance);
    }

    public void applyShading(final Tessellator tess) {
        tess.setBrightness(brightness);
        tess.setColorOpaque_F(r, g, b);
    }

    public void applyAveragedShading(final Tessellator tess, final ShadingInfo other, final float ratio) {
        final float avgAO = MathUtils.lerp(ambientOcclusion, other.ambientOcclusion, ratio);

        int avgBrightness = brightness;
        if (brightness != other.brightness) {
            avgBrightness = Math.round(MathUtils.lerp((float) brightness, (float) other.brightness, ratio));
        }

        int avgColor = pureColor;
        if (pureColor != other.pureColor) {
            avgColor = RenderUtils.lerpARGB(pureColor, other.pureColor, ratio);
        }

        final float avgR = avgAO * (float) (avgColor >> 16 & 0xFF) / 255.0f;
        final float avgG = avgAO * (float) (avgColor >> 8 & 0xFF) / 255.0f;
        final float avgB = avgAO * (float) (avgColor & 0xFF) / 255.0f;

        tess.setBrightness(avgBrightness);
        tess.setColorOpaque_F(avgR, avgG, avgB);
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
