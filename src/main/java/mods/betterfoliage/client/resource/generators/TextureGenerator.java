package mods.betterfoliage.client.resource.generators;

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
import mods.betterfoliage.client.render.TextureProvider;

public abstract class TextureGenerator implements TextureProvider, IResourcePack {

    private final String name;
    private final String description;
    private final Set<String> domain;

    protected TextureGenerator(String name, String description, String domain) {
        this.name = name;
        this.description = description;
        this.domain = ImmutableSet.of(BetterFoliageMod.MOD_ID.toLowerCase() + "_" + domain);

        // Add self to the list of default resource packs
        FMLClientHandler.instance().resourcePackList.add(this);
    }

    protected abstract void generateTextures();

    protected abstract BufferedImage getGeneratedTexture(ResourceLocation location);

    @Override
    public final InputStream getInputStream(ResourceLocation location) throws IOException {
        BufferedImage image = getGeneratedTexture(location);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outStream);
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    @Override
    public final Set<String> getResourceDomains() {
        return domain;
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
}
