package mods.betterfoliage.client.registries;

import net.minecraft.util.IIcon;

import com.github.bsideup.jabel.Desugar;

import mods.betterfoliage.client.render.RenderUtils;

@Desugar
public record GrassInfo(int customColor) {

    /// The default color multiplier for blocks that don't use custom coloring, like standard grass.
    /// If a block's value for this is different from the default, then we should _not_ use customColor.
    public static final int DEFAULT_COLOR_MULTIPLIER = 0xFF_FF_FF;

    public GrassInfo(IIcon customColor) {
        this(RenderUtils.averageSpriteSquare(customColor, DEFAULT_COLOR_MULTIPLIER));
    }
}
