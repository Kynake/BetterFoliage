package mods.betterfoliage.client.render;

import net.minecraft.client.renderer.Tessellator;

import mods.betterfoliage.utils.MathUtils;

public final class ShadingInfo {

    private static final int BRIGHTNESS_ALPHA_MASK = 0xFF00FF;

    // TODO: Make configurable
    private static final int DIAG_MIN_BRIGHTNESS = 0x5A0000;
    private static final float DIAG_MIN_AO = 0.25f;

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

    public static void updateDiagonalAOShading(ShadingInfo topClock, ShadingInfo botClock, ShadingInfo botCounter,
        ShadingInfo topCounter) {

        // Brightness
        final int topBrightness = Math.max(Math.max(topClock.brightness, topCounter.brightness), DIAG_MIN_BRIGHTNESS);
        final int botBrightness = Math.max(Math.max(botClock.brightness, botCounter.brightness), DIAG_MIN_BRIGHTNESS);

        topClock.brightness = topCounter.brightness = topBrightness;
        botClock.brightness = botCounter.brightness = botBrightness;

        // AO
        topClock.ambientOcclusion = Math.max(topClock.ambientOcclusion, DIAG_MIN_AO);
        topClock.setRGB(topClock.pureColor);

        botClock.ambientOcclusion = Math.max(botClock.ambientOcclusion, DIAG_MIN_AO);
        botClock.setRGB(botClock.pureColor);

        topCounter.ambientOcclusion = Math.max(topCounter.ambientOcclusion, DIAG_MIN_AO);
        topCounter.setRGB(topCounter.pureColor);

        botCounter.ambientOcclusion = Math.max(botCounter.ambientOcclusion, DIAG_MIN_AO);
        botCounter.setRGB(botCounter.pureColor);
    }

    public static void applyFlatDiagonalShading(final Tessellator tess, final ShadingInfo clock,
        final ShadingInfo counter, final ShadingInfo front, final ShadingInfo back) {

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

        final int bright = (brightness + other.brightness) >> 1 & BRIGHTNESS_ALPHA_MASK;

        int avgColor = pureColor;
        if (avgColor != other.pureColor) {
            avgColor = RenderUtils.lerpARGB(pureColor, other.pureColor, ratio);
        }

        applyShading(tess, bright, avgColor, avgAO);
    }

    public void updateAverageShadingInfo(final ShadingInfo other, final float ratio) {
        updateAverageShadingInfo(this, other, ratio);
    }

    /// Update internal values based on two other ShadingInfos
    public void updateAverageShadingInfo(final ShadingInfo a, final ShadingInfo b, final float ratio) {
        ambientOcclusion = MathUtils.lerp(a.ambientOcclusion, b.ambientOcclusion, ratio);

        brightness = (a.brightness + b.brightness) >> 1 & BRIGHTNESS_ALPHA_MASK;

        if (a.pureColor != b.pureColor) {
            pureColor = RenderUtils.lerpARGB(a.pureColor, b.pureColor, ratio);
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
