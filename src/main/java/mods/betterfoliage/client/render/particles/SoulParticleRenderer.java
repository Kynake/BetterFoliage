package mods.betterfoliage.client.render.particles;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.integration.EFRIntegration;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSetRandom;
import mods.betterfoliage.client.resource.SpriteSingle;
import mods.betterfoliage.utils.MathUtils;

public class SoulParticleRenderer extends ParticleRenderer {

    private static final float CORKSCREW_SPEED = MathUtils.PI2 / 64f;
    private static final int FADEOUT_TICKS = 20;

    private static final ISpriteProvider headSprites = new SpriteSetRandom(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/rising_soul_",
        ".png");

    private static final ISpriteProvider tailSprites = new SpriteSingle(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/soul_track.png");

    private final float initialRadians = MathUtils.PI2 * rand.nextFloat();

    public static boolean isValidSoulBlock(Block block) {
        return block == Blocks.soul_sand || EFRIntegration.isEFRSoulSoil(block);
    }

    public static void initSprites() {
        BetterFoliageMod.log.info("Soul particle sprites initialized");
    }

    public static void spawnSoulParticle(World world, int x, int y, int z) {
        new SoulParticleRenderer(headSprites.getSpriteForCoord(x, y, z), world, x, y, z, false);
    }

    protected SoulParticleRenderer(IIcon sprite, World world, int x, int y, int z, boolean isTail) {
        super(sprite, world, x, y, z);

        particleGravity = 0f;
        motionY = 0.1;
        particleScale = (float) Config.risingSoul.INSTANCE.getHeadSize() * 0.25f;
        particleAlpha = Config.risingSoul.INSTANCE.getOpacity();
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6f, 1.0f) * Config.risingSoul.INSTANCE.getLifetime()
            * 20);
    }

    @Override
    protected void update() {
        if (!(Config.INSTANCE.getEnabled() & Config.risingSoul.INSTANCE.getEnabled())) {
            setDead();
            return;
        }

        motionY = 0.1;

        final float offsetRadians = initialRadians + particleAge * CORKSCREW_SPEED;

        motionX = MathHelper.cos(offsetRadians) * Config.risingSoul.INSTANCE.getPerturb();
        motionZ = MathHelper.sin(offsetRadians) * Config.risingSoul.INSTANCE.getPerturb();

        final int fadeoutAge = particleMaxAge - FADEOUT_TICKS;
        if (particleAge >= fadeoutAge) {
            particleAlpha = MathUtils
                .remapToRange(fadeoutAge, particleMaxAge, Config.risingSoul.INSTANCE.getOpacity(), 0, particleAge);
        }

        if (particleAge % Config.risingSoul.INSTANCE.getTrailDensity() == 0) {
            new TailParticleRender(worldObj, posX, posY, posZ, particleAlpha);
        }
    }

    private static final class TailParticleRender extends ParticleRenderer {

        private static final int TAIL_MAX_AGE = 40;
        private static final int TAIL_APPEAR_TIME = TAIL_MAX_AGE / 4;

        private final float rotationSpeed = MathUtils.randomBetween(rand, 0.2f, 1f) * MathUtils.PI2 / 32.0f;
        private final float maxAlpha;

        private TailParticleRender(World world, double x, double y, double z, float initialAlpha) {
            super(tailSprites.getSpriteForCoord(0, 0, 0, 0), world, x, y, z);

            maxAlpha = initialAlpha;
            particleMaxAge = TAIL_MAX_AGE;
            particleGravity = 0;
            motionX = 0;
            motionY = -0.01;
            motionZ = 0;

            particleScale = (float) Config.risingSoul.INSTANCE.getTrailSize() * 0.25f;
            rotationRadians = MathUtils.PI2 * rand.nextFloat();
        }

        @Override
        protected void update() {
            if (!(Config.INSTANCE.getEnabled() & Config.risingSoul.INSTANCE.getEnabled())) {
                setDead();
                return;
            }

            particleAlpha = particleAge <= TAIL_APPEAR_TIME
                ? MathUtils.remapToRange(0, TAIL_APPEAR_TIME, 0, maxAlpha, particleAge)
                : MathUtils.remapToRange(TAIL_APPEAR_TIME, particleMaxAge, maxAlpha, 0, particleAge);

            particleScale *= (float) Config.risingSoul.INSTANCE.getSizeDecay();
            rotationRadians += rotationSpeed;
        }
    }
}
