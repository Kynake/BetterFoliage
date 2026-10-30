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

    // TODO: Actually check distribution of what is calculated here, instead of just copying the old mod
    // Good enough for now
    public boolean isAboveThreshold(int x, int y, float threshold) {
        return noise.func_151605_a(x, y) + 1.0 > threshold;
    }
}
