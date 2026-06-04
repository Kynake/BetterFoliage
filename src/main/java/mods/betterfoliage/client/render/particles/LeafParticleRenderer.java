package mods.betterfoliage.client.render.particles;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.utils.MathUtils;

public class LeafParticleRenderer extends ParticleRenderer {

    private static final float BLOCK_BRIGHTNESS_MULTIPLIER = 0.5f;

    // TODO: make all configurable
    private static final int FADEOUT_TICKS = 20;
    private static final float ROTATION_SPEED = (float) (Math.PI * 2.0 / 64.0);
    private static final float GROUND_MOVE_DAMPENING = 0.5f;

    private boolean firstGroundHit = false;
    private float rotationPerTick;

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
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6f, 1.0f) * Config.fallingLeaves.INSTANCE.getLifetime()
            * 20.0);

        motionY = -Config.fallingLeaves.INSTANCE.getSpeed();
        particleScale = (float) Config.fallingLeaves.INSTANCE.getSize() * 0.1f;

        quadMirrorHorizontally = rand.nextBoolean();
        rotationPerTick = ROTATION_SPEED;
        changeRandomRotation();

        Block block = world.getBlock(x, y, z);
        setParticleColor(leafInfo.averageColor, block.colorMultiplier(world, x, y, z));
        particleAlpha = 1.0f;
    }

    protected void setParticleColor(int spriteAverageColor, int blockColor) {

        int r = spriteAverageColor >> 16 & 0xFF;
        int g = spriteAverageColor >> 8 & 0xFF;
        int b = spriteAverageColor & 0xFF;

        final float[] hsb = Color.RGBtoHSB(r, g, b, null);

        final float hSprite = hsb[0];
        final float sSprite = hsb[1];
        final float bSprite = hsb[2];

        r = blockColor >> 16 & 0xFF;
        g = blockColor >> 8 & 0xFF;
        b = blockColor & 0xFF;

        Color.RGBtoHSB(r, g, b, hsb);

        final float spriteRatio = sSprite / (sSprite + hsb[1]);
        final float blockRatio = 1.0f - spriteRatio;

        final float hue = hSprite * spriteRatio + hsb[0] * blockRatio;
        final float saturation = sSprite * spriteRatio + hsb[1] * blockRatio;
        final float brightness = bSprite * spriteRatio + hsb[2] * blockRatio * BLOCK_BRIGHTNESS_MULTIPLIER;

        setRGBColor(Color.HSBtoRGB(hue, saturation, brightness));
    }

    @Override
    protected void update() {
        // 1 second fadeout
        final int fadeoutAge = particleMaxAge - FADEOUT_TICKS;

        if (particleAge > fadeoutAge) {
            particleAlpha = MathUtils.inverseLerp(particleMaxAge, fadeoutAge, particleAge);
        }

        final float speed = (float) Config.fallingLeaves.INSTANCE.getSpeed();
        motionY = -speed;

        if (onGround) {

            // Dampen movement when hitting the ground, rather than a complete stop.
            // This adds a very small "settling" effect to the leaves.
            motionX *= GROUND_MOVE_DAMPENING;
            motionZ *= GROUND_MOVE_DAMPENING;

            if (!firstGroundHit) {
                // TODO add configurable
                // start fadeout when hitting ground
                particleAge = Math.max(particleAge, fadeoutAge);
                firstGroundHit = true;
            }
            return;
        }

        changeRandomRotation();
        rotationRadians += rotationPerTick;

        final float perturb = (float) Config.fallingLeaves.INSTANCE.getPerturb();

        // TODO movement due to wind (after * perturb, before * speed)
        motionX = MathHelper.cos(rotationRadians) * perturb * speed;
        motionZ = MathHelper.sin(rotationRadians) * perturb * speed;
    }

    @Override
    protected void render() {
        // TODO retest properly (vanilla, angelica, swansong)
        if (Config.fallingLeaves.INSTANCE.getOpacityHack()) {
            GL11.glDepthMask(true);
        }

        calculateQuadCenter(posX, posY, posZ, prevPosX, prevPosY, prevPosZ);
        renderBillboardQuad();
    }

    private void changeRandomRotation() {
        if (rand.nextFloat() > 0.95f) {
            rotationPerTick = -rotationPerTick;
        }
    }
}
