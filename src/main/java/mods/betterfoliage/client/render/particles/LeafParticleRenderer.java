package mods.betterfoliage.client.render.particles;

import java.awt.*;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.utils.MathUtils;

public class LeafParticleRenderer extends ParticleRenderer {

    private static final float BLOCK_BRIGHTNESS_MULTIPLIER = 0.5f;

    public static void spawnLeafParticle(World world, int x, int y, int z) {
        // TODO consider using an object pool (only if better performance)
        final LeafInfo leafInfo = LeafRegistry.getInstance()
            .getLeafForBlock(world, x, y, z);

        // No need to even try, as the texture would've been null
        if (leafInfo != null) {
            new LeafParticleRenderer(leafInfo, world, x, y, z);
        }
    }

    public static void initSprites() {
        BetterFoliageMod.log.info("LeafParticleRenderer sprites initialized");
    }

    protected LeafParticleRenderer(LeafInfo leafInfo, World world, int x, int y, int z) {
        super(leafInfo.particleSprites.getRandomSprite(), world, x, y, z);
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6, 1.0) * Config.fallingLeaves.INSTANCE.getLifetime()
            * 20.0);
        motionY = -Config.fallingLeaves.INSTANCE.getSpeed();
        particleScale = (float) Config.fallingLeaves.INSTANCE.getSize() * 0.1f;
        quadMirrorHorizontally = rand.nextBoolean();

        Block block = world.getBlock(x, y, z);
        setParticleColor(leafInfo.averageColor, block.colorMultiplier(world, x, y, z));
        particleAlpha = 1.0f;
    }

    // TODO consider using blendRGB (squares?)
    protected void setParticleColor(int spriteAverageColor, int blockColor) {

        int r = spriteAverageColor >> 16 & 0xFF;
        int g = spriteAverageColor >> 8 & 0xFF;
        int b = spriteAverageColor & 0xFF;

        final float[] hsbSprite = Color.RGBtoHSB(r, g, b, null);

        r = blockColor >> 16 & 0xFF;
        g = blockColor >> 8 & 0xFF;
        b = blockColor & 0xFF;

        final float[] hsbBlock = Color.RGBtoHSB(r, g, b, null);

        final float spriteRatio = hsbSprite[1] / (hsbSprite[1] + hsbBlock[1]);
        final float blockRatio = 1.0f - spriteRatio;

        final float hue = hsbSprite[0] * spriteRatio + hsbBlock[0] * blockRatio;
        final float saturation = hsbSprite[1] * spriteRatio + hsbBlock[1] * blockRatio;
        final float brightness = hsbSprite[2] * spriteRatio + hsbBlock[2] * blockRatio * BLOCK_BRIGHTNESS_MULTIPLIER;

        setRGBColor(Color.HSBtoRGB(hue, saturation, brightness));
    }

    @Override
    protected void update() {
        // TODO movement / wind
        motionY = -Config.fallingLeaves.INSTANCE.getSpeed();
    }

    @Override
    protected void render() {
        quadRotationRadians = 0;
        calculateQuadCenter(posX, posY, posZ, prevPosX, prevPosY, prevPosZ);
        renderBillboardQuad();
    }
}
