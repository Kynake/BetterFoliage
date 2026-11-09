package mods.betterfoliage.client.render;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.config.Configs;
import mods.betterfoliage.mixins.interfaces.minecraft.IRendererByType;
import mods.betterfoliage.utils.BlockUtils;
import mods.betterfoliage.utils.MathUtils;

public class RenderUtils {

    /// The default color multiplier for blocks that don't use custom coloring, like standard grass.
    /// If a block's value for this is different from the default, then we should _not_ use customColor.
    public static final int DEFAULT_COLOR_MULTIPLIER = 0xFF_FF_FF;

    private static final float COUNTERCLOCK_SIN = MathHelper.sin((float) (Math.PI / 2D));
    private static final float COUNTERCLOCK_COS = MathHelper.cos((float) (Math.PI / 2D));
    private static final float CLOCKWISE_SIN = MathHelper.sin((float) (3D * Math.PI / 2D));
    private static final float CLOCKWISE_COS = MathHelper.cos((float) (3D * Math.PI / 2D));

    public static boolean shouldRenderBlockWithAO(Block block) {
        return Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0;
    }

    /// Defer block rendering back to the original renderer, ignoring BetterFoliage overrides
    public static boolean renderBaseBlock(final int x, final int y, final int z, final Block block,
        final RenderBlocks renderer) {
        final IRendererByType baseRenderer = (IRendererByType) renderer;
        return baseRenderer.betterfoliage$renderBaseBlock(block, x, y, z);
    }

    public static float getColorMultiplierBySide(ForgeDirection side) {
        return getColorMultiplierBySide(side.ordinal());
    }

    public static float getColorMultiplierBySide(int side) {
        return switch (side) {
            case 0 -> 0.5f; // Down (-Y)
            case 2, 3 -> 0.8f; // North (-Z), South (+Z)
            case 4, 5 -> 0.6f; // West (-X), East (+X)
            default -> 1f; // Up (+Y), or UNKNOWN
        };
    }

    public static void setColorMultiplierBySide(Tessellator tessellator, int side) {
        int multiplier = (int) (getColorMultiplierBySide(side) * 255f);
        tessellator.setColorRGBA(multiplier, multiplier, multiplier, 0xFF);
    }

    /// Linearly interpolate between two colors in ARGB format. Alpha is also interpolated
    public static int lerpARGB(int colorA, int colorB, float ratio) {
        final int a = Math.round(MathUtils.lerp((float) (colorA >> 24 & 0xFF), (float) (colorB >> 24 & 0xFF), ratio));
        final int r = Math.round(MathUtils.lerp((float) (colorA >> 16 & 0xFF), (float) (colorB >> 16 & 0xFF), ratio));
        final int g = Math.round(MathUtils.lerp((float) (colorA >> 8 & 0xFF), (float) (colorB >> 8 & 0xFF), ratio));
        final int b = Math.round(MathUtils.lerp((float) (colorA & 0xFF), (float) (colorB & 0xFF), ratio));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /// Calculate the weighted average of two colors in ARGB format. Alpha is copied from first color
    public static int averageRGB(int colorA, int colorB, float ratio) {
        final float invRatio = 1f / ratio;
        final int r = (int) (ratio * (colorA >> 16 & 0xFF) + invRatio * (colorB >> 16 & 0xFF));
        final int g = (int) (ratio * (colorA >> 8 & 0xFF) + invRatio * (colorB >> 8 & 0xFF));
        final int b = (int) (ratio * (colorA & 0xFF) + invRatio * (colorB & 0xFF));

        return (colorA & 0xFF_00_00_00) | (r << 16) | (g << 8) | b;
    }

    /// Calculates the average alpha channel between two colors in ARGB format. RGB channels are copied from first color
    public static int averageAlphas(int colorA, int colorB) {
        final float alphaA = ((colorA >> 24) & 0xFF) / (float) 0xFF;
        final float alphaB = ((colorB >> 24) & 0xFF) / (float) 0xFF;
        final int alpha = (int) (alphaA * alphaB * 0xFF);

        return (alpha << 24) | (colorA & 0x00_FF_FF_FF);
    }

    /// Average visible colors in a Sprite using their squares
    public static int averageSpriteSquare(IIcon sprite, int defaultColor) {
        ResourceLocation location = ResourceUtils.convertToResourceLocation(sprite);

        BufferedImage spriteImage;
        try {
            IResource resource = ResourceUtils.getResource(location);
            spriteImage = ImageIO.read(resource.getInputStream());
        } catch (IOException e) {
            BetterFoliageMod.log
                .error("Error loading sprite [{}] at [{}] for color averaging.", sprite.getIconName(), location);
            return defaultColor;
        }

        int opaquePixelsCount = 0;
        double rSqr = 0;
        double gSqr = 0;
        double bSqr = 0;

        for (int x = 0; x < spriteImage.getWidth(); x++) {
            for (int y = 0; y < spriteImage.getHeight(); y++) {
                final int pixel = spriteImage.getRGB(x, y);

                // Only fully opaque pixels are considered
                if ((pixel & 0xFF_00_00_00) != 0xFF_00_00_00) continue;

                opaquePixelsCount++;

                final double r = (double) (pixel >> 16 & 0xFF) / 255.0;
                final double g = (double) (pixel >> 8 & 0xFF) / 255.0;
                final double b = (double) (pixel & 0xFF) / 255.0;

                rSqr += r * r;
                gSqr += g * g;
                bSqr += b * b;
            }
        }

        int color = 0xFF_00_00_00;
        color |= (int) (Math.sqrt(rSqr / opaquePixelsCount) * 255.0) << 16;
        color |= (int) (Math.sqrt(gSqr / opaquePixelsCount) * 255.0) << 8;
        color |= (int) (Math.sqrt(bSqr / opaquePixelsCount) * 255.0);

        BetterFoliageMod.log.info("Average color for [{}]: {}", sprite.getIconName(), String.format("0x%08x", color));
        return color;
    }

    public static void getShadingInfoForCrossedSquareVertex(RenderBlocks renderer, int x, int y, int z,
        ForgeDirection face, ForgeDirection hor, ForgeDirection vert, boolean useBlockColor,
        ShadingInfo shadingContainer) {

        final IBlockAccess world = renderer.blockAccess;
        final Block block = world.getBlock(x, y, z);

        int aoX = x;
        int aoY = y;
        int aoZ = z;

        // We use standard block multiplier here because our only use of AO with block color is extra leaves,
        // which would need the same multiplier as the standard leaf block.
        // (If this ever changes then this method has to be refactored)
        int color = useBlockColor ? BlockUtils.getStandardColorMultiplier(world, block, x, y, z)
            : DEFAULT_COLOR_MULTIPLIER;

        /// Corner Selection
        final ForgeDirection aoFace;
        final ForgeDirection aoHor;
        final ForgeDirection aoVert;

        // Prioritize AO from the vertical axis, if not occluded
        if (isFaceExposed(world, block, x, y, z, vert)) {
            aoFace = vert;
            aoHor = hor;
            aoVert = face;
        }

        // Otherwise, try main face AO
        else if (isFaceExposed(world, block, x, y, z, face)) {
            aoFace = face;
            aoHor = hor;
            aoVert = vert;
        }

        // If occluded try the horizontal side face AO
        else if (isFaceExposed(world, block, x, y, z, hor)) {
            aoFace = hor;
            aoHor = face;
            aoVert = vert;
        }

        // All faces are blocked, try stepping one block towards hor
        else {
            aoX += hor.offsetX;
            aoY += hor.offsetY;
            aoZ += hor.offsetZ;

            final Block horBlock = world.getBlock(x, y, z);

            // Try using the face AO from the block on the horizontal side
            if (isFaceExposed(world, horBlock, aoX, aoY, aoZ, face)) {
                aoFace = face;
                aoHor = hor.getOpposite(); // We've already shifted to the side, so invert horizontal axis
                aoVert = vert;
            }

            // All faces + horizontal diagonal are blocked, use vertical diagonal as last resort
            else {
                aoX += face.offsetX;
                aoY += face.offsetY;
                aoZ += face.offsetZ;

                aoFace = vert;

                // Apply direction shifts
                aoHor = hor.getOpposite();
                aoVert = face.getOpposite();
            }
        }

        if (shouldRenderBlockWithAO(block)) {
            getAOShadingInfoForBlockCorner(
                renderer,
                block,
                aoX,
                aoY,
                aoZ,
                aoFace,
                aoHor,
                aoVert,
                color,
                getColorMultiplierBySide(aoFace),
                shadingContainer);
        } else {
            getFlatShadingInfoForBlockFace(renderer, block, aoX, aoY, aoZ, aoFace, color, shadingContainer);
        }
    }

    public static void getAOShadingInfoForBlockCorner(RenderBlocks renderer, Block block, int x, int y, int z,
        ForgeDirection face, ForgeDirection hor, ForgeDirection vert, int color, float colorMultiplier,
        ShadingInfo container) {

        final IBlockAccess world = renderer.blockAccess;

        ForgeDirection swap;

        // UP or DOWN
        if (hor.offsetY != 0) {
            swap = hor;
            hor = vert;
            vert = swap;
        }

        // EAST or WEST
        else if (vert.offsetX != 0) {
            swap = hor;
            hor = vert;
            vert = swap;
        }

        // South
        final int faceX = x + face.offsetX;
        final int faceY = y + face.offsetY;
        final int faceZ = z + face.offsetZ;

        final int horX = faceX + hor.offsetX;
        final int horY = faceY + hor.offsetY;
        final int horZ = faceZ + hor.offsetZ;

        final int vertX = faceX + vert.offsetX;
        final int vertY = faceY + vert.offsetY;
        final int vertZ = faceZ + vert.offsetZ;

        final int faceBrightness = block.getMixedBrightnessForBlock(world, faceX, faceY, faceZ);
        final int horizontalBrightness = block.getMixedBrightnessForBlock(world, horX, horY, horZ);
        final int verticalBrightness = block.getMixedBrightnessForBlock(world, vertX, vertY, vertZ);

        final float faceAO = world.getBlock(faceX, faceY, faceZ)
            .getAmbientOcclusionLightValue();
        final float horizontalAO = world.getBlock(horX, horY, horZ)
            .getAmbientOcclusionLightValue();
        final float verticalAO = world.getBlock(vertX, vertY, vertZ)
            .getAmbientOcclusionLightValue();

        /// Diag
        final float cornerAO;
        final int cornerBrightness;

        // Is diagonal completely clear of blocks
        if (!(world.getBlock(horX + face.offsetX, horY + face.offsetY, horZ + face.offsetZ)
            .getCanBlockGrass()
            || world.getBlock(vertX + face.offsetX, vertY + face.offsetY, vertZ + face.offsetZ)
                .getCanBlockGrass())) {

            cornerAO = horizontalAO;
            cornerBrightness = horizontalBrightness;
        } else {
            final int diagX = horX + vert.offsetX;
            final int diagY = horY + vert.offsetY;
            final int diagZ = horZ + vert.offsetZ;

            cornerAO = world.getBlock(diagX, diagY, diagZ)
                .getAmbientOcclusionLightValue();
            cornerBrightness = block.getMixedBrightnessForBlock(world, diagX, diagY, diagZ);
        }

        float ao = colorMultiplier * (faceAO + horizontalAO + verticalAO + cornerAO) / 4.0F;
        final int brightness = renderer
            .getAoBrightness(horizontalBrightness, cornerBrightness, verticalBrightness, faceBrightness);

        container.updateValues(brightness, color, ao);
    }

    public static void getFlatShadingInfoForBlockFace(RenderBlocks renderer, Block block, int x, int y, int z,
        ForgeDirection face, int color, ShadingInfo shadingContainer) {

        final float colorMultiplier = getColorMultiplierBySide(face);

        final int brightness = block
            .getMixedBrightnessForBlock(renderer.blockAccess, x + face.offsetX, y + face.offsetY, z + face.offsetZ);

        shadingContainer.updateValues(brightness, color, colorMultiplier);
    }

    public static boolean isFaceExposed(IBlockAccess world, Block block, int x, int y, int z, ForgeDirection face) {
        final int meta = world.getBlockMetadata(x, y, z);

        final int sideX = x + face.offsetX;
        final int sideY = y + face.offsetY;
        final int sideZ = z + face.offsetZ;

        final Block side = world.getBlock(sideX, sideY, sideZ);
        final int sideMeta = world.getBlockMetadata(sideX, sideY, sideZ);

        // Round logs don't block side faces
        if (Configs.RoundLogs.enabled && Configs.Blocks.logs.matches(side, sideMeta)) {
            return false;
        }

        // Both are leaf blocks
        if (Configs.ExtraLeaves.enabled && (Configs.Blocks.extraLeaves.matches(block, meta)
            && Configs.Blocks.extraLeaves.matches(side, sideMeta))) {
            return false;
        }

        return !side.isOpaqueCube();
    }

    /// Rotates a point in 3D space 90 degrees counter-clockwise along an axis
    public static void rotateCounterclock(ForgeDirection rotationAxis, double axisX, double axisY, double axisZ,
        double[] xyz) {
        double centerA, centerB;
        double a, b;
        float rotSin, rotCos;

        switch (rotationAxis) {
            // Constant Y (XZ)
            case DOWN -> {
                centerA = axisX;
                centerB = axisZ;
                a = xyz[0];
                b = xyz[2];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;

            }
            case UP -> {
                centerA = axisX;
                centerB = axisZ;
                a = xyz[0];
                b = xyz[2];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            // Constant Z (XY)
            case NORTH -> {
                centerA = axisX;
                centerB = axisY;
                a = xyz[0];
                b = xyz[1];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;
            }
            case SOUTH -> {
                centerA = axisX;
                centerB = axisY;
                a = xyz[0];
                b = xyz[1];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            // Constant X (YZ)
            case WEST -> {
                centerA = axisY;
                centerB = axisZ;
                a = xyz[1];
                b = xyz[2];
                rotSin = COUNTERCLOCK_SIN;
                rotCos = COUNTERCLOCK_COS;
            }
            case EAST -> {
                centerA = axisY;
                centerB = axisZ;
                a = xyz[1];
                b = xyz[2];
                rotSin = CLOCKWISE_SIN;
                rotCos = CLOCKWISE_COS;
            }

            default -> {
                return;
            }
        }

        double deltaA = a - centerA;
        double deltaB = b - centerB;

        a = (rotCos * deltaA) + (rotSin * deltaB) + centerA;
        b = (rotCos * deltaB) - (rotSin * deltaA) + centerB;

        switch (rotationAxis) {
            // Constant Y (XZ)
            case DOWN, UP -> {
                xyz[0] = a;
                xyz[2] = b;
            }

            // Constant Z (XY)
            case NORTH, SOUTH -> {
                xyz[0] = a;
                xyz[1] = b;
            }

            // Constant X (YZ)
            case WEST, EAST -> {
                xyz[1] = a;
                xyz[2] = b;
            }
        }
    }
}
