package mods.betterfoliage.client.resource.generators;

import java.awt.Graphics2D;
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
import mods.betterfoliage.client.render.ISpriteProvider;
import mods.betterfoliage.client.resource.ResourceUtils;
import mods.betterfoliage.client.resource.StitchListener;

public abstract class TextureGenerator extends StitchListener implements IResourcePack, ISpriteProvider {

    private final String name;
    private final String description;

    protected final String domain;

    protected TextureGenerator(String name, String description, String domain) {
        super();
        this.name = name;
        this.description = description;
        this.domain = BetterFoliageMod.MOD_ID.toLowerCase() + "_" + domain;

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

    protected final BufferedImage createCopy(BufferedImage original) {
        BufferedImage res = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        Graphics2D graphics = res.createGraphics();
        graphics.drawImage(original, 0, 0, null);

        return res;
    }

    protected final void debugPaintRed(BufferedImage image) {
        // Colors are in the TYPE_INT_ARGB format
        final int alphaMask = 0xFF_00_00_00;
        // final int color = 0x00_FF_00_00; // RED
        final int color = 0x00_00_00_FF; // BLUE

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int originalAlpha = image.getRGB(x, y) & alphaMask;
                image.setRGB(x, y, color | originalAlpha);
            }
        }
    }
}
