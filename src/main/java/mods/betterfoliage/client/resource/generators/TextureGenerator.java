package mods.betterfoliage.client.resource.generators;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.client.FMLClientHandler;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.client.resource.StitchListener;

public abstract class TextureGenerator extends StitchListener implements IResourcePack {

    private final String name;
    private final String description;

    protected final String domain;

    protected TextureGenerator(String name, String description, String domain) {
        this(name, description, domain, true);
    }

    protected TextureGenerator(String name, String description, String domain, boolean selfRegister) {
        super(selfRegister);
        this.name = name;
        this.description = description;
        this.domain = BetterFoliageMod.DOMAIN + "_" + domain;

        // Add self to the list of default resource packs
        FMLClientHandler.instance().resourcePackList.add(this);
    }

    protected abstract BufferedImage getGeneratedTexture(ResourceLocation location) throws IOException;

    protected abstract InputStream getGeneratedMcMeta(ResourceLocation location) throws IOException;

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        if (ResourceUtils.isMcMeta(location)) {
            return getGeneratedMcMeta(location);
        }

        BufferedImage image = getGeneratedTexture(location);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outStream);
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    @Override
    public final Set<String> getResourceDomains() {
        return ImmutableSet.of(domain);
    }

    @Override
    public final IMetadataSection getPackMetadata(IMetadataSerializer serializer, String type) {
        if (!type.equals("pack")) {
            return null;
        }

        return new PackMetadataSection(new ChatComponentText("[" + BetterFoliageMod.MOD_NAME + "] " + description), 1);
    }

    @Override
    public final BufferedImage getPackImage() {
        return null;
    }

    @Override
    public final String getPackName() {
        return "[" + BetterFoliageMod.MOD_NAME + "] " + name;
    }

    protected final BufferedImage createEmptyFrom(BufferedImage original) {
        return new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    protected final BufferedImage convertToType(BufferedImage original, int imageType) {
        if (original.getType() == imageType) return original;

        BufferedImage res = new BufferedImage(original.getWidth(), original.getHeight(), imageType);

        Graphics2D graphics = res.createGraphics();
        graphics.drawImage(original, 0, 0, null);

        graphics.dispose();
        return res;
    }

    protected final BufferedImage createScaledCopy(BufferedImage original, int targetWidth, int targetHeight,
        int scalingType) {
        double widthFactor = (double) targetWidth / (double) original.getWidth();
        double heightFactor = (double) targetHeight / (double) original.getHeight();

        BufferedImage copyImage = new BufferedImage(targetWidth, targetHeight, original.getType());

        AffineTransform scalerTransform = new AffineTransform();
        scalerTransform.scale(widthFactor, heightFactor);
        AffineTransformOp scaleOperation = new AffineTransformOp(scalerTransform, scalingType);
        return scaleOperation.filter(original, copyImage);
    }
}
