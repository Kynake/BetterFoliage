package mods.betterfoliage.client.render.particles;

import java.awt.Color;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.Configs;
import mods.betterfoliage.client.WindTracker;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

public class LeafParticleRenderer extends ParticleRenderer {

    private static final float BLOCK_BRIGHTNESS_MULTIPLIER = 0.5f;
    private static final float ROTATION_MULTIPLIER = MathUtils.PI2 / 64.0f;
    private static final float KEEP_ROTATION_DIR_CHANCE = 0.95f;

    private boolean isFadingFromGroundHit = false;
    private float rotationPerTick;

    public static void spawnLeafParticle(World world, int x, int y, int z) {
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

        particleMaxAge = MathUtils
            .randomBetween(rand, Configs.FallingLeaves.minLifetimeTicks, Configs.FallingLeaves.maxLifetimeTicks);

        motionY = -Configs.FallingLeaves.speed;
        particleScale = Configs.FallingLeaves.size;

        quadMirrorHorizontally = rand.nextBoolean();
        rotationPerTick = Configs.FallingLeaves.rotationSpeed * ROTATION_MULTIPLIER;
        changeRandomRotation();

        setParticleColor(leafInfo.averageColor, BlockUtils.getStandardColorMultiplier(world, x, y, z));
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
        if (!(Configs.enabled & Configs.FallingLeaves.enabled)) {
            setDead();
            return;
        }

        final int fadeoutAge = particleMaxAge - Math.min(Configs.FallingLeaves.maxFadeoutTicks, particleMaxAge);

        if (particleAge > fadeoutAge) {
            particleAlpha = MathUtils.inverseLerp(particleMaxAge, fadeoutAge, particleAge);
        }

        motionY = -Configs.FallingLeaves.speed;

        if (onGround) {

            // Dampen movement when hitting the ground, rather than a complete stop.
            // This adds a very small "settling" effect to the leaves.
            motionX *= Configs.FallingLeaves.groundDampening;
            motionZ *= Configs.FallingLeaves.groundDampening;

            if (Configs.FallingLeaves.fadeoutOnGroundHit && !isFadingFromGroundHit) {
                particleAge = Math.max(particleAge, fadeoutAge);
                isFadingFromGroundHit = true;
            }

            return;
        }

        changeRandomRotation();
        rotationRadians += rotationPerTick;

        motionX = (MathHelper.cos(rotationRadians) * Configs.FallingLeaves.perturb + WindTracker.currentX)
            * Configs.FallingLeaves.speed;
        motionZ = (MathHelper.sin(rotationRadians) * Configs.FallingLeaves.perturb + WindTracker.currentZ)
            * Configs.FallingLeaves.speed;
    }

    @Override
    protected void render() {
        // TODO retest properly (vanilla, angelica, swansong)
        if (Configs.FallingLeaves.opacityHack) {
            GL11.glDepthMask(true);
        }

        calculateQuadCenter(posX, posY, posZ, prevPosX, prevPosY, prevPosZ);
        renderBillboardQuad();
    }

    private void changeRandomRotation() {
        if (rand.nextFloat() > KEEP_ROTATION_DIR_CHANCE) {
            rotationPerTick = -rotationPerTick;
        }
    }
}
