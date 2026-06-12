package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.client.resource.SpriteSingle;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;

public class CactusRenderer extends BlockRenderer {

    // Brings the cactus arms closer to the stem
    private static final double ARM_PUSHBACK = 1.0 / 16.0;
    private static final float STEM_SCALE = 1.4f;

    // Height is increased in the original, for some reason
    private static final float STEM_VERTICAL_SCALE = STEM_SCALE * 1.41f;

    private static final double OFFSET_EPSILON = 0.002;

    // spotless:off
    private static final ForgeDirection[] SIDES = {
        ForgeDirection.NORTH,
        ForgeDirection.SOUTH,
        ForgeDirection.WEST,
        ForgeDirection.EAST
    };

    private static final ForgeDirection[] ROTATIONS = {
        ForgeDirection.EAST,
        ForgeDirection.WEST,
        ForgeDirection.SOUTH,
        ForgeDirection.NORTH
    };
    // spotless:on

    private static CactusRenderer instance;

    private final ISpriteProvider stem = new SpriteSingle(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_cactus.png");

    private final ISpriteProvider arms = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_cactus_arm_",
        ".png");

    public static CactusRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Cactus Renderer");
            instance = new CactusRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        return (Config.cactus.INSTANCE.getStem() || Config.cactus.INSTANCE.getArms())
            && Config.blocks.INSTANCE.getCactus()
                .matchesID(world.getBlock(x, y, z));
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        boolean renderResult = renderer.renderBlockCactus(block, x, y, z);

        if (!renderResult) return false;

        ICrossedSquaresRenderer cactusRenderer = (ICrossedSquaresRenderer) renderer;
        Tessellator tessellator = Tessellator.instance;

        // Render Stem
        if (Config.cactus.INSTANCE.getStem()) {
            int hash = MathUtils.hashCoords(x, y, z);

            double sizeOffset = Config.cactus.INSTANCE.getSizeVariation();
            float scale = (float) (STEM_SCALE + MathUtils.hashToRange(hash, 0, sizeOffset));

            double xOffset = x;
            double zOffset = z;

            // TODO Move to static utils method
            if ((y & 1) == 0) {
                xOffset += OFFSET_EPSILON;
                zOffset += OFFSET_EPSILON / 2.0;
            }

            if ((x & 1) == 0) {
                xOffset += OFFSET_EPSILON / 8.0;
                zOffset += OFFSET_EPSILON / 4.0;
            }

            if ((z & 1) == 0) {
                xOffset += OFFSET_EPSILON / 16.0;
                zOffset += OFFSET_EPSILON / 32.0;
            }

            cactusRenderer.betterfoliage$setVerticalScale(STEM_VERTICAL_SCALE);
            cactusRenderer.betterfoliage$setAORender(block, x, y, z, false);

            renderer.drawCrossedSquares(
                stem.getSpriteForCoord(x, y, z),
                xOffset,
                y - STEM_VERTICAL_SCALE / 4.0,
                zOffset,
                scale);

            cactusRenderer.betterfoliage$resetAORender();
            cactusRenderer.betterfoliage$resetVerticalScale();
        }

        // Render Arms
        if (Config.cactus.INSTANCE.getArms()) {
            for (int i = 0; i < SIDES.length; i++) {
                int ordinal = SIDES[i].ordinal();

                int sideHash = MathUtils.hashCoords(x, y, z, ordinal);

                // TODO convert to [0, 1] range RNG
                if (MathUtils.hashToRange(sideHash, 0, 64) > Config.cactus.INSTANCE.getArmChance()) {
                    continue;
                }

                int xSide = x + SIDES[i].offsetX;
                int ySide = y + SIDES[i].offsetY;
                int zSide = z + SIDES[i].offsetZ;

                IIcon armSprite = arms.getSpriteForCoord(x, y, z, ordinal);

                double hOffset = Config.cactus.INSTANCE.getHOffset();
                double xOffset = xSide + MathUtils.hashToRange(MathUtils.hash(sideHash + 1), -hOffset, hOffset);
                double zOffset = zSide + MathUtils.hashToRange(MathUtils.hash(sideHash + 2), -hOffset, hOffset);

                tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, xSide, ySide, zSide));
                RenderUtils.setColorMultiplierBySide(tessellator, ordinal);

                cactusRenderer.betterfoliage$setRotation(xSide + 0.5, ySide + 0.5, zSide + 0.5, ROTATIONS[i]);
                renderer.drawCrossedSquares(
                    armSprite,
                    xOffset,
                    ySide - ARM_PUSHBACK,
                    zOffset,
                    (float) Config.cactus.INSTANCE.getSize());
                cactusRenderer.betterfoliage$resetRotation();
            }
        }

        return true;
    }
}
