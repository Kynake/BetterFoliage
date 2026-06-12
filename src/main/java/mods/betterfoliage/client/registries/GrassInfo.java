package mods.betterfoliage.client.registries;

import net.minecraft.util.IIcon;

import com.github.bsideup.jabel.Desugar;

import mods.betterfoliage.client.render.RenderUtils;

@Desugar
public record GrassInfo(int customColor) {

    public GrassInfo(IIcon customColor) {
        this(RenderUtils.averageSpriteSquare(customColor, RenderUtils.DEFAULT_COLOR_MULTIPLIER));
    }
}
