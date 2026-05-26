package mods.betterfoliage.client.render.particles;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.resource.SpriteSetRandom;
import mods.betterfoliage.utils.MathUtils;

public class LeafParticleRenderer extends ParticleRenderer {

    // TODO fetch from LeafRegistry
    private static final SpriteSetRandom leafParticle = new SpriteSetRandom(
        BetterFoliageMod.DOMAIN,
        "textures/blocks/falling_leaf_default_",
        ".png");

    public static void spawnLeafParticle(World world, int x, int y, int z) {
        // TODO consider using an object pool (only if better performance)
        new LeafParticleRenderer(world, x, y, z);
    }

    public static void initSprites() {
        BetterFoliageMod.log.info("LeafParticleRenderer sprites initialized");
    }

    protected LeafParticleRenderer(World world, int x, int y, int z) {
        super(leafParticle.getRandomSprite(), world, x, y, z);
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6, 1.0) * Config.fallingLeaves.INSTANCE.getLifetime()
            * 20.0);
        motionY = -Config.fallingLeaves.INSTANCE.getSpeed();
        particleScale = (float) Config.fallingLeaves.INSTANCE.getSize() * 0.1f;
        quadMirrorHorizontally = rand.nextBoolean();

        Block block = world.getBlock(x, y, z);
        // TODO better coloring
        setRGBColor(block.colorMultiplier(world, x, y, z));
        particleAlpha = 1.0f;
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
