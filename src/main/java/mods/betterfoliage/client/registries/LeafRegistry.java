package mods.betterfoliage.client.registries;

import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.client.render.RenderUtils;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.client.resource.generators.TextureGenerator;

@SideOnly(Side.CLIENT)
public final class LeafRegistry extends TextureGenerator {

    private static final ResourceLocation maskMappings = new ResourceLocation(
        BetterFoliageMod.DOMAIN,
        "leafMaskTextureMappings.cfg");

    private static final ResourceLocation particleMappings = new ResourceLocation(
        BetterFoliageMod.DOMAIN,
        "leafParticleTextureMappings.cfg");

    private static LeafRegistry instance = null;
    private static SpriteTypeMap maskTypes;
    private static SpriteTypeMap particleTypes;

    private final Map<IIcon, LeafInfo> leaves = new HashMap<>();

    public static LeafRegistry getInstance() {
        if (instance == null) {
            instance = new LeafRegistry();
        }

        return instance;
    }

    private LeafRegistry() {
        super("Generated Round Leaves and Particles", "Round leaves and leaf particles generator", "gen_leaves");
    }

    public LeafInfo getLeafForBlock(IBlockAccess world, int x, int y, int z) {
        return getLeafForBlock(world, x, y, z, 0);
    }

    public LeafInfo getLeafForBlock(IBlockAccess world, int x, int y, int z, int side) {
        final IIcon sprite = world.getBlock(x, y, z)
            .getIcon(world, x, y, z, side);
        return getLeafForSprite(sprite);
    }

    public LeafInfo getLeafForSprite(IIcon sprite) {
        return leaves.getOrDefault(sprite, null);
    }

    @Override
    protected void onSpriteStitch(TextureMap atlas) {
        if (atlas.getTextureType() != 0) return;

        maskTypes = new SpriteTypeMap(maskMappings);
        particleTypes = new SpriteTypeMap(particleMappings);

        LeafInfo.clearRegistries();
        leaves.clear();

        for (Object obj : Block.blockRegistry) {
            if (!(obj instanceof Block block)) continue;
            if (!Config.blocks.INSTANCE.getLeaves()
                .matchesClass(block)) continue;

            block.registerBlockIcons(location -> {
                IIcon original = atlas.getTextureExtry(location);
                BetterFoliageMod.log.info("Registering Particles and Extra Leaf textures for {}", location);

                registerLeaf(atlas, original);

                return original;
            });
        }

    }

    private void registerLeaf(TextureMap atlas, IIcon baseLeafSprite) {

        String maskType = maskTypes.getSpriteType(baseLeafSprite, "default");
        String particleType = particleTypes.getSpriteType(baseLeafSprite, "default");

        LeafInfo leaf = new LeafInfo(atlas, baseLeafSprite, domain, maskType, particleType);
        leaves.put(baseLeafSprite, leaf);
    }

    @Override
    protected BufferedImage getGeneratedTexture(ResourceLocation location) throws IOException {
        LeafInfo leaf = null;
        for (LeafInfo leafInfo : leaves.values()) {
            if (!location.equals(leafInfo.generatedLeafResource)) continue;
            leaf = leafInfo;
            break;
        }

        if (leaf == null) {
            throw new IOException("Resource " + location + " is not handled by this generator!");
        }

        BufferedImage baseImage = ImageIO.read(
            ResourceUtils.getResource(leaf.baseLeafResource)
                .getInputStream());

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        int frames = height / width;

        int genWidth = width * 2;

        IResource leafMask = getBiggestAvailableLeafMask(leaf.maskType, genWidth);

        BufferedImage maskImage = ImageIO.read(leafMask.getInputStream());
        if (maskImage.getWidth() != genWidth) {
            maskImage = createScaledCopy(maskImage, genWidth, genWidth, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        }

        maskImage = convertToType(maskImage, BufferedImage.TYPE_INT_ARGB);

        BufferedImage genImage = new BufferedImage(genWidth, height * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = genImage.createGraphics();

        for (int frame = 0; frame < frames; frame++) {
            BufferedImage baseSubFrame = baseImage.getSubimage(0, height * frame, width, height);

            // Copy base image to the four quadrants of the generated texture
            graphics.drawImage(baseSubFrame, 0, 0, width, height, null);
            graphics.drawImage(baseSubFrame, width, 0, width, height, null);
            graphics.drawImage(baseSubFrame, 0, height, width, height, null);
            graphics.drawImage(baseSubFrame, width, height, width, height, null);

            // Multiply alpha mask
            for (int x = 0; x < maskImage.getWidth(); x++) {
                for (int y = 0; y < maskImage.getHeight(); y++) {
                    int baseY = y + (frame * genWidth);
                    int baseColor = genImage.getRGB(x, baseY);
                    int maskColor = maskImage.getRGB(x, y);
                    genImage.setRGB(x, baseY, RenderUtils.multiplyAlphas(baseColor, maskColor));
                }
            }
        }

        graphics.dispose();
        return genImage;
    }

    private IResource getBiggestAvailableLeafMask(String maskType, int baseSize) throws IOException {
        int maxMaskSize = MathHelper.roundUpToPowerOfTwo(baseSize);
        while (maxMaskSize > 0) {
            ResourceLocation maskLocation = new ResourceLocation(
                BetterFoliageMod.DOMAIN,
                "textures/blocks/leafmask_" + maxMaskSize + "_" + maskType + ".png");

            if (ResourceUtils.resourceExists(maskLocation)) {
                return ResourceUtils.getResource(maskLocation);
            }

            maxMaskSize /= 2;
        }

        throw new IOException("No leaf mask available for size " + baseSize);
    }

    @Override
    protected InputStream getGeneratedMcMeta(ResourceLocation location) throws IOException {
        ResourceLocation nonMeta = ResourceUtils.getBaseForMcMeta(location);
        for (LeafInfo leaf : leaves.values()) {
            if (!nonMeta.equals(leaf.generatedLeafResource)) continue;
            return ResourceUtils
                .getResource(
                    new ResourceLocation(leaf.baseLeafResource.getResourceDomain(), location.getResourcePath()))
                .getInputStream();
        }

        throw new IOException("None of the generated leaves uses the .mcmeta file at: " + location);
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        boolean isMcMeta = ResourceUtils.isMcMeta(location);

        for (LeafInfo leaf : leaves.values()) {
            if (isMcMeta) {
                if (ResourceUtils.resourceHasMcMeta(leaf.baseLeafResource)) return true;
                continue;
            }

            if (location.equals(leaf.generatedLeafResource)) return true;
        }

        return false;
    }
}
