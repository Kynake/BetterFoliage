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

    // TODO: make configurable
    private static final float ROTATION_SPEED = (float) (Math.PI * 2.0 / 64.0);

    private boolean didHitGround = false;
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
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6, 1.0) * Config.fallingLeaves.INSTANCE.getLifetime()
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

    // TODO consider using blendRGB (squares?)
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
        // TODO movement / wind

        // 1 second fadeout
        if (particleAge > particleMaxAge - 20) {
            particleAlpha = 0.05f * (particleMaxAge - particleAge);
        }

        if (didHitGround || onGround) {
            motionY = 0;

            if (!didHitGround) {
                // TODO add configurable
                // start fadeout upon hitting ground
                particleAge = Math.max(particleAge, particleMaxAge - 20);
                didHitGround = true;
            }
            return;
        }

        changeRandomRotation();
        rotationRadians += rotationPerTick;

        motionY = -Config.fallingLeaves.INSTANCE.getSpeed();
    }

    @Override
    protected void render() {
        calculateQuadCenter(posX, posY, posZ, prevPosX, prevPosY, prevPosZ);
        renderBillboardQuad();
    }

    private void changeRandomRotation() {
        if (rand.nextFloat() > 0.95f) {
            rotationPerTick = -rotationPerTick;
        }
    }
}
