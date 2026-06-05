package mods.betterfoliage.client.render.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.registries.LeafInfo;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.render.Utils;
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.MathUtils;
import mods.octarinecore.client.render.BlockContext;

public class LeafRenderer extends BlockRenderer {

    private static final int SALT = 39845;

    // Multiply Horizontal scale by this factor to ensure the horizontal scale equals the vertical one.
    private static final float HORIZONTAL_SCALE_FACTOR = 0.5f / 0.45f;

    // Original renderer scaled the vertical axis so that the diagonal pixels look square instead of rectangular.
    private static final float VERTICAL_SCALE_FACTOR = 1.41f;

    private static final LeafRegistry leafRegistry = LeafRegistry.getInstance();

    private static LeafRenderer instance;

    private final ISpriteProvider snowCovering = new SpriteSet(
        BetterFoliageMod.LEGACY_DOMAIN,
        "textures/blocks/better_leaves_snowed_",
        ".png");

    public static LeafRenderer getInstance() {
        if (instance == null) {
            BetterFoliageMod.log.info("Registering NEW! Leaf Renderer");
            instance = new LeafRenderer();
        }

        return instance;
    }

    @Override
    public boolean isEligible(BlockContext ctx) {
        return Config.leaves.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getLeaves()
            .matchesID(ctx.getBlock())
            && (!Config.leaves.INSTANCE.getSurfaceOnly()
                || isExposed(ctx.getWorld(), ctx.getBlock(), ctx.getX(), ctx.getY(), ctx.getZ()));
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        boolean renderResult = renderer.renderStandardBlock(block, x, y, z);

        if (!renderResult) return false;

        IIcon keySprite = block.getIcon(world, x, y, z, ForgeDirection.DOWN.ordinal());
        LeafInfo leaf = leafRegistry.getLeafForSprite(keySprite);

        if (leaf == null) return true;

        // Render Round Leaves
        float scale = (float) Config.leaves.INSTANCE.getSize();
        float verticalScale = scale * VERTICAL_SCALE_FACTOR;

        int coordHash = MathUtils.hashCoords(x, y, z, SALT);

        double vOffset = Config.leaves.INSTANCE.getVOffset();
        double yOffset = y + MathUtils.hashToRange(coordHash, -vOffset, vOffset) + (1f - verticalScale) / 2f;

        double hOffset = Config.leaves.INSTANCE.getHOffset();
        double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        ICrossedSquaresRenderer leafRenderer = (ICrossedSquaresRenderer) renderer;

        IIcon sprite = leaf.getSpriteForCoord(x, y, z, ForgeDirection.UP.ordinal());
        float horizontalScale = scale * HORIZONTAL_SCALE_FACTOR;

        leafRenderer.betterfoliage$setVerticalScale(verticalScale);
        leafRenderer.betterfoliage$setAORender(block, x, y, z, true);
        renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

        if (Config.leaves.INSTANCE.getDense()) {
            double rotX = x + 0.5;
            double rotY = y + 0.5;
            double rotZ = z + 0.5;

            sprite = leaf.getSpriteForCoord(x, y, z, ForgeDirection.SOUTH.ordinal());
            leafRenderer.betterfoliage$setRotation(rotX, rotY, rotZ, ForgeDirection.SOUTH);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

            sprite = leaf.getSpriteForCoord(x, y, z, ForgeDirection.EAST.ordinal());
            leafRenderer.betterfoliage$setRotation(rotX, rotY, rotZ, ForgeDirection.EAST);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

            leafRenderer.betterfoliage$resetRotation();
        }

        if (Config.leaves.INSTANCE.getSnowEnabled() && Utils.isSnow(world.getBlock(x, y + 1, z))) {
            sprite = snowCovering.getSpriteForCoord(x, y, z, ForgeDirection.UP.ordinal());
            leafRenderer.betterfoliage$setAORender(block, x, y, z, false);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);
        }

        leafRenderer.betterfoliage$resetAORender();
        leafRenderer.betterfoliage$resetVerticalScale();

        return true;
    }

    private static boolean isExposed(IBlockAccess world, Block block, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            Block blockSide = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (!blocksLeafRendering(blockSide, block)) return true;
        }

        return false;
    }

    private static boolean blocksLeafRendering(Block visonBlocker, Block leaf) {
        return visonBlocker.isOpaqueCube() || visonBlocker == leaf;
    }
}
