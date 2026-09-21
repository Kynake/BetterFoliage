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
import mods.betterfoliage.client.resource.SpriteSet;
import mods.betterfoliage.mixins.interfaces.minecraft.ICrossedSquaresRenderer;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import static net.minecraftforge.common.util.ForgeDirection.EAST;

public class LeafRenderer extends BlockRenderer {

    private static final int SALT = 39845;

    // Multiply Horizontal scale by this factor to ensure the horizontal scale equals the vertical one.
    private static final float HORIZONTAL_SCALE_FACTOR = 0.5f / 0.45f;

    // Original renderer scaled the vertical axis so that the diagonal pixels look square instead of rectangular.
    private static final float VERTICAL_SCALE_FACTOR = 1.41f;

    // spotless:off
    private static final ForgeDirection[][] QUAD_AO_SOUTH = {
        // Quad 1
        { DOWN, NORTH, WEST },
        { DOWN, NORTH, EAST },
        { SOUTH, UP, EAST },
        { SOUTH, UP, WEST },

        // Quad 2
        { SOUTH, WEST, UP },
        { SOUTH, EAST, UP },
        { DOWN, NORTH, EAST },
        { DOWN, NORTH, WEST },

        // Quad 3
        { DOWN, SOUTH, WEST },
        { DOWN, SOUTH, EAST },
        { NORTH, EAST, UP },
        { NORTH, WEST, UP },

        // Quad 4
        { NORTH, UP, WEST },
        { NORTH, UP, EAST },
        { DOWN, SOUTH, EAST },
        { DOWN, SOUTH, WEST },
    };

    private static final ForgeDirection[][] QUAD_AO_EAST = {
        // Quad 1
        { WEST, UP, SOUTH },
        { WEST, UP, NORTH },
        { DOWN, EAST, NORTH },
        { DOWN, EAST, SOUTH },

        // Quad 2
        { DOWN, EAST, SOUTH },
        { DOWN, EAST, NORTH },
        { WEST, NORTH, UP },
        { WEST, SOUTH, UP },

        // Quad 3
        { DOWN, SOUTH, WEST },
        { DOWN, NORTH, WEST },
        { EAST, UP, NORTH },
        { EAST, UP, SOUTH },

        // Quad 4
        { EAST, SOUTH, UP },
        { EAST, NORTH, UP },
        { DOWN, WEST, NORTH },
        { DOWN, WEST, SOUTH },
    };
    // spotless:on

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
    public boolean isEligible(IBlockAccess world, int x, int y, int z) {
        final Block block = world.getBlock(x, y, z);

        return Config.leaves.INSTANCE.getEnabled() && Config.blocks.INSTANCE.getLeaves()
            .matchesID(block) && (!Config.leaves.INSTANCE.getSurfaceOnly() || isExposed(world, block, x, y, z));
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        // Use original render path when rendering block breaking overlay
        if (renderer.hasOverrideBlockTexture()) {
            renderer.setRenderBoundsFromBlock(block);
            return renderer.renderStandardBlock(block, x, y, z);
        }

        // TODO add option to skip rendering this when density is high enough
        final boolean renderResult = renderer.renderStandardBlock(block, x, y, z);
        if (!renderResult) return false;

        final IIcon keySprite = block.getIcon(world, x, y, z, DOWN.ordinal());
        final LeafInfo leaf = leafRegistry.getLeafForSprite(keySprite);

        if (leaf == null) return true;

        // Render Round Leaves
        final float scale = (float) Config.leaves.INSTANCE.getSize();
        final float verticalScale = scale * VERTICAL_SCALE_FACTOR;

        final int coordHash = MathUtils.hashCoords(x, y, z, SALT);

        final double vOffset = Config.leaves.INSTANCE.getVOffset();
        final double yOffset = y + MathUtils.hashToRange(coordHash, -vOffset, vOffset) + (1f - verticalScale) / 2f;

        final double hOffset = Config.leaves.INSTANCE.getHOffset();
        final double xOffset = x + MathUtils.hashToRange(MathUtils.hash(coordHash + 1), -hOffset, hOffset);
        final double zOffset = z + MathUtils.hashToRange(MathUtils.hash(coordHash + 2), -hOffset, hOffset);

        ICrossedSquaresRenderer leafRenderer = (ICrossedSquaresRenderer) renderer;

        IIcon sprite = leaf.getSpriteForCoord(x, y, z, UP.ordinal());
        float horizontalScale = scale * HORIZONTAL_SCALE_FACTOR;

        leafRenderer.betterfoliage$setVerticalScale(verticalScale);
        leafRenderer.betterfoliage$setAORender(block, x, y, z, true);
        renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

        if (Config.leaves.INSTANCE.getDense()) {
            final double rotX = x + 0.5;
            final double rotY = y + 0.5;
            final double rotZ = z + 0.5;

            sprite = leaf.getSpriteForCoord(x, y, z, SOUTH.ordinal());
            leafRenderer.betterfoliage$setRotation(rotX, rotY, rotZ, SOUTH);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

            sprite = leaf.getSpriteForCoord(x, y, z, EAST.ordinal());
            leafRenderer.betterfoliage$setRotation(rotX, rotY, rotZ, EAST);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);

            leafRenderer.betterfoliage$resetRotation();
        }

        if (Config.leaves.INSTANCE.getSnowEnabled() && BlockUtils.isSnow(world.getBlock(x, y + 1, z))) {
            sprite = snowCovering.getSpriteForCoord(x, y, z, UP.ordinal());
            leafRenderer.betterfoliage$setAORender(block, x, y, z, false);
            renderer.drawCrossedSquares(sprite, xOffset, yOffset, zOffset, horizontalScale);
        }

        leafRenderer.betterfoliage$resetAORender();
        leafRenderer.betterfoliage$resetVerticalScale();

        return true;
    }

    private static boolean isExposed(IBlockAccess world, Block block, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            final Block blockSide = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (!blocksLeafRendering(blockSide, block)) return true;
        }

        return false;
    }

    private static boolean blocksLeafRendering(Block visonBlocker, Block leaf) {
        return visonBlocker.isOpaqueCube() || visonBlocker == leaf;
    }

    /// Redefines AO directions when extra leaves are rotated
    /// Used for adding AO to extra leaves in dense mode
    /// NORTH / WEST rotation axis are not implemented, as they're not required
    public static ForgeDirection[] getSwizzledVertexAOs(ForgeDirection rotationAxis, int vertIndex) {
        switch (rotationAxis) {
            case SOUTH -> {
                return QUAD_AO_SOUTH[vertIndex];
            }

            case EAST -> {
                return QUAD_AO_EAST[vertIndex];
            }

            default -> {
                BetterFoliageMod.log.error("Attempted to get rotated dense leaves AO with invalid axis: {}",
                    rotationAxis);
                return null;
            }
        }
    }
}
