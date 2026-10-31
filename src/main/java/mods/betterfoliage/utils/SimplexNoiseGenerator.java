package mods.betterfoliage.utils;

import java.util.Random;

import net.minecraft.world.gen.NoiseGeneratorSimplex;

import mods.betterfoliage.config.Configs;

// TODO: Experiment with other noise generators
public class SimplexNoiseGenerator {

    private final NoiseGeneratorSimplex noise;

    public SimplexNoiseGenerator(int salt) {
        noise = new NoiseGeneratorSimplex(new Random((long) Configs.seed + (long) salt));
    }

    public boolean isBelowThreshold(int x, int y, float threshold) {
        // Converts threshold from [0, 1] range to [1, 1]
        return noise.func_151605_a(x, y) < 2f * threshold - 1f;
    }
}
