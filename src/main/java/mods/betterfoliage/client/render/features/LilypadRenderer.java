package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

public class LilypadRenderer extends BlockRenderer {

    private static final float LILYPAD_HEIGHT = 0.015625f;

    // TODO: make configurable
    private static final float LILYPAD_FLOWER_SCALE = 0.5f;

    private static LilypadRenderer instance;

    private final ISpriteProvider roots = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_lilypad_roots_",
        ".png");

    private final ISpriteProvider flowers = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_lilypad_flower_",
        ".png");

    public static LilypadRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Lilypad Renderer");
            instance = new LilypadRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.lilypad.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getLilypad()
            .matchesID(ctx.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        boolean renderResult = renderer.renderBlockLilyPad(block, x, y, z);

        if (!renderResult) return false;

        // Render Roots
        double hOffset = Config.lilypad.INSTANCE.getHOffset();

        int coordHash = MathUtils.hashCoords(x, y - 1, z, 375);
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        IIcon sprite = roots.getSpriteForCoord(x, y - 1, z);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque(0xFF, 0xFF, 0xFF);

        tessellator.setBrightness(Blocks.tallgrass.getMixedBrightnessForBlock(world, x, y - 1, z));
        renderer.drawCrossedSquares(sprite, xOffset, y + LILYPAD_HEIGHT - 1, zOffset, 1.0f);

        // Render Flower
        coordHash = MathUtils.hashCoords(x, y, z, 375);

        // TODO convert to [0, 1] range RNG
        if (MathUtils.hashToRange(coordHash, 0, 64) > Config.lilypad.INSTANCE.getFlowerChance()) {
            return true;
        }

        xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        sprite = flowers.getSpriteForCoord(x, y, z);

        tessellator.setBrightness(Blocks.red_flower.getMixedBrightnessForBlock(world, x, y, z));
        renderer.drawCrossedSquares(sprite, xOffset, y + LILYPAD_HEIGHT, zOffset, LILYPAD_FLOWER_SCALE);

        return true;
    }
}
