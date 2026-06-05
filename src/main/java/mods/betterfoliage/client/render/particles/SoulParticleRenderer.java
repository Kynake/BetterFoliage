package mods.betterfoliage.client.render.particles;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.integration.EFRIntegration;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.SpriteSingle;
import mods.betterfoliage.utils.MathUtils;

public class SoulParticleRenderer extends ParticleRenderer {

    private static final ISpriteProvider headSprites = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/rising_soul_",
        ".png");

    private static final ISpriteProvider tailSprites = new SpriteSingle(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/soul_track.png");

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
        particleMaxAge = (int) (MathUtils.randomBetween(rand, 0.6f, 1.0f) * Config.risingSoul.INSTANCE.getLifetime()
            * 20);
    }

    @Override
    protected void update() {
        if (!(Config.INSTANCE.getEnabled() & Config.risingSoul.INSTANCE.getEnabled())) {
            setDead();
            return;
        }
    }
}
