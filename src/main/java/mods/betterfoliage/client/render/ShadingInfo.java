package mods.betterfoliage.client.render;

import mods.betterfoliage.utils.MathUtils;
import net.minecraft.client.renderer.Tessellator;

public class ShadingInfo {

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
        final Tessellator tess = Tessellator.instance;
        tess.setBrightness(brightness);
        tess.setColorOpaque_F(r, g, b);
    }

    public void applyAveragedShading(ShadingInfo other, float ratio) {
        final float avgAO = MathUtils.lerp(ambientOcclusion, other.ambientOcclusion, ratio);
        final int avgBrightness = Math.round(MathUtils.lerp((float) brightness, (float) other.brightness, ratio));

        int avgColor = RenderUtils.blendRGB(pureColor, other.pureColor, ratio);
        avgColor = RenderUtils.multiplyAlphas(avgColor, other.pureColor);

        final float avgR = avgAO * (float) (avgColor >> 16 & 0xFF) / 255.0f;
        final float avgG = avgAO * (float) (avgColor >> 8 & 0xFF) / 255.0f;
        final float avgB = avgAO * (float) (avgColor & 0xFF) / 255.0f;

        final Tessellator tess = Tessellator.instance;
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
